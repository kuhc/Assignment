package com.utar.assignment.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.utar.assignment.Activity.AddExpenses;
import com.utar.assignment.Model.Amount;
import com.utar.assignment.Model.Group;
import com.utar.assignment.Model.MainActivity;
import com.utar.assignment.Model.SubActivity;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.FirebaseCallback;
import com.utar.assignment.Util.FirestoreHelper;
import com.utar.assignment.Util.GeneralHelper;
import com.utar.assignment.Util.SplitCalHelper;
import com.utar.assignment.Util.friend_adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment  extends Fragment {

    //variable
    private TextView overall,owe;
    private User userInfo;
    double amount;
    private ProgressBar pb;
    String temp_username;


    //firebase
    FirebaseFirestore db;
    private FirebaseAuth Auth;

    //model
    private List<Group> group_list = new ArrayList<>();
    private List<MainActivity> mainActivity_list_all = new ArrayList<>();
    private ArrayList<Amount> amount_list =new ArrayList<>();
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        overall = view.findViewById(R.id.home_overall_amount);
        owe = view.findViewById(R.id.home_owe);
        pb = view.findViewById(R.id.progressBar2);


        //done implement progress bar
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pb.setVisibility(View.VISIBLE);

        FirebaseUser user;
        user = Auth.getInstance().getCurrentUser();
        String uid = user.getUid();



        //get user overall amount
        db = FirebaseFirestore.getInstance();
        DocumentReference documentReference =db.collection("Users").document(uid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM");

                userInfo = documentSnapshot.toObject(User.class);
                if (userInfo.getAmountList()==null){
                    userInfo.setAmountList(new ArrayList<Amount>());
                }

                for(int i = 0;i<userInfo.getAmountList().size();i++){

                    amount_list.add(userInfo.getAmountList().get(i));
                }

                //add total amount
                for(int i = 0;i<amount_list.size();i++){
                    amount = amount + amount_list.get(i).getAmount();
                }
                if(amount < 0){
                    overall.setTextColor(Color.RED);
                    owe.setText("You Owe");
                }else{
                    overall.setTextColor(Color.GREEN);
                    owe.setText("Owe you");
                }
                amount = Math.round(amount * 100.0) / 100.0;

                overall.setText("RM "+ amount);

                LinearLayout ll = view.findViewById(R.id.home_expenses_list);
                ll.removeAllViews();

                //get all sub activity
                CollectionReference groupRef = db.collection("Group_1");
                if(userInfo.getGroupList()!=null) {
                    groupRef.whereIn("groupId", userInfo.getGroupList()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            //done remove progress bar
                            pb.setVisibility(View.INVISIBLE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            group_list=task.getResult().toObjects(Group.class);

                            for(int i = 0; i<group_list.size();i++){

                                if(group_list.get(i).getMainActivityList() == null){
                                    continue;
                                }else{
                                    for(int j = 0; j<group_list.get(i).getMainActivityList().size();j++){

                                        mainActivity_list_all.add(group_list.get(i).getMainActivityList().get(j));
                                    }
                                }

                            }

                            //Collections.sort(mainActivity_list_all);

                            Collections.sort(mainActivity_list_all, (o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()));

                            for (int j = 0; j<mainActivity_list_all.size();j++){

                                List<SubActivity> subActivity_list = mainActivity_list_all.get(j).getSubActivityList();

                                for(int x = 0; x<subActivity_list.size();x++){

                                    //if current user involve in this activity.
                                    if(subActivity_list.get(x).getOwnerId().matches(userInfo.getUid()) ){

                                        db = FirebaseFirestore.getInstance();
                                        DocumentReference documentReference =db.collection("Users").document(subActivity_list.get(x).getPayerId());
                                        int finalX = x;
                                        int finalJ = j;

                                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {


                                                User user = documentSnapshot.toObject(User.class);
                                                temp_username = user.getUsername();

                                                LinearLayout ll_h = new LinearLayout(getActivity());
                                                ll_h.setOrientation(LinearLayout.HORIZONTAL);
                                                TextView tv = new TextView(getActivity());
                                                tv.setId(tv.generateViewId());
                                                tv.setHeight(200);
                                                tv.setWidth(300);
                                                tv.setTextSize(20);
                                                tv.setTextColor(Color.BLACK);
                                                tv.setText(mainActivity_list_all.get(finalJ).getName());

                                                TextView tv_amount = new TextView(getActivity());
                                                tv_amount.setId(tv.generateViewId());
                                                tv_amount.setHeight(100);
                                                tv_amount.setWidth(250);
                                                tv_amount.setTextSize(22);
                                                tv_amount.setTextColor(Color.RED);
                                                tv_amount.setText("RM "+subActivity_list.get(finalX).getAmount());

                                                TextView tv_username = new TextView(getActivity());
                                                tv_username.setId(tv.generateViewId());
                                                tv_username.setHeight(100);
                                                tv_username.setWidth(250);
                                                tv_username.setTextColor(Color.BLACK);
                                                tv_username.setText(temp_username);

                                                TextView tv_date = new TextView(getActivity());
                                                tv_date.setId(tv.generateViewId());
                                                tv_date.setHeight(100);
                                                tv_date.setTextColor(Color.BLACK);
                                                if(subActivity_list.get(finalX).getCreatedDate() !=null){
                                                    tv_date.setText(formatter.format(subActivity_list.get(finalX).getCreatedDate()));
                                                }
                                                ll_h.addView(tv);
                                                ll_h.addView(tv_username);
                                                ll_h.addView(tv_amount);
                                                ll_h.addView(tv_date);
                                                ll.addView(ll_h);
                                            }
                                        });
                                    }


                                    if(subActivity_list.get(x).getPayerId().matches(userInfo.getUid())){
                                        db = FirebaseFirestore.getInstance();
                                        DocumentReference documentReference =db.collection("Users").document(subActivity_list.get(x).getOwnerId());
                                        int finalJ1 = j;
                                        int finalX1 = x;
                                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {



                                                User user = documentSnapshot.toObject(User.class);
                                                temp_username = user.getUsername();

                                                LinearLayout ll_h = new LinearLayout(getActivity());
                                                ll_h.setOrientation(LinearLayout.HORIZONTAL);

                                                TextView tv = new TextView(getActivity());
                                                tv.setId(tv.generateViewId());
                                                tv.setHeight(200);
                                                tv.setWidth(300);
                                                tv.setTextSize(20);
                                                tv.setTextColor(Color.BLACK);
                                                tv.setText(mainActivity_list_all.get(finalJ1).getName());

                                                TextView tv_username = new TextView(getActivity());
                                                tv_username.setId(tv.generateViewId());
                                                tv_username.setHeight(100);
                                                tv_username.setWidth(250);
                                                tv_username.setTextColor(Color.BLACK);
                                                tv_username.setText(temp_username);

                                                TextView tv_amount = new TextView(getActivity());
                                                tv_amount.setId(tv.generateViewId());
                                                tv_amount.setHeight(100);
                                                tv_amount.setWidth(250);
                                                tv_amount.setTextSize(22);
                                                tv_amount.setTextColor(Color.GREEN);
                                                tv_amount.setText("RM "+subActivity_list.get(finalX1).getAmount());

                                                TextView tv_date = new TextView(getActivity());
                                                tv_date.setId(tv.generateViewId());
                                                tv_date.setHeight(100);
                                                tv_date.setTextColor(Color.BLACK);

                                                if(subActivity_list.get(finalX1).getCreatedDate() !=null){
                                                    tv_date.setText(formatter.format(subActivity_list.get(finalX1).getCreatedDate()));
                                                }

                                                ll_h.addView(tv);
                                                ll_h.addView(tv_username);
                                                ll_h.addView(tv_amount);
                                                ll_h.addView(tv_date);
                                                ll.addView(ll_h);
                                            }
                                        });

                                    }
                                }

                            }

                        }

                    });   //End get all sub activity
                }else{
                    pb.setVisibility(View.INVISIBLE);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    GeneralHelper.showMessage(getActivity(),"You have not expenses yet.");
                }
            }
        });//End get user overall amount


        //add expenses Floating button
        FloatingActionButton fab = view.findViewById(R.id.btnAddPost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), AddExpenses.class);
                startActivity(intent);

            }
        });

        return view;
    }


}
