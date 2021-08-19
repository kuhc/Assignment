package com.utar.assignment.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.utar.assignment.Model.Group;
import com.utar.assignment.Model.MainActivity;
import com.utar.assignment.Model.SubActivity;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.FirebaseCallback;
import com.utar.assignment.Util.FirestoreHelper;
import com.utar.assignment.Util.GeneralHelper;
import com.utar.assignment.Util.SplitCalHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddExpenses extends AppCompatActivity {

    private Button btn_split, btn_cancel, btn_split_unequally;
    private ChipGroup chipGroup;
    private TextView result;
    private int split_user_count = 0;
    private double temp_result = 0;
    private EditText amount, expenses_name;
    private Spinner groups;
    SplitCalHelper sh = new SplitCalHelper();

    //Model List
    List<Group> group = new ArrayList<>();
    List<User> userList = new ArrayList<>();
    List<SubActivity> subactivity_List = new ArrayList<>();

    //model
    private com.utar.assignment.Model.MainActivity mainactivity;
    private SubActivity subactivity;
    private Group cur_group;
    private User userInfo;

    //firebase
    private static FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth Auth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        btn_split_unequally = findViewById(R.id.add_split_unequally);
        btn_split = findViewById(R.id.add_split);
        btn_cancel = findViewById(R.id.add_cancel);
        chipGroup = findViewById(R.id.add_chipGroup);
        result = findViewById(R.id.add_result);
        amount = (EditText) findViewById(R.id.add_amount);
        expenses_name = (EditText) findViewById(R.id.add_expensesname);
        groups = findViewById(R.id.add_group);


        //get the user friend as suggestion
        FirebaseUser user;
        user = Auth.getInstance().getCurrentUser();
        String uid = user.getUid();

        db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Users").document(uid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userInfo = documentSnapshot.toObject(User.class);

                //group list
                CollectionReference groupRef = db.collection("Group_1");
                if (userInfo.getGroupList() != null) {
                    groupRef.whereIn("groupId", userInfo.getGroupList()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            group = task.getResult().toObjects(Group.class);

                            List<String> supplierNames = new ArrayList<>();
                            for (int i = 0; i < group.size(); i++) {
                                supplierNames.add(group.get(i).getGroupName());
                            }

                            ArrayAdapter<String> grouplist = new ArrayAdapter<String>
                                    (AddExpenses.this, android.R.layout.simple_list_item_1, supplierNames);
                            grouplist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            groups.setAdapter(grouplist);

                            spinner_listener();
                        }

                    });
                }
            }
        });


        //amount has been type
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check the decimal point
                String str = amount.getText().toString();
                if (!str.isEmpty()) {
                    String str2 = PerfectDecimal(str, 3, 2);
                    if (!str2.equals(str)) {
                        amount.setText(str2);
                        amount.setSelection(str2.length());
                    }
                }
                cal_temp_result();
            }
        });


        //ready to split
        btn_split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btn_enable_check()) {
                    double amounts = 0;
                    String text = amount.getText().toString();
                    if (text.matches("")) {
                        amounts = 0;
                    } else {
                        amounts = Double.parseDouble(text);
                    }

                    split_user_count = chipGroup.getChildCount();
                    temp_result = sh.splitNormal(split_user_count, amounts);

                    spinner_listener();

                    ArrayList<String> split_user_list = new ArrayList<>();
                    for (int i = 0; i < chipGroup.getChildCount(); i++) {
                        String user = ((Chip) chipGroup.getChildAt(i)).getText().toString();
                        split_user_list.add(user);
                    }

                    split_to_database(split_user_list, temp_result);

                } else {
                    GeneralHelper.showMessage(AddExpenses.this, "Please fill in both Amount or Expenses");
                }

            }
        });

        btn_split_unequally.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btn_enable_check()) {

                    ArrayList<String> split_user_list = new ArrayList<>();
                    for (int i = 0; i < chipGroup.getChildCount(); i++) {
                        String user = ((Chip) chipGroup.getChildAt(i)).getText().toString();
                        split_user_list.add(user);
                    }


                    Intent intent = new Intent(AddExpenses.this, splitActivity.class);

                    //insert username
                    for (int i = 0; i < split_user_list.size(); i++) {
                        String temp_user = split_user_list.get(i);
                        intent.putExtra("users" + i, temp_user);
                    }

                    ArrayList<String> split_userid_list = find_user_id(split_user_list);
                    split_userid_list.add(0, userInfo.getUid());
                    //insert userid
                    for (int i = 0; i < split_userid_list.size(); i++) {
                        String temp_user = split_userid_list.get(i);
                        intent.putExtra("usersid" + i, temp_user);
                    }
                    intent.putExtra("amount", amount.getText().toString());
                    intent.putExtra("name", expenses_name.getText().toString());
                    intent.putExtra("group", cur_group.getGroupId());
                    startActivity(intent);
                    finish();
                } else {
                    GeneralHelper.showMessage(AddExpenses.this, "Please fill in both Amount or Expenses");
                }

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
    public void cal_temp_result() {

        double amounts = 0;
        String text = amount.getText().toString();

        if (text.matches("")
                || text.contains("-")
                || text.contains("\\")
                || text.contains("*")
                || text.contains("+")
                || text.contains("_")
        ) {
            amounts = 0;
        } else {
            amounts = Double.parseDouble(text);
        }
        split_user_count = chipGroup.getChildCount();
        temp_result = sh.splitNormal(split_user_count, amounts);
        temp_result = Math.round(temp_result * 100.0) / 100.0;

        result.setText("Split: RM" + temp_result);
    }


    //save amount to firebase
    public void split_to_database(ArrayList<String> Temp_userlist, double split_amount) {

        Date date = new Date();
        String str_date = new SimpleDateFormat("yyyy-MM-dd").format(date);
        ArrayList<String> splituser_id_list = find_user_id(Temp_userlist);
        splituser_id_list.add(0, userInfo.getUid());

        ArrayList<Double> split_amount_list = new ArrayList<>();

        for (int i = 0; i < splituser_id_list.size(); i++) {
            split_amount_list.add(split_amount);
        }

        sh.update_amount_list(splituser_id_list, split_amount_list, AddExpenses.this);

        mainactivity = new com.utar.assignment.Model.MainActivity();

        for (int i = 1; i < Temp_userlist.size(); i++) {
            subactivity = new SubActivity();
            subactivity.setPayerId(splituser_id_list.get(0));
            subactivity.setOwnerId(splituser_id_list.get(i));
            subactivity.setAmount(split_amount);
            subactivity.setCreatedDate(date);
            subactivity_List.add(subactivity);
        }
        mainactivity.setSubActivityList(subactivity_List);
        mainactivity.setName(expenses_name.getText().toString());
        mainactivity.setId(UUID.randomUUID().toString());
        mainactivity.setCreatedDate(str_date);

        double amounts;
        String temp_amount = amount.getText().toString();
        if (temp_amount.matches("")) {
            amounts = 0;
        } else {
            amounts = Double.parseDouble(temp_amount);
        }
        mainactivity.setBillAmount(amounts);

        if (cur_group.getMainActivityList() == null) {
            cur_group.setMainActivityList(new ArrayList<MainActivity>());
        }

        cur_group.getMainActivityList().add(mainactivity);

        FirestoreHelper.setGroup(cur_group, new FirebaseCallback() {
            @Override
            public void onResponse() {
                GeneralHelper.showMessage(AddExpenses.this, "Split Successfully");
                finish();
            }
        });

    }


    //add user for split
    public void add_user_chip(View view, String name) {

        Chip chip = new Chip(this);

        ChipDrawable drawable = ChipDrawable.createFromAttributes(this, null
                , 0, R.style.Widget_MaterialComponents_Chip_Entry);
        chip.setChipDrawable(drawable);
        chip.setCheckable(false);
        chip.setClickable(false);
        chip.setChipIconResource(R.drawable.ic_user);
        chip.setIconStartPadding(3f);
        chip.setPadding(60, 10, 60, 10);
        chip.setText(name);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip);
                cal_temp_result();
            }
        });

        chipGroup.addView(chip);
        cal_temp_result();
    }


    public ArrayList<String> find_user_id(ArrayList<String> Temp_userlist) {

        ArrayList<String> splituser_id_list = new ArrayList<>();

        for (int i = 0; i < Temp_userlist.size(); i++) {
            for (int j = 0; j < userList.size(); j++) {
                if (Temp_userlist.get(i).matches(userList.get(j).getUsername())) {
                    splituser_id_list.add(userList.get(j).getUid());
                    break;
                }
            }
        }
        return splituser_id_list;
    }


    public void spinner_listener() {
        groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                cur_group = group.get(position);
                db.collection("Users").whereArrayContains("groupList", group.get(position).getGroupId())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        chipGroup.removeAllViews();

                        //default user in chips group "You"
                        Chip default_chip = new Chip(AddExpenses.this);
                        ChipDrawable drawable = ChipDrawable.createFromAttributes(AddExpenses.this, null
                                , 0, R.style.Widget_MaterialComponents_Chip_Filter);
                        default_chip.setChipDrawable(drawable);
                        default_chip.setCheckable(false);
                        default_chip.setClickable(false);
                        default_chip.setChipIconResource(R.drawable.ic_user);
                        default_chip.setIconStartPadding(3f);
                        default_chip.setPadding(60, 10, 60, 10);
                        default_chip.setText("You");
                        chipGroup.addView(default_chip);

                        userList = task.getResult().toObjects(User.class);

                        for (int i = 0; i < userList.size(); i++) {

                            if (userList.get(i).getUsername().matches(userInfo.getUsername())) {
                                continue;
                            }

                            add_user_chip(view, userList.get(i).getUsername());
                        }

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public boolean btn_enable_check() {

        String temp1, temp2;
        temp1 = amount.getText().toString();
        temp2 = expenses_name.getText().toString();

        if (temp1.matches("") || temp2.matches("")) {

            return false;
        }

        return true;
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