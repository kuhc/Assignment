package com.utar.assignment.Fragment;
//Toast.makeText(getActivity(),  x.getGroupList().toString(), Toast.LENGTH_SHORT).show();
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

//import com.utar.assignment.Activity.add_group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.utar.assignment.Activity.new_member_group;
import com.utar.assignment.Model.Group;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.GeneralHelper;
import com.utar.assignment.Util.group_adapter;
import com.utar.assignment.Util.member_adapter;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {
Button new_group_but1;
    FirebaseFirestore db;
    RecyclerView recycle_groupname1;
    List<Group> groupList;
    List<User> userList;
    FirebaseAuth fAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
       // GeneralHelper.showMessage(getActivity(),"This is Group");
        View view= inflater.inflate(R.layout.fragment_group, container, false);
        userList = new ArrayList<>();
        fAuth = FirebaseAuth.getInstance();
        new_group_but1=view.findViewById(R.id.new_group_but);
        recycle_groupname1=view.findViewById(R.id.recycle_groupname);
        db = FirebaseFirestore.getInstance();
        groupList= new ArrayList<>();
        //Toast.makeText(getActivity(), fAuth.getUid(), Toast.LENGTH_SHORT).show();

        //fAuth.getUid();
        new_group_but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent intent = new Intent(getActivity(), add_group.class);
                Intent intent = new Intent(getActivity(), new_member_group.class);
                GroupFragment.this.startActivity(intent);
            }
        });

db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
    @Override
    public void onComplete(@NonNull  Task<QuerySnapshot> task) {
        userList=task.getResult().toObjects(User.class);
        for (User x : userList)
        {//found me
            if(x.getUid().equals(fAuth.getUid()))
            {

                if(x.getGroupList()==null ||x.getGroupList().isEmpty())
                {
                    Toast.makeText(getActivity(),  "Dont have group yet", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    db.collection("Group_1").whereIn("groupId",x.getGroupList()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull  Task<QuerySnapshot> task) {
                            groupList=task.getResult().toObjects(Group.class);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            recycle_groupname1.setLayoutManager(linearLayoutManager);
                            group_adapter adapter= new group_adapter(groupList, getActivity());
                            recycle_groupname1.setAdapter(adapter);
                        }
                    });
                }


            }
        }
    }
});






        return view;
    }
}