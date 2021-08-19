package com.utar.assignment.Fragment;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.utar.assignment.Util.SplitCalHelper;

import java.util.ArrayList;

public class Adjustment_Add_Fragment extends Fragment {

    private Button btn_split;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ArrayList<String> split_user_list = new ArrayList<>();
        ArrayList<String> split_userid_list = new ArrayList<>();

        Intent intent = getActivity().getIntent();
        int temp_i = 0;
        while (intent.hasExtra("users" + temp_i)) {
            String temp = intent.getStringExtra("users" + temp_i);
            split_user_list.add(temp);
            temp_i++;
        }

        int temp_j = 0;
        while (intent.hasExtra("usersid" + temp_j)) {
            String temp = intent.getStringExtra("usersid" + temp_j);
            split_userid_list.add(temp);
            temp_j++;
        }


        int split_user = split_user_list.size();
        double amount = Double.parseDouble(intent.getStringExtra("amount"));

        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);

        ArrayList<EditText> list = new ArrayList<>();
        ArrayList<TextView> tv_list = new ArrayList<>();
        TextView result = new TextView(getActivity());
        btn_split = new Button(getActivity());
        btn_split.setText("Split By Adjusted");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 0f;
        params.setMargins(50, 50, 50, 50);
        params.gravity = Gravity.RIGHT;
        btn_split.setLayoutParams(params);
        btn_split.setEnabled(false);


        result.setId(result.generateViewId());
        result.setHeight(100);
        result.setWidth(200);
        result.setTextColor(Color.BLACK);
        result.setTextSize(20);
        result.setGravity(Gravity.CENTER);
        result.setBackgroundColor(Color.LTGRAY);
        result.setText("Key in the adjustment in RM" + amount);
        ll.addView(result);


        for (int i = 0; i < split_user; i++) {

            int id_generate = 100 + i;
            LinearLayout ll_hori = new LinearLayout(getActivity());
            ll_hori.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv = new TextView(getActivity());
            TextView tv_pay = new TextView(getActivity());

            LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tvLayoutParams.setMargins(50, 30, 0, 0);
            tv.setLayoutParams(tvLayoutParams);

            tv.setId(tv.generateViewId());
            tv.setHeight(100);
            tv.setWidth(200);
            tv.setText(split_user_list.get(i));
            ll_hori.addView(tv);

            tv_pay.setId(tv.generateViewId());
            tv_pay.setHeight(100);
            tv_pay.setWidth(200);
            tv_pay.setText("RM ");
            tv_pay.setTextColor(Color.BLACK);
            ll_hori.addView(tv_pay);
            tv_pay.setLayoutParams(tvLayoutParams);

            EditText editText = new EditText(getActivity());
            editText.setId(id_generate);
            editText.setTextSize(15);
            editText.setHeight(100);
            editText.setWidth(200);
            editText.setLayoutParams(tvLayoutParams);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers

            editText.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String str = editText.getText().toString();
                    double parse_amount;

                    //check the decimal point
                    if (!str.isEmpty()) {
                        String str2 = PerfectDecimal(str, 3, 2);
                        if (!str2.equals(str)) {
                            editText.setText(str2);
                            editText.setSelection(str2.length());
                        }

                    }

                    if (str.matches("")
                            || str.contains("-")
                            || str.contains("\\")
                            || str.contains("*")
                            || str.contains("+")
                            || str.contains("_")
                    ) {
                        parse_amount = 0;
                    } else {
                        parse_amount = Double.parseDouble(str);
                    }

                    if (parse_amount > amount) {
                        editText.setText(Double.toString(amount));
                    }

                    adjustment_split(list, tv_list, amount);

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

                ArrayList<Double> amount_list = adjustment_split_result(list, amount);
                SplitCalHelper sh = new SplitCalHelper();
                sh.create_main_activity(getActivity(), split_userid_list, amount_list, intent.getStringExtra("name"),
                        intent.getStringExtra("group"), amount);

                getActivity().finish();
            }
        });


        ll.addView(btn_split);
        return ll;
    }

    public void adjustment_split(ArrayList<EditText> list, ArrayList<TextView> tv_list, double amounts) {

        double temp_split = amounts / list.size();
        double[] adjusted_amount = new double[list.size()];
        double total_split = 0;

        for (int i = 0; i < list.size(); i++) {
            adjusted_amount[i] = temp_split;
        }

        for (int i = 0; i < list.size(); i++) {
            double temp = 0;
            String et = list.get(i).getText().toString();
            if (et.matches("")
                    || et.contains("-")
                    || et.contains("\\")
                    || et.contains("*")
                    || et.contains("+")
                    || et.contains("_")
            ) {

            } else {
                temp = Double.parseDouble(list.get(i).getText().toString());
                adjusted_amount[i] = adjusted_amount[i] + temp;
            }

            for (int j = 0; j < list.size(); j++) {
                adjusted_amount[j] = adjusted_amount[j] - (temp / list.size());
                adjusted_amount[j] = Math.round(adjusted_amount[j] * 100.0) / 100.0;
            }

        }


        //if the total split is not split out complete, add to first one 0.1
        for (int i = 0; i < list.size(); i++) {
            total_split = total_split + adjusted_amount[i];
        }
        if (total_split < amounts) {
            adjusted_amount[0] = adjusted_amount[0] + (amounts - total_split);
            adjusted_amount[0] = Math.round(adjusted_amount[0] * 100.0) / 100.0;
        }
        if (total_split > amounts) {
            adjusted_amount[0] = adjusted_amount[0] - (amounts - total_split);
            adjusted_amount[0] = Math.round(adjusted_amount[0] * 100.0) / 100.0;
        }

        for (int i = 0; i < list.size(); i++) {
            if (adjusted_amount[i] < 0) {
                btn_split.setEnabled(false);
                break;
            } else {
                btn_split.setEnabled(true);
            }
        }

        for (int i = 0; i < list.size(); i++) {
            tv_list.get(i).setText("RM" + adjusted_amount[i]);
        }


    }


    public ArrayList<Double> adjustment_split_result(ArrayList<EditText> adjust_list, double amount) {

        ArrayList<Double> result_list = new ArrayList<>();
        double temp_split = amount / adjust_list.size();
        double[] adjusted_amount = new double[adjust_list.size()];
        double total_split = 0;

        for (int i = 0; i < adjust_list.size(); i++) {
            adjusted_amount[i] = temp_split;
        }

        for (int i = 0; i < adjust_list.size(); i++) {
            double temp = 0;
            String et = adjust_list.get(i).getText().toString();
            if (et.matches("")
                    || et.contains("-")
                    || et.contains("\\")
                    || et.contains("*")
                    || et.contains("+")
            ) {

            } else {
                temp = Double.parseDouble(adjust_list.get(i).getText().toString());
                adjusted_amount[i] = adjusted_amount[i] + temp;
            }

            for (int j = 0; j < adjust_list.size(); j++) {
                adjusted_amount[j] = adjusted_amount[j] - (temp / adjust_list.size());
                adjusted_amount[j] = Math.round(adjusted_amount[j] * 100.0) / 100.0;
            }
        }

        //if the total split is not split out complete, add to first one 0.1
        for (int i = 0; i < adjust_list.size(); i++) {
            total_split = total_split + adjusted_amount[i];
        }
        if (total_split < amount) {
            adjusted_amount[0] = adjusted_amount[0] + (amount - total_split);
            adjusted_amount[0] = Math.round(adjusted_amount[0] * 100.0) / 100.0;
        }
        if (total_split > amount) {
            adjusted_amount[0] = adjusted_amount[0] - (amount - total_split);
            adjusted_amount[0] = Math.round(adjusted_amount[0] * 100.0) / 100.0;
        }

        for (int i = 0; i < adjust_list.size(); i++) {
            result_list.add(adjusted_amount[i]);
        }

        return result_list;
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