package com.utar.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utar.assignment.Model.Amount;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.GeneralHelper;

public class settleBill extends AppCompatActivity {

    private static FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_bill);

        TextView username;
        TextView amountToPay;
        Button settleBillBut;
        username = findViewById(R.id.userName_settle);
        amountToPay = findViewById(R.id.amountToPay);
        settleBillBut = findViewById(R.id.payButton);


        fStore.collection("Users").document(getIntent().getStringExtra("userID")).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user1 = task.getResult().toObject(User.class);
                        String um = user1.getUsername();

                        username.setText(um);
                    }
                });
        FirebaseUser user;
        user = Auth.getInstance().getCurrentUser();
        String uid = user.getUid();

        /*fStore.collection("Users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user1 = task.getResult().toObject(User.class);
                        Amount amount = user1.getAmountList().get(0);

                        String amountStr = amount.toString();

                    }
                });*/
        String amount = getIntent().getStringExtra("amount") ;
        amountToPay.setText(amount);

        int position = getIntent().getIntExtra("position",-1);
        GeneralHelper.showMessage(settleBill.this,"Position : " + position);

        settleBillBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*fStore.collection("Users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user1 = task.getResult().toObject(User.class);
                        Amount amount = user1.getAmountList().get(0);

                        String amountStr = amount.toString();

                    }
                });*/
                GeneralHelper.showMessage(settleBill.this,"Bill with amount " + position + " has been settle succesfully"  );
            }
        });
    }
}