package com.utar.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.utar.assignment.Fragment.Adjustment_Add_Fragment;
import com.utar.assignment.Fragment.FriendFragment;
import com.utar.assignment.Fragment.GroupFragment;
import com.utar.assignment.Fragment.HomeFragment;
import com.utar.assignment.Fragment.Percentage_Add_Fragment;
import com.utar.assignment.Fragment.ProfileFragment;
import com.utar.assignment.Fragment.Unequal_Add_Fragment;
import com.utar.assignment.R;

public class splitActivity extends AppCompatActivity {

    private BottomNavigationView splitMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);

        splitMenu = findViewById(R.id.split_menu);
        splitMenu.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.split_container, new Unequal_Add_Fragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId())
            {

                case R.id.split_unequal:
                    selectedFragment = new Unequal_Add_Fragment();
                    break;

                case R.id.split_adjustment:

                    selectedFragment = new Adjustment_Add_Fragment();
                    break;

                /*case R.id.split_percentage:
                    selectedFragment = new Percentage_Add_Fragment();
                    break;*/
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.split_container, selectedFragment).commit();

            return true;
        }
    };
}