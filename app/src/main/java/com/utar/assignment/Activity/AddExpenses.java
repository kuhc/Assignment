package com.utar.assignment.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.utar.assignment.R;
import com.utar.assignment.Util.GeneralHelper;
import com.utar.assignment.Util.SplitCalHelper;

import java.util.ArrayList;

public class AddExpenses extends AppCompatActivity {

    private EditText user_share,expenses_name;
    private Button btn_adduser,btn_split;
    private ChipGroup chipGroup;
    private TextView result;
    private int split_user_count =0;
    private double temp_result=0;
    private EditText amount;
    SplitCalHelper sh = new SplitCalHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        user_share = findViewById(R.id.add_usershare);
        btn_adduser = findViewById(R.id.add_adduser);
        btn_split = findViewById(R.id.add_split);
        chipGroup = findViewById(R.id.add_chipGroup);
        result = findViewById(R.id.add_result);
        amount = (EditText) findViewById(R.id.add_amount);
        expenses_name = (EditText) findViewById(R.id.add_expensesname);

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






        btn_split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


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
    }

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

        result.setText("Each User Split RM"+temp_result);
    }

    //
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