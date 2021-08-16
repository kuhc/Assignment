package com.utar.assignment.Util;

import android.content.Context;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utar.assignment.Activity.AddExpenses;
import com.utar.assignment.Model.Amount;
import com.utar.assignment.Model.Group;
import com.utar.assignment.Model.MainActivity;
import com.utar.assignment.Model.SubActivity;
import com.utar.assignment.Model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SplitCalHelper {

    //normal split equally
    public double splitNormal(int per, double amount){
        double result = amount/per;
        Math.round(result);
        return result;
    }

    public void create_main_activity(Context ctx, ArrayList<String> user_id_list,
                                        ArrayList<Double> split_amount_list, String exp_name, String group_id, double amount) {
        Date date = new Date();
        update_amount_list(user_id_list,split_amount_list,ctx);


        ArrayList<SubActivity> subactivity_List = new ArrayList<>();
        MainActivity mainactivity = new com.utar.assignment.Model.MainActivity();


        for (int i = 1; i < user_id_list.size(); i++) {
            SubActivity subactivity = new SubActivity();
            subactivity.setPayerId(user_id_list.get(0));
            subactivity.setOwnerId(user_id_list.get(i));
            subactivity.setAmount(split_amount_list.get(i));
            subactivity.setCreatedDate(date);
            subactivity_List.add(subactivity);
        }
        mainactivity.setSubActivityList(subactivity_List);
        mainactivity.setName(exp_name);
        mainactivity.setId(UUID.randomUUID().toString());
        mainactivity.setCreatedDate(date);

        mainactivity.setBillAmount(amount);
        FirestoreHelper.getGroup(group_id, new FirebaseCallback() {
            @Override
            public void onResponse(Object object) {
                Group group = (Group) object;


                if (group.getMainActivityList() == null) {
                    group.setMainActivityList(new ArrayList<MainActivity>());
                }

                group.getMainActivityList().add(mainactivity);

                FirestoreHelper.setGroup(group, new FirebaseCallback() {
                    @Override
                    public void onResponse() {
                        GeneralHelper.showMessage(ctx, "Split Successfully");
                    }
                });

            }
        });
    }


    public void update_amount_list(ArrayList<String> user_id_list, ArrayList<Double> split_amount_list,Context ctx){

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        //loop the owner account and save to amount
        for (int i = 1;i<user_id_list.size();i++){

            int finalI1 = i;
            DocumentReference documentReference = db.collection("Users").document(user_id_list.get(i));
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);

                    if(user.getAmountList() == null){
                        user.setAmountList(new ArrayList<Amount>());
                    }
                    boolean in_amount_list = false;

                    //update the every owner user's amount
                    for (int j = 0; j < user.getAmountList().size(); j++) {
                        Double temp_amount = split_amount_list.get(finalI1);
                        temp_amount = temp_amount - (temp_amount*2);

                        if(user.getAmountList().get(j).getOwnerId().matches(user_id_list.get(0))){
                            user.getAmountList().get(j).updateAmount(temp_amount);
                            in_amount_list=true;
                        }
                    }

                    //if the payer id not in owner id amount list
                    if(in_amount_list == false)
                    {
                        Double temp_amount = split_amount_list.get(finalI1);
                        temp_amount = temp_amount - (temp_amount*2);

                        Amount temp_amount_class = new Amount();
                        temp_amount_class.setOwnerId(user_id_list.get(0));
                        temp_amount_class.setAmount(temp_amount);
                        user.getAmountList().add(temp_amount_class);
                    }

                    FirestoreHelper.setUser(user, new FirebaseCallback() {
                        @Override
                        public void onResponse() {

                        }
                    });


                }
            });

        }

        DocumentReference documentReference1 = db.collection("Users").document(user_id_list.get(0));
        documentReference1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                User user = documentSnapshot.toObject(User.class);

                if(user.getAmountList() == null){
                    user.setAmountList(new ArrayList<Amount>());
                }


                //update the every owner user's amount
                for (int x = 1; x < user_id_list.size(); x++) {

                    boolean in_amount_list = false;

                    //match the amount list
                    for (int j = 0; j < user.getAmountList().size(); j++) {

                        if (user.getAmountList().get(j).getOwnerId().matches(user_id_list.get(x))) {
                            user.getAmountList().get(j).updateAmount(split_amount_list.get(x));
                            in_amount_list = true;
                        }

                    }
                    //if the payer id not in owner id amount list
                    if (in_amount_list == false) {
                        Amount temp_amount_class = new Amount();
                        temp_amount_class.setOwnerId(user_id_list.get(x));
                        temp_amount_class.setAmount(split_amount_list.get(x));
                        user.getAmountList().add(temp_amount_class);
                    }


                    FirestoreHelper.setUser(user, new FirebaseCallback() {
                        @Override
                        public void onResponse() {

                        }
                    });
                }
            }
        });

    }

    public void clear_bill(String owner_id, String payer_id, double amount, Context context, TextView amounttopay){
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Users").document(owner_id);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                List<Amount> amountList = user.getAmountList();

                for(int i=0; i<amountList.size();i++)
                {
                    String getOwnerid=  amountList.get(i).getOwnerId();
                    if(getOwnerid.equals(payer_id))
                    {
                        GeneralHelper.showMessage(context,"This is the number " + i + " " + amountList.get(i).getAmount());
                        double exitingAmount = amountList.get(i).getAmount();
                        if( exitingAmount< 0)
                        {
                            double newAmount = exitingAmount+amount;
                            user.getAmountList().get(i).setAmount(newAmount);
                        }
                        else if (exitingAmount >0)
                        {
                            double newAmount = exitingAmount - amount;
                            user.getAmountList().get(i).setAmount(newAmount);
                        }
                    }
                   //GeneralHelper.showMessage(context,"This is the number " + i + amountList.get(i).getAmount() + " getOwmerID :" + amountList.get(i).getOwnerId() + " owner_id : " + owner_id) ;
                }
                FirestoreHelper.setUser(user, new FirebaseCallback() {
                    @Override
                    public void onResponse() {

                    }
                });
            }
        });

        DocumentReference documentReference2 = db.collection("Users").document(payer_id);
        documentReference2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                List<Amount> amountList = user.getAmountList();

                for(int i=0; i<amountList.size();i++)
                {
                    String getPayerid=  amountList.get(i).getOwnerId();
                    if(getPayerid.equals(owner_id))
                    {
                        GeneralHelper.showMessage(context,"This is the number2  " + i + " " + amountList.get(i).getAmount());
                        double exitingAmount = amountList.get(i).getAmount();
                        if( exitingAmount< 0)
                        {
                            double newAmount = exitingAmount+amount;
                            String newAmountString = String.valueOf(newAmount);
                            user.getAmountList().get(i).setAmount(newAmount);
                            amounttopay.setText(newAmountString);
                        }
                        else if (exitingAmount >0)
                        {
                            double newAmount = exitingAmount - amount;
                            String newAmountString = String.valueOf(newAmount);
                            user.getAmountList().get(i).setAmount(newAmount);
                            amounttopay.setText(newAmountString);
                        }
                    }
                    FirestoreHelper.setUser(user, new FirebaseCallback() {
                        @Override
                        public void onResponse() {

                        }
                    });
                    //GeneralHelper.showMessage(context,"This is the number " + i + amountList.get(i).getAmount() + " getOwmerID :" + amountList.get(i).getOwnerId() + " owner_id : " + owner_id) ;
                }
            }
        });
    }

}
