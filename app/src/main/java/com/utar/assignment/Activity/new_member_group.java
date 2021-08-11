package com.utar.assignment.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.utar.assignment.R;

public class new_member_group extends AppCompatActivity {
    Button new_member_but1;
    EditText new_group_name1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member_group);

        new_group_name1=findViewById(R.id.new_group_name);
        new_member_but1=findViewById(R.id.new_member_but);
        new_member_but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(new_member_group.this, GroupMember.class);
                intent.putExtra("g_name", new_group_name1.getText().toString());
                new_member_group.this.startActivity(intent);
            }
        });


    }
}