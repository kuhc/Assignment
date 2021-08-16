package com.utar.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.utar.assignment.Util.GeneralHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class sub_activity extends AppCompatActivity {
FirebaseFirestore db;
TextView amountvalue;
String  test;
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
       // amountvalue=findViewById(R.id.amount_value);
sublv=findViewById(R.id.sub_listview);
       String get_id= getIntent().getStringExtra("main_id");
       String get_group_id=getIntent().getStringExtra("group_id");
        //Toast.makeText(sub_activity.this, get_id , Toast.LENGTH_SHORT).show();

        //Toast.makeText(sub_activity.this, get_group_id , Toast.LENGTH_SHORT).show();

//findname("6qUiRr5ZzKPafTlZ08jp88zDAnL2");
//Toast.makeText(sub_activity.this,  findname("6qUiRr5ZzKPafTlZ08jp88zDAnL2"), Toast.LENGTH_SHORT).show();
db.collection("Group_1").document(get_group_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    @Override
    public void onComplete(@NonNull  Task<DocumentSnapshot> task) {
        group=task.getResult().toObject(Group.class);
 //       group.getMainActivityList().get(3).getId();
//        int x =  group.getMainActivityList().size();
//        String y = String.valueOf(x);
//        Toast.makeText(sub_activity.this,  y , Toast.LENGTH_SHORT).show();
        //Toast.makeText(sub_activity.this,  findname("6qUiRr5ZzKPafTlZ08jp88zDAnL2"), Toast.LENGTH_SHORT).show();

        for(int i =0;i<group.getMainActivityList().size();i++)
        {
            if( group.getMainActivityList().get(i).getId().equals(get_id))
            {
               List<SubActivity> sub = group.getMainActivityList().get(i).getSubActivityList();

                //amountvalue.setText(sub.indexOf(0));
//                Toast.makeText(sub_activity.this,  sub.get(0).getOwnerId(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(sub_activity.this,  sub.get(1).getOwnerId(), Toast.LENGTH_SHORT).show();
//                int x =  sub.size();
//       String y = String.valueOf(x);
//                Toast.makeText(sub_activity.this,  y, Toast.LENGTH_SHORT).show();

                idList = new ArrayList<>();
                for(int z=0;z<sub.size();z++)
                {
 // Toast.makeText(sub_activity.this,  sub.get(z).getPayerId()+" --- OWN --- "+sub.get(z).getOwnerId() +" "+sub.get(z).getAmount(), Toast.LENGTH_SHORT).show();
                    idList.add(sub.get(z).getPayerId());
                    idList.add(sub.get(z).getOwnerId());



                    //  sub.get(z).getPayerId();

//db.collection("Users").document(sub.get(z).getPayerId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//    @Override
//    public void onComplete(@NonNull  Task<DocumentSnapshot> task) {
//
//task.getResult().get("username").toString();
//
//
//
//
//
//
//
//
////        Toast.makeText(sub_activity.this,  task.getResult().get("username").toString(), Toast.LENGTH_SHORT).show();
////        Toast.makeText(sub_activity.this,  "1", Toast.LENGTH_SHORT).show();
//    }
//});
//
//db.collection("Users").document(sub.get(z).getOwnerId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//    @Override
//    public void onComplete(@NonNull  Task<DocumentSnapshot> task) {
//        task.getResult().get("username");
//        Toast.makeText(sub_activity.this,  task.getResult().get("username").toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(sub_activity.this,  "2", Toast.LENGTH_SHORT).show();
//    }
//});


                }

                iterator = idList.iterator();

                nameList = new ArrayList<>();

                findname(iterator.next());


            }
        }
    }
});

    }

    public void findname(String id)
    {
        FirestoreHelper.getUser(id, new FirebaseCallback() {
            @Override
            public void onResponse(Object object) {
                User user = (User)object;
                nameList.add(user.getUsername());

                if(iterator.hasNext())
                findname(iterator.next());
                else {
                    display();
                }
            }
        });
    }

    private void display() {
        Iterator nameIterator = nameList.iterator();
ArrayList<String> display= new ArrayList<>();

        for (int i = 0; i < group.getMainActivityList().size(); i++) {
            if (group.getMainActivityList().get(i).getId().equals(getIntent().getStringExtra("main_id"))) {
                List<SubActivity> subList = group.getMainActivityList().get(i).getSubActivityList();

                for(SubActivity sub : subList) {
                    display.add(nameIterator.next()+ " OWN " + nameIterator.next() + " RM " + sub.getAmount());
                    //Toast.makeText(this, nameIterator.next() + "---" + nameIterator.next() + "---" + sub.getAmount(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1,display
        );
sublv.setAdapter(adapter);
    }


}