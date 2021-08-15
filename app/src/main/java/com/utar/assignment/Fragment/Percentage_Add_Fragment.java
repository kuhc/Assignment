package com.utar.assignment.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.utar.assignment.Activity.AddExpenses;
import com.utar.assignment.R;
import com.utar.assignment.Util.FirestoreHelper;
import com.utar.assignment.Util.SplitCalHelper;

import java.util.ArrayList;

public class Percentage_Add_Fragment extends Fragment {

    private Button btn_split;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.fragment_home, container, false);

        ArrayList<String> split_user_list = new ArrayList<>();
        ArrayList<String> split_userid_list = new ArrayList<>();

        Intent intent = getActivity().getIntent();
        int temp_i = 0;
        while (intent.hasExtra("users" + temp_i)){
            String temp = intent.getStringExtra("users" + temp_i);
            split_user_list.add(temp);
            temp_i++;
        }

        int temp_j= 0;
        while (intent.hasExtra("usersid" + temp_j)){
            String temp = intent.getStringExtra("usersid" + temp_j);
            split_userid_list.add(temp);
            temp_j++;
        }


        int split_user = split_user_list.size();
        double amount = Double.parseDouble(intent.getStringExtra("amount"));;

        double split = amount/split_user;


        LinearLayout ll = new LinearLayout (getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);

        ArrayList<EditText> list = new ArrayList<>();
        ArrayList<TextView> tv_list = new ArrayList<>();

        TextView result = new TextView(getActivity());
        btn_split = new Button(getActivity());
        btn_split.setText("Split Percentage");
        btn_split.setEnabled(false);



        result.setId(result.generateViewId());
        result.setHeight(100);
        result.setWidth(200);
        result.setTextColor(Color.BLACK);
        result.setTextSize(20);
        result.setGravity(Gravity.CENTER);
        result.setText("Key in and split out 100%");
        ll.addView(result);


        for(int i = 0 ; i<split_user ; i++){

            int id_generate = 100+i;
            LinearLayout ll_hori = new LinearLayout (getActivity());
            ll_hori.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv = new TextView(getActivity());
            TextView tv_pay = new TextView(getActivity());


            LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tvLayoutParams.setMargins(50, 10, 0, 0);
            tv.setLayoutParams(tvLayoutParams);

            tv.setId(tv.generateViewId());
            tv.setHeight(100);
            tv.setWidth(200);
            tv.setText(split_user_list.get(i));
            ll_hori.addView(tv);

            tv_pay.setId(tv.generateViewId());
            tv_pay.setHeight(100);
            tv_pay.setWidth(200);
            tv_pay.setText("RM" + split);
            ll_hori.addView(tv_pay);
            tv_pay.setLayoutParams(tvLayoutParams);


            EditText editText = new EditText(getActivity());
            editText.setId(id_generate);
            editText.setHeight(100);
            editText.setWidth(200);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);

            editText.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) { }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    int temp_int = percentage_left(list);

                    if(temp_int < 0){
                        result.setText("The percentage is over " + temp_int+"%");
                        result.setTextColor(Color.RED);
                        btn_split.setEnabled(false);
                    }else if(temp_int == 0)
                    {
                        result.setText("The percentage left "+ temp_int+"%");
                        result.setTextColor(Color.GREEN);
                        btn_split.setEnabled(true);
                    }
                    else
                    {
                        result.setText("The percentage left "+ temp_int+"%");
                        result.setTextColor(Color.BLACK);
                        btn_split.setEnabled(false);
                    }

                    cal_temp_result(list,tv_list,amount);
                }
            });

            ll_hori.addView(editText);
            ll.addView(ll_hori);

            tv_list.add(tv_pay);
            list.add(editText);
        }


        btn_split.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ArrayList<Double> amount_list = cal_result_list(list,amount);

                SplitCalHelper sh = new SplitCalHelper();
                sh.create_main_activity(getActivity(),split_userid_list,amount_list,intent.getStringExtra("name"),
                        intent.getStringExtra("group"),amount);

                getActivity().finish();

            }
        });


        ll.addView(btn_split);


        return ll;
    }

    public int percentage_left(ArrayList<EditText> list){

        int percentage = 100;
        int temp_percentage;
        for(int i = 0; i <list.size(); i++){

            if(list.get(i).getText().toString().matches("")){
                temp_percentage=0;
            }else{
                temp_percentage = Integer.parseInt(list.get(i).getText().toString());
            }

            percentage = percentage - temp_percentage;
        }

        return percentage;
    }

    public void cal_temp_result(ArrayList<EditText> list,ArrayList<TextView> tv_list,double amount) {
        double percentage;
        for (int i = 0; i < list.size(); i++) {

            double result;

            String temp_string = list.get(i).getText().toString();
            if (temp_string.matches("") || temp_string.matches("-")) {
                percentage = 0;
            } else {
                percentage = Double.parseDouble(temp_string);
            }
            result = Math.round(((amount * percentage) / 100) * 100.0) / 100.0;

            tv_list.get(i).setText("RM" + result);
        }
    }

    public ArrayList<Double> cal_result_list (ArrayList<EditText> percentage_list,double amount){
        double percentage;
        ArrayList<Double> result_list = new ArrayList<>();

        for (int i = 0; i < percentage_list.size(); i++) {

            String temp_string = percentage_list.get(i).getText().toString();
            if (temp_string.matches("") || temp_string.matches("-")) {
                percentage = 0;
            } else {
                percentage = Double.parseDouble(temp_string);
            }
            result_list.add(Math.round(((amount * percentage) / 100) * 100.0) / 100.0);
        }

        return result_list;
    }

}