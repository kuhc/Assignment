package com.utar.assignment.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utar.assignment.Model.Group;
import com.utar.assignment.Model.SubActivity;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.FirebaseCallback;
import com.utar.assignment.Util.FirestoreHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class sub_activity extends AppCompatActivity {
    FirebaseFirestore db;
    List<String> idList;
    Iterator<String> iterator;
    List<String> nameList;
    Group group;
    ListView sublv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        db = FirebaseFirestore.getInstance();
        sublv = findViewById(R.id.sub_listview);
        String get_id = getIntent().getStringExtra("main_id");
        String get_group_id = getIntent().getStringExtra("group_id");

        db.collection("Group_1").document(get_group_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                group = task.getResult().toObject(Group.class);

                for (int i = 0; i < group.getMainActivityList().size(); i++) {
                    if (group.getMainActivityList().get(i).getId().equals(get_id)) {
                        List<SubActivity> sub = group.getMainActivityList().get(i).getSubActivityList();

                        idList = new ArrayList<>();
                        for (int z = 0; z < sub.size(); z++) {

                            idList.add(sub.get(z).getOwnerId());
                            idList.add(sub.get(z).getPayerId());

                        }

                        iterator = idList.iterator();

                        nameList = new ArrayList<>();

                        findname(iterator.next());


                    }
                }
            }
        });

    }

    public void findname(String id) {
        FirestoreHelper.getUser(id, new FirebaseCallback() {
            @Override
            public void onResponse(Object object) {
                User user = (User) object;
                nameList.add(user.getUsername());

                if (iterator.hasNext())
                    findname(iterator.next());
                else {
                    display();
                }
            }
        });
    }

    private void display() {
        Iterator nameIterator = nameList.iterator();
        ArrayList<String> display = new ArrayList<>();

        for (int i = 0; i < group.getMainActivityList().size(); i++) {
            if (group.getMainActivityList().get(i).getId().equals(getIntent().getStringExtra("main_id"))) {
                List<SubActivity> subList = group.getMainActivityList().get(i).getSubActivityList();

                for (SubActivity sub : subList) {
                    display.add(nameIterator.next() + " OWN " + nameIterator.next() + " RM " + sub.getAmount());
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, display
        );
        sublv.setAdapter(adapter);
    }


}