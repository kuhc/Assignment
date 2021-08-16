package com.utar.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.utar.assignment.Util.SplitCalHelper;

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

        username.setText(getIntent().getStringExtra("userName"));
        double amounttoPay = getIntent().getDoubleExtra("amounttoPay",-1);
        String amountST = Double.toString(amounttoPay);
        amountToPay.setText(amountST);

        if(amounttoPay < 0)
        {
            amountToPay.setTextColor(Color.RED);
        }
        else
            amountToPay.setTextColor(Color.GREEN);

        GeneralHelper.showMessage(settleBill.this,"Amount :" + amountST);

        /*fStore.collection("Users").document(getIntent().getStringExtra("userID")).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user1 = task.getResult().toObject(User.class);
                        String um = user1.getUsername();

                        username.setText(um);
                    }
                });*/
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

        int position = getIntent().getIntExtra("position",-1);
        //GeneralHelper.showMessage(settleBill.this,"Position : " + position);

        settleBillBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("Users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user1 = task.getResult().toObject(User.class);
                        Amount amount = user1.getAmountList().get(position);
                        String ownerID = amount.getOwnerId();
                        String payerID = uid;



                        AlertDialog.Builder builder = new AlertDialog.Builder(settleBill.this);
                        View v = LayoutInflater.from(settleBill.this).inflate(R.layout.settle_bill_amount, null, false);
                        builder.setTitle("Enter the amount");
                        final EditText editText = v.findViewById(R.id.settle_bill_amount);
                        builder.setView(v);

                        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!editText.getText().toString().isEmpty()) {

                                    String enteredAmount = editText.getText().toString().trim();
                                    Double enteredAmountDouble = Double.parseDouble(enteredAmount);
                                    if(amounttoPay < 0) {
                                        double amounttoPayPositive = Math.abs(amounttoPay);

                                        if (enteredAmountDouble > amounttoPayPositive || enteredAmountDouble < amounttoPay) {
                                            GeneralHelper.showMessage(settleBill.this, "The amount is out of range!!!");
                                        } else {
                                            SplitCalHelper splitCalHelper = new SplitCalHelper();
                                            GeneralHelper.showMessage(settleBill.this, "Settle Successfully");
                                            splitCalHelper.clear_bill(ownerID, payerID, enteredAmountDouble, settleBill.this, amountToPay);
                                        }
                                    }

                                    else if(amounttoPay >0)
                                    {
                                        if (enteredAmountDouble > amounttoPay || enteredAmountDouble <= 0) {
                                            GeneralHelper.showMessage(settleBill.this, "The amount is out of range!!!");
                                        } else {
                                            SplitCalHelper splitCalHelper = new SplitCalHelper();
                                            GeneralHelper.showMessage(settleBill.this, "Settle Successfully");
                                            splitCalHelper.clear_bill(ownerID, payerID, enteredAmountDouble, settleBill.this, amountToPay);
                                        }
                                    }
                                } else {
                                    editText.setError("add item here !");
                                    GeneralHelper.showMessage(settleBill.this, "Please type in some value!");
                                }
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.show();
                    }
                });
                //GeneralHelper.showMessage(settleBill.this,"Bill with amount " + position + " has been settle succesfully"  );
            }
        });
    }
}