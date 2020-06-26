package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ohee.R;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailField, passwordField;
    private Button btLogin;
    private TextView txtCreateAcc, txtForgotPassword;
    private ProgressBar progressBar;

    private FirebaseAuth auth = SetFirebase.getFirebaseAuth();

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField          = findViewById(R.id.editEmail);
        passwordField       = findViewById(R.id.editPassword);
        btLogin             = findViewById(R.id.btLogin);
        txtCreateAcc        = findViewById(R.id.txtCreateAcc);
        progressBar         = findViewById(R.id.progressBar);
        txtForgotPassword   = findViewById(R.id.btForgotPassword);

        checkLogged();

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail = emailField.getText().toString();
                String txtPassword = passwordField.getText().toString();
                if (txtEmail.isEmpty() || txtPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Complete all fields", Toast.LENGTH_SHORT).show();
                } else {
                    user = new User(txtEmail, txtPassword);
                    validateLogin();
                }
            }
        });

       txtForgotPassword.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
           }
       });

        txtCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });
    }

    public void validateLogin() {
        progressBar.setVisibility(View.VISIBLE);
        auth = SetFirebase.getFirebaseAuth();
        auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    if (auth.getCurrentUser().isEmailVerified()) {
                        goToMain();
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Your email hasn't been verified yet.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    String exception = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        exception = "Ivalid user";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exception = "Email and password do not match";
                    } catch (Exception e) {
                        exception = "Error: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_LONG).show();
                    Log.i("Erro", exception);
                }
            }
        });
    }

    public void checkLogged () {
        //auth.signOut();
        if (auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()) {
            goToMain();
        }
    }

    public void goToMain() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

}
