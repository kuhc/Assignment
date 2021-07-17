package com.utar.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.utar.assignment.R;
import com.utar.assignment.Util.GeneralHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private TextView txtRegister, txtForgotPassword;
    private Button btnRegister;
    private ProgressBar progressBar;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.login_txtEmail);
        txtPassword = findViewById(R.id.login_txtPassword);
        btnRegister = findViewById(R.id.login_btnLogin);
        txtRegister = findViewById(R.id.login_txtRegister);
        txtForgotPassword = findViewById(R.id.login_txtForgotPassword);
        progressBar = findViewById(R.id.login_pb);

        fAuth = FirebaseAuth.getInstance();

        // Check if User is logged in
        if(fAuth.getCurrentUser()  != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        setListenerToBtnLogin();
        setListnerToTxtRegister();
        setListenerToTxtForgotPassword();
    }

    private void setListenerToTxtForgotPassword() {
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start alert dialog
                View view = getLayoutInflater().inflate(R.layout.layout_reset_password, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                builder.setTitle("Reset Forgot Password?")
                        .setMessage("Enter Your Email to Get Password Reset Link.")
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Validate Email Address
                                EditText txtResetEmail = view.findViewById(R.id.txtResetEmail);
                                if (txtResetEmail.getText().toString().isEmpty()) {
                                    txtResetEmail.setError("Email is Required.");
                                }

                                // Send the Reset Link
                                fAuth.sendPasswordResetEmail(txtResetEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        GeneralHelper.showMessage(getBaseContext(), "Reset Email Sent!");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        GeneralHelper.showMessage(getBaseContext(), e.getMessage());
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", null)
                        .setView(view)
                        .create().show();
            }
        });
    }

    private void setListnerToTxtRegister() {
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    private void setListenerToBtnLogin() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                // Check Email
                if(TextUtils.isEmpty(email)) {
                    txtEmail.setError("Email is required.");
                    return;
                }

                // Check Password
                if(TextUtils.isEmpty(password)) {
                    txtPassword.setError("Password is required.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                btnRegister.setVisibility(View.INVISIBLE);

                // Authentication via Firebase
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            GeneralHelper.showMessage(getBaseContext(), "Login successfully!");
                            txtEmail.setText("");
                            txtPassword.setText("");
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            GeneralHelper.showMessage(getBaseContext(), "Error! " + task.getException().getMessage());
                            progressBar.setVisibility(View.INVISIBLE);
                            btnRegister.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }
}