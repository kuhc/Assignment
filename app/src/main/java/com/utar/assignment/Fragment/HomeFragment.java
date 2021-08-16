package com.utar.assignment.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utar.assignment.Activity.AddExpenses;
import com.utar.assignment.Model.Amount;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.GeneralHelper;
import com.utar.assignment.Util.SplitCalHelper;

import java.util.ArrayList;

public class HomeFragment  extends Fragment {


    private TextView overall;
    private User userInfo;
    double amount;
    FirebaseFirestore db;
    private FirebaseAuth Auth;
    private ArrayList<Amount> amount_list =new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        overall = view.findViewById(R.id.home_overall_amount);

        FirebaseUser user;
        user = Auth.getInstance().getCurrentUser();
        String uid = user.getUid();

        db = FirebaseFirestore.getInstance();
        DocumentReference documentReference =db.collection("Users").document(uid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                userInfo = documentSnapshot.toObject(User.class);

                for(int i = 0;i<userInfo.getAmountList().size();i++){

                    amount_list.add(userInfo.getAmountList().get(i));

                }

                //add total amount
                for(int i = 0;i<amount_list.size();i++){
                    amount = amount + amount_list.get(i).getAmount();
                }
                if(amount < 0){
                    overall.setTextColor(Color.RED);
                }else{
                    overall.setTextColor(Color.GREEN);
                }

                overall.setText("RM "+ amount);
            }

        });




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
