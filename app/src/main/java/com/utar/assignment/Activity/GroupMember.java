package com.utar.assignment.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.utar.assignment.Model.Group;
import com.utar.assignment.Model.User;

import java.util.ArrayList;
import java.util.List;

public class GroupMember extends AppCompatActivity {
    FirebaseFirestore db;
    List<User> userList;
    FirebaseAuth fAuth;
    List<CheckBox> cbs;
    List<String> user_id;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        user = new User();
        cbs = new ArrayList<CheckBox>();
        user_id = new ArrayList<>();
        LinearLayout myOwnLayout = new LinearLayout(this);
        myOwnLayout.setOrientation(LinearLayout.VERTICAL);

        db.collection("Users").document(fAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        user = task.getResult().toObject(User.class);

                        db.collection("Users").whereIn("email", user.getFriendList())
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                List<User> userList2 = new ArrayList<>();
                                userList2 = task.getResult().toObjects(User.class);

                                int i = 0;
                                for (User x : userList2) {
                                    CheckBox cb = new CheckBox(GroupMember.this);
                                    cb.setText(x.getUsername());
                                    user_id.add(x.getUid());
                                    cbs.add(cb); // add into the list
                                    myOwnLayout.addView(cbs.get(i)); // 2) add each checkbox to the layout
                                    i++;
                                }

                            }
                        });
                    }
                });


        Button button_addFriend = new Button(this);
        button_addFriend.setText("Add member");
        button_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Group newgroup = new Group(getIntent().getStringExtra("g_name"));

                db.collection("Group_1").document(newgroup.getGroupId()).set(newgroup).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(GroupMember.this, " updated", Toast.LENGTH_SHORT).show();

                    }
                });

                db.collection("Users").document(fAuth.getUid()).update("groupList", FieldValue.arrayUnion(newgroup.getGroupId()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                for (int i = 0; i < cbs.size(); i++) {
                    if (cbs.get(i).isChecked()) {

                        db.collection("Users").document(user_id.get(i)).update("groupList", FieldValue.arrayUnion(newgroup.getGroupId()))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                    }
                }

                Intent intent = new Intent(GroupMember.this, specified_group.class);
                intent.putExtra("g_id", newgroup.getGroupId());
                intent.putExtra("name", getIntent().getStringExtra("g_name"));
                startActivity(intent);
                
            }

        });

        myOwnLayout.addView(button_addFriend);
        setContentView(myOwnLayout);

    }
}