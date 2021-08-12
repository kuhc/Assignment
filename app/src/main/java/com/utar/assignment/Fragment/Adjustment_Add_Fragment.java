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

import java.util.ArrayList;

public class Adjustment_Add_Fragment extends Fragment {

    private Button btn_split;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.fragment_home, container, false);

        //tempppp dataaaa
        int split_user = 5;
        double amount = 500;

        LinearLayout ll = new LinearLayout (getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);

        ArrayList<EditText> list = new ArrayList<>();
        ArrayList<TextView> tv_list = new ArrayList<>();
        TextView result = new TextView(getActivity());
        btn_split = new Button(getActivity());
        btn_split.setText("Split By Adjusted");
        btn_split.setEnabled(false);

        result.setId(result.generateViewId());
        result.setHeight(100);
        result.setWidth(200);
        result.setTextColor(Color.BLACK);
        result.setTextSize(20);
        result.setGravity(Gravity.CENTER);
        result.setText("Key in and split out RM"+amount);
        ll.addView(result);


        for(int i = 0 ; i<split_user ; i++){

            int id_generate = 100+i;
            LinearLayout ll_hori = new LinearLayout (getActivity());
            ll_hori.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv = new TextView(getActivity());
            TextView tv_pay = new TextView(getActivity());

            LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tvLayoutParams.setMargins(50, 30, 0, 0);
            tv.setLayoutParams(tvLayoutParams);

            tv.setId(tv.generateViewId());
            tv.setHeight(100);
            tv.setWidth(200);
            tv.setText("ID "+id_generate);
            ll_hori.addView(tv);

            tv_pay.setId(tv.generateViewId());
            tv_pay.setHeight(100);
            tv_pay.setWidth(200);
            tv_pay.setText("RM");
            ll_hori.addView(tv_pay);
            tv_pay.setLayoutParams(tvLayoutParams);


            EditText editText = new EditText(getActivity());
            editText.setId(id_generate);
            editText.setTextSize(15);
            editText.setHeight(100);
            editText.setWidth(200);
            editText.setLayoutParams(tvLayoutParams);
            editText.setRawInputType(InputType.TYPE_CLASS_NUMBER);

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

                    adjustment_split(list ,tv_list,amount);

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
                int finalI = 1;
                EditText temp= new EditText(getActivity());
                Toast.makeText(getActivity(), "This is " + list.get(finalI).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        ll.addView(btn_split);
        //setContentView(ll);


        return ll;
    }

    public void adjustment_split(ArrayList<EditText> list, ArrayList<TextView> tv_list, double amounts){

        double temp_split = amounts / list.size();
        double[] adjusted_amount = new double[list.size()];
        double total_split = 0;

        for(int i = 0; i <list.size(); i++) {
            adjusted_amount[i] = temp_split;
        }

        for(int i = 0; i <list.size(); i++){
            double temp = 0;
            String et = list.get(i).getText().toString();
            if(et.matches("")
                    ||et.contains("-")
                    ||et.contains("\\")
                    ||et.contains("*")
                    ||et.contains("+")
            ){

            }else{
                temp = Double.parseDouble(list.get(i).getText().toString());
                adjusted_amount[i] = adjusted_amount[i] + temp;
            }

            for(int j = 0 ; j <list.size();j++){
                adjusted_amount[j] = adjusted_amount[j]-(temp/list.size());
                adjusted_amount[j] = Math.round(adjusted_amount[j] * 100.0) / 100.0;
            }


        }

        for(int i = 0; i <list.size(); i++){
            total_split = total_split + adjusted_amount[i];
        }
        if(total_split < amounts){
            adjusted_amount[0] = adjusted_amount[0] + (amounts - total_split);
            adjusted_amount[0] = Math.round(adjusted_amount[0] * 100.0) / 100.0;
        }
        if(total_split > amounts){
            adjusted_amount[0] = adjusted_amount[0] - (amounts - total_split);
            adjusted_amount[0] = Math.round(adjusted_amount[0] * 100.0) / 100.0;
        }

        for(int i = 0; i <list.size(); i++){
            tv_list.get(i).setText("RM"+adjusted_amount[i]);

        }

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



}