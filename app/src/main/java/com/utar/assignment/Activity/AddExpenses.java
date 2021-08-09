package com.utar.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.GeneralHelper;
import com.utar.assignment.Util.SplitCalHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddExpenses extends AppCompatActivity {

    private Button btn_adduser,btn_split,btn_cancel;
    private ChipGroup chipGroup;
    private TextView result;
    private int split_user_count =0;
    private double temp_result=0;
    private EditText amount,expenses_name;
    private AutoCompleteTextView user_share;
    private Spinner groups;
    SplitCalHelper sh = new SplitCalHelper();

    private User userInfo;

    List<String> userList = new ArrayList<>();

    private static FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth Auth;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        user_share = findViewById(R.id.add_usershare);
        btn_adduser = findViewById(R.id.add_adduser);
        btn_split = findViewById(R.id.add_split);
        btn_cancel = findViewById(R.id.add_cancel);
        chipGroup = findViewById(R.id.add_chipGroup);
        result = findViewById(R.id.add_result);
        amount = (EditText) findViewById(R.id.add_amount);
        expenses_name = (EditText) findViewById(R.id.add_expensesname);
        groups = findViewById(R.id.add_group);

        List<String> supplierNames = Arrays.asList("Kampar group", "Ipoh Group", "KL Group");

        ArrayAdapter<String> grouplist = new ArrayAdapter<String>
                (AddExpenses.this, android.R.layout.simple_list_item_1,supplierNames);
        grouplist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groups.setAdapter(grouplist);

        //get the user friend as suggestion
        FirebaseUser user;
        user = Auth.getInstance().getCurrentUser();
        String uid = user.getUid();
        db = FirebaseFirestore.getInstance();
        DocumentReference documentReference =db.collection("Users").document(uid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userInfo = documentSnapshot.toObject(User.class);
                if(userInfo.getFriendList()!= null) {
                    userList.addAll(userInfo.getFriendList());
                    user_share.setAdapter(new ArrayAdapter<>
                            (AddExpenses.this, android.R.layout.simple_list_item_activated_1,userList));

                }
            }
        });




        //default user in chips group "You"
        Chip default_chip = new Chip(this);
        ChipDrawable drawable = ChipDrawable.createFromAttributes(this,null
                ,0,R.style.Widget_MaterialComponents_Chip_Filter);
        default_chip.setChipDrawable(drawable);
        default_chip.setCheckable(false);
        default_chip.setClickable(false);
        default_chip.setChipIconResource(R.drawable.ic_user);
        default_chip.setIconStartPadding(3f);
        default_chip.setPadding(60,10,60,10);
        default_chip.setText("You");
        chipGroup.addView(default_chip);


        //amount has been type
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cal_temp_result();
            }
        });

        //ready to split
        btn_split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double amounts = 0;
                String am =amount.getText().toString();
                ArrayList<String> split_user_list = new ArrayList<>();
                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    String user = ((Chip) chipGroup.getChildAt(i)).getText().toString();
                    split_user_list.add(user);
                }
                amounts = Double.parseDouble(am);
                split_to_database(split_user_list,amounts);

                GeneralHelper.showMessage(AddExpenses.this,expenses_name.getText().toString());
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //function to calculate the split amount
    public void cal_temp_result(){

        double amounts = 0;
        String text =amount.getText().toString();
        if(text.matches("")){
            amounts=0;
        }else{
            amounts = Double.parseDouble(text);
        }
        split_user_count = chipGroup.getChildCount();
        temp_result = sh.splitNormal(split_user_count,amounts);

        result.setText("Split: RM"+temp_result);
    }

    //save amount to
    public void split_to_database(List<String> userList, double split_amount){

    }

    //add user for split
    public void add_user_chip(View view){
        Chip chip = new Chip(this);

        ChipDrawable drawable = ChipDrawable.createFromAttributes(this,null
                ,0,R.style.Widget_MaterialComponents_Chip_Entry);
        chip.setChipDrawable(drawable);
        chip.setCheckable(false);
        chip.setClickable(false);
        chip.setChipIconResource(R.drawable.ic_user);
        chip.setIconStartPadding(3f);
        chip.setPadding(60,10,60,10);
        chip.setText(user_share.getText().toString());
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip);
                cal_temp_result();
            }
        });

        chipGroup.addView(chip);

        user_share.setText("");
        cal_temp_result();
    }
}