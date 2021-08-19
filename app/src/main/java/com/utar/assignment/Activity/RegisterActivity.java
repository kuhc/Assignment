package com.utar.assignment.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.FirebaseCallback;
import com.utar.assignment.Util.FirestoreHelper;
import com.utar.assignment.Util.GeneralHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtName, txtEmail, txtPassword, txtPasswordRepeat;
    private TextView txtLogin;
    private Button btnRegister;
    private ProgressBar progressBar;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtName = findViewById(R.id.register_txtName);
        txtEmail = findViewById(R.id.register_txtEmail);
        txtPassword = findViewById(R.id.register_txtPassword);
        txtPasswordRepeat = findViewById(R.id.register_txtPasswordRepeat);
        txtLogin = findViewById(R.id.register_txtLogin);
        btnRegister = findViewById(R.id.register_btnRegister);
        progressBar = findViewById(R.id.register_pb);

        fAuth = FirebaseAuth.getInstance();


        setListenerToBtnRegister();
        setListenerToTxtLogin();

    }

    private void setListenerToTxtLogin() {
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    private void setListenerToBtnRegister() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txtName.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                String password2 = txtPasswordRepeat.getText().toString().trim();

                boolean check = true;

                if (TextUtils.isEmpty(name)) {
                    txtName.setError("Name is required.");
                    check = false;
                }

                if (TextUtils.isEmpty(email)) {
                    txtEmail.setError("Email is required.");
                    check = false;
                }

                if (TextUtils.isEmpty(password)) {
                    txtPassword.setError("Password is required.");
                    check = false;
                }

                if (TextUtils.isEmpty(password2)) {
                    txtPasswordRepeat.setError("Repeat password is required.");
                    check = false;
                }

                if (!password.equals(password2)) {
                    txtPassword.setError("Password does not match.");
                    txtPasswordRepeat.setError("Password does not match.");
                    check = false;
                }

                if (!check)
                    return;

                progressBar.setVisibility(View.VISIBLE);
                btnRegister.setVisibility(View.INVISIBLE);

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = fAuth.getCurrentUser().getUid();
                            User user = new User(userId, name, email);

                            FirestoreHelper.setUser(user, new FirebaseCallback() {
                                @Override
                                public void onResponse() {
                                    GeneralHelper.showMessage(getBaseContext(), "Successfully registered!");
                                    progressBar.setVisibility(View.INVISIBLE);
                                    btnRegister.setVisibility(View.VISIBLE);
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            GeneralHelper.showMessage(getBaseContext(), "Error! " + task.getException().getMessage());
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}
