package com.utar.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.utar.assignment.Model.Group;
import com.utar.assignment.Model.MainActivity;
import com.utar.assignment.R;
import com.utar.assignment.Util.group_adapter;
import com.utar.assignment.Util.main_act_adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class specified_group extends AppCompatActivity {
    RecyclerView recycle_main_activity1;
    Button leavebut1;
FirebaseFirestore db;
FirebaseAuth fAuth;
TextView group_name;
Button hp_button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specified_group);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        leavebut1=findViewById(R.id.leave_but);
        hp_button1=findViewById(R.id.home_but);
        group_name=findViewById(R.id.specified_group_name);
        recycle_main_activity1=findViewById(R.id.recycle_main_activity);



group_name.setText(getIntent().getStringExtra("name"));

        hp_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(specified_group.this, com.utar.assignment.Activity.MainActivity.class);

                startActivity(intent);
            }
        });
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


db.collection("Group_1").document(getIntent().getStringExtra("g_id")).get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull  Task<DocumentSnapshot> task) {
                Group group = task.getResult().toObject(Group.class);
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if(group.getMainActivityList()!=null)
                {
                    List<MainActivity> mainact = group.getMainActivityList();

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(specified_group.this);
                    recycle_main_activity1.setLayoutManager(linearLayoutManager);
                    main_act_adapter adapter= new main_act_adapter( specified_group.this,mainact,getIntent().getStringExtra("g_id"));
                    recycle_main_activity1.setAdapter(adapter);
                }



            }
        });




    }
}