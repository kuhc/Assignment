package com.utar.assignment.Util;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class StorageHelper {
    private static FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public static void getProfilePictureUri(String UserId, FirebaseCallback callback) {
        storageReference.child("users/" + UserId + "/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                callback.onResponse(uri);
            }
        });
    }

    public static void uploadImage(String child, Uri uri, FirebaseCallback callback) {
        StorageReference ref = storageReference.child(child);
        ref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                       callback.onResponse(uri);
                    }
                });
            }
        });
    }

    public static void uploadImageByData(String child, byte[] data, FirebaseCallback callback) {
        StorageReference ref = storageReference.child(child).child(new Date().toString());
        ref.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        callback.onResponse(uri);
                    }
                });
            }
        });
    }
}
