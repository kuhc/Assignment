package com.utar.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utar.assignment.Model.Friend;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.FirebaseCallback;
import com.utar.assignment.Util.FirestoreHelper;
import com.utar.assignment.Util.GeneralHelper;

import java.util.List;

public class AddFriend extends AppCompatActivity {

    private static FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        EditText userEmail = findViewById(R.id.friendEmail);
        Button conformAddFriend = findViewById(R.id.conformAddFriend);


        FirebaseUser user;
        user = fAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
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
                            } else {
                                //GeneralHelper.showMessage(AddFriend.this, "The email does exist");
                                List<String> emailList = null;
                                emailList.add(email);
                                Friend friend = new Friend();
                                friend.setId(uid);
                                friend.setFriendEmail(emailList);

                               FirestoreHelper.addFriend(friend, new FirebaseCallback() {
                                    @Override
                                    public void onResponse() {
                                        GeneralHelper.showMessage(AddFriend.this, "Successfully registered!");
                                        //progressBar.setVisibility(View.INVISIBLE);
                                        //btnRegister.setVisibility(View.VISIBLE);
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