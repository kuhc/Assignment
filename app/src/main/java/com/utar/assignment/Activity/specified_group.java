package com.utar.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.utar.assignment.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class specified_group extends AppCompatActivity {

    Button leavebut1;
FirebaseFirestore db;
FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specified_group);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        leavebut1=findViewById(R.id.leave_but);
        leavebut1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIntent().getStringExtra("g_id");
                db.collection("Users").document(fAuth.getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull  Task<DocumentSnapshot> task) {

                        List<String> groupidList = (List<String>)  task.getResult().get("groupList");
                        groupidList.remove(getIntent().getStringExtra("g_id"));


                        Toast.makeText(specified_group.this, groupidList.toString(), Toast.LENGTH_SHORT).show();
                        Map<String,Object> updates=new HashMap<>();
                        updates.put("groupList",FieldValue.delete());
db.collection("Users").document(fAuth.getUid()).update(updates)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {

            }
        });


        Map<String,Object> data = new HashMap<>();
        data.put("groupList",groupidList);
        db.collection("Users").document(fAuth.getUid()).set(data, SetOptions.merge());












                    }
                });
            }
        });
    }
}