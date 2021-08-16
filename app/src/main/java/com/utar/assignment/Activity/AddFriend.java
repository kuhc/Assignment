package com.utar.assignment.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.utar.assignment.Model.Amount;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.FirebaseCallback;
import com.utar.assignment.Util.FirestoreHelper;
import com.utar.assignment.Util.GeneralHelper;

import java.util.ArrayList;
import java.util.List;

public class AddFriend extends AppCompatActivity {

    private static FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        EditText userEmail = findViewById(R.id.friendEmail);
        Button conformAddFriend = findViewById(R.id.conformAddFriend);

        List<String> emailList = new ArrayList<>();
        FirebaseUser user;
        user = fAuth.getInstance().getCurrentUser();
        String oriUserEmail = user.getEmail();
        String uid = user.getUid();

        FirestoreHelper.getUser(uid, new FirebaseCallback() {
            @Override
            public void onResponse(Object object) {
                User userInfo;
                userInfo = (User)object;
                List<String> existingFriend = userInfo.getFriendList();
                if(existingFriend != null) {
                    emailList.addAll(existingFriend);
                }
            }
        });

        conformAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();


                if (email.length()!=0) {
                    fAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.getResult().getSignInMethods().size() == 0) {
                                GeneralHelper.showMessage(AddFriend.this, "The email does not exist");
                                userEmail.getText().clear();
                            }
                            else if (emailList.contains(email))
                            {
                                GeneralHelper.showMessage(AddFriend.this, "The email is already in the friends list!");
                                userEmail.getText().clear();
                            }

                            else if(email.equals(oriUserEmail))
                                {
                                    GeneralHelper.showMessage(AddFriend.this, "You cannot type in your email!");
                                    userEmail.getText().clear();
                                }
                                else {
                                    emailList.add(email);

                                    FirestoreHelper.addFriend(uid, emailList, new FirebaseCallback() {
                                        @Override
                                        public void onResponse() {
                                            GeneralHelper.showMessage(AddFriend.this, "Successfully registered!");
                                            Intent intent = new Intent(AddFriend.this,MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                               fStore.collection("Users").whereEqualTo("email", email)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                   @Override
                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                       User user2 = task.getResult().toObjects(User.class).get(0);
                                       List<String> emailList2 = new ArrayList<>();
                                       List<String> existingFriend = user2.getFriendList();
                                       if(existingFriend != null) {
                                           emailList2.addAll(existingFriend);
                                       }
                                       emailList2.add(user.getEmail());
                                       FirestoreHelper.addFriend(user2.getUid(), emailList2, new FirebaseCallback() {
                                           @Override
                                           public void onResponse() {
                                               GeneralHelper.showMessage(AddFriend.this, "THis is emailList: "+ emailList2);
                                               Intent intent = new Intent(AddFriend.this,MainActivity.class);
                                               startActivity(intent);
                                           }
                                       });
                                   }
                               });

                                    userEmail.getText().clear();
                                }
                            }
                    });
                }
                else
                    GeneralHelper.showMessage(AddFriend.this, "Please type in the email");
            }
        });

    }
}