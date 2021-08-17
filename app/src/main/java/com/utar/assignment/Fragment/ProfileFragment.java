package com.utar.assignment.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.utar.assignment.Activity.LoginActivity;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.FirebaseCallback;
import com.utar.assignment.Util.FirestoreHelper;
import com.utar.assignment.Util.StorageHelper;

import java.io.ByteArrayOutputStream;


public class ProfileFragment extends Fragment {

    private final static int CAMERA_REQUEST_CODE = 1000;
    private final static int GALLERY_REQUEST_CODE = 1001;

    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private TextView txtName, txtEmail;
    private ImageView imgProfile;
    private Button btnLogout;
    private User user;
    private ProgressDialog pd;
    private ConstraintLayout loadingPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fUser = fAuth.getInstance().getCurrentUser();

        txtName = view.findViewById(R.id.profile_txtName);
        txtEmail = view.findViewById(R.id.profile_txtEmail);
        imgProfile = view.findViewById(R.id.profile_imgProfile);
        btnLogout = view.findViewById(R.id.profile_btnLogout);
        loadingPage = view.findViewById(R.id.profile_loadingPage);

        // Load User Profile
        setUpUserProfile();

        // Profile Picture Menu
        createProfilePictureMenu();
        
        // set Logout Listener
        setBtnLogoutListener();

        return view;
    }

    private void setBtnLogoutListener() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                //finish
                getActivity().finishAffinity();
                //Intent intent = new Intent(getActivity(), LoginActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.putExtra("EXIT", true);
                //getActivity().startActivity(intent);
            }
        });
    }

    private void createProfilePictureMenu() {
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.profile_picture_menu, popup.getMenu());
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.profile_picture_option_gallery:
                                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(openGalleryIntent, GALLERY_REQUEST_CODE);
                                break;
                            case R.id.profile_picture_option_camera:
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                        }
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri, null);
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] photoData = baos.toByteArray();
            uploadImageToFirebase(null, photoData);
        }
    }

    private void uploadImageToFirebase(Uri imageUri, byte[] photoData) {

        // Upload Image to Firebase Storage
        pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading Profile Picture...");
        pd.show();

        String child = "users/" + fUser.getUid() + "/profile_picture.jpg";

        if(photoData == null)
            StorageHelper.uploadImage(child, imageUri, new FirebaseCallback() {
                @Override
                public void onResponse(Uri uri) {
                    Picasso.get().load(uri).into(imgProfile);
                    pd.dismiss();
                }
            });
        else
            StorageHelper.uploadImageByData(child, photoData, new FirebaseCallback() {
                @Override
                public void onResponse(Uri uri) {
                    Picasso.get().load(uri).into(imgProfile);
                    pd.dismiss();
                }
            });
    }

    private void setUpUserProfile() {
        FirestoreHelper.getUser(fUser.getUid(), new FirebaseCallback() {
            @Override
            public void onResponse(Object object) {
                user = (User)object;
                txtName.setText(user.getUsername());
                txtEmail.setText(user.getEmail());

                loadingPage.setVisibility(View.INVISIBLE);

                // Load Profile Picture
                StorageHelper.getProfilePictureUri(fUser.getUid(), new FirebaseCallback() {
                    @Override
                    public void onResponse(Uri uri) {
                        Picasso.get().load(uri).into(imgProfile);
                    }
                });
            }
        });
    }
}