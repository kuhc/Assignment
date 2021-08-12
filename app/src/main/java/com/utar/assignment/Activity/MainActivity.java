package com.utar.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utar.assignment.Fragment.FriendFragment;
import com.utar.assignment.Fragment.GroupFragment;
import com.utar.assignment.Fragment.HomeFragment;
import com.utar.assignment.Fragment.ProfileFragment;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.FirebaseCallback;
import com.utar.assignment.Util.FirestoreHelper;
import com.utar.assignment.Util.GeneralHelper;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Run Hms Push Kit
        HmsPushKit();

        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId())
            {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_group:
                    selectedFragment = new GroupFragment();
                    break;
                case R.id.nav_friend:
                    selectedFragment = new FriendFragment();
                    break;
                 case R.id.nav_profile:
                    selectedFragment = new ProfileFragment();
                    break;

            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };

    private void HmsPushKit() {
        FirestoreHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), new FirebaseCallback() {
            @Override
            public void onResponse(Object object) {
                user = (User) object;

                // Check User Token
                if(user.getPushToken() == null || user.getPushToken().isEmpty()) {
                    GeneralHelper.getToken(getApplicationContext(), user);
                }
            }
        });
    }
}