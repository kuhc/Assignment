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
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.FirebaseCallback;
import com.utar.assignment.Util.FirestoreHelper;
import com.utar.assignment.Util.GeneralHelper;
import com.utar.assignment.Util.SplitCalHelper;

import java.util.ArrayList;
import java.util.List;

public class Unequal_Add_Fragment extends Fragment {

    private Button btn_split;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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
        double amount = Double.parseDouble(intent.getStringExtra("amount"));


        LinearLayout ll = new LinearLayout (getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);

        ArrayList<EditText> list = new ArrayList<>();
        TextView result = new TextView(getActivity());
        btn_split = new Button(getActivity());
        btn_split.setText("Split Unequally");
        btn_split.setEnabled(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 0f;
        params.setMargins(50,50,50,50);
        params.gravity = Gravity.RIGHT;
        btn_split.setLayoutParams(params);


        result.setId(result.generateViewId());
        result.setHeight(100);
        result.setWidth(200);
        result.setTextColor(Color.BLACK);
        result.setTextSize(20);
        result.setGravity(Gravity.CENTER);
        result.setBackgroundColor(Color.LTGRAY);
        result.setText("Key in and split out RM"+amount);
        ll.addView(result);


        for(int i = 0 ; i<split_user ; i++){

            int id_generate = 100+i;
            LinearLayout ll_hori = new LinearLayout (getActivity());
            ll_hori.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv = new TextView(getActivity());

            LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tvLayoutParams.setMargins(50, 30, 0, 0);
            tv.setLayoutParams(tvLayoutParams);

            tv.setId(tv.generateViewId());
            tv.setHeight(100);
            tv.setWidth(200);
            tv.setText(split_user_list.get(i));
            ll_hori.addView(tv);

            EditText editText = new EditText(getActivity());
            editText.setId(id_generate);
            editText.setTextSize(15);
            editText.setHeight(100);
            editText.setWidth(200);
            editText.setLayoutParams(tvLayoutParams);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers

            editText.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) { }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String str = editText.getText().toString();

                    //check the decimal point
                    if (!str.isEmpty()){
                        String str2 = PerfectDecimal(str, 3, 2);
                        if (!str2.equals(str)) {
                            editText.setText(str2);
                            editText.setSelection(str2.length());
                        }
                    }

                    double temp_double = unequally_left(list,amount);
                    temp_double = Math.round(temp_double * 100.0) / 100.0;
                    if(temp_double < 0){
                        temp_double = Math.abs(temp_double);
                        result.setText("The amount is over RM" + temp_double);
                        result.setTextColor(Color.RED);
                        btn_split.setEnabled(false);
                    }else if(temp_double == 0)
                    {
                        result.setText("The amount has been split out");
                        result.setTextColor(Color.GREEN);
                        btn_split.setEnabled(true);
                    }
                    else
                    {
                        result.setText("The amount left RM"+ temp_double);
                        result.setTextColor(Color.BLACK);
                        btn_split.setEnabled(false);
                    }
                }
            });

            ll_hori.addView(editText);
            ll.addView(ll_hori);
            list.add(editText);
        }



        btn_split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Double> amount_list = new ArrayList<>();
                for(int i = 0; i<list.size();i++){

                    double amounts = 0;
                    String text =list.get(i).getText().toString();
                    if(text.matches("")){
                        amounts=0;
                    }else{
                        amounts = Double.parseDouble(text);
                    }
                    amount_list.add(amounts);
                }
                SplitCalHelper sh = new SplitCalHelper();
                sh.create_main_activity(getActivity(),split_userid_list,amount_list,intent.getStringExtra("name"),
                        intent.getStringExtra("group"),amount);

                getActivity().finish();
            }
        });


        ll.addView(btn_split);

        return ll;
    }

    public String PerfectDecimal(String str, int MAX_BEFORE_POINT, int MAX_DECIMAL) {
        if (str.charAt(0) == '.') str = "0" + str;
        int max = str.length();

        String rFinal = "";
        boolean after = false;
        int i = 0, up = 0, decimal = 0;
        char t;
        while (i < max) {
            t = str.charAt(i);
            if (t != '.' && after == false) {
                up++;
                if (up > MAX_BEFORE_POINT) return rFinal;
            } else if (t == '.') {
                after = true;
            } else {
                decimal++;
                if (decimal > MAX_DECIMAL)
                    return rFinal;
            }
            rFinal = rFinal + t;
            i++;
        }
        return rFinal;
    }

    public double unequally_left(ArrayList<EditText> list, double amounts){

        double temp_amounts;
        for(int i = 0; i <list.size(); i++){

            if(list.get(i).getText().toString().matches("")
                    ||list.get(i).getText().toString().contains("-")
                    ||list.get(i).getText().toString().contains("\\")
                    ||list.get(i).getText().toString().contains("*")
                    ||list.get(i).getText().toString().contains("+")
            ){
                temp_amounts=0;
            }else{
                temp_amounts = Double.parseDouble(list.get(i).getText().toString());
            }

            amounts = amounts - temp_amounts;
        }

        return amounts;
    }
}