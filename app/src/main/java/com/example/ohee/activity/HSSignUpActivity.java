package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ohee.R;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.University;
import com.example.ohee.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HSSignUpActivity extends AppCompatActivity {
    private TextInputEditText nameField, emailField, passwordField, confirmPasswordField, userNameField;
    private Button btSignUp;
    private ProgressBar progressBar;

    private FirebaseAuth auth = SetFirebase.getFirebaseAuth();
    private HighSchooler user;
    private DatabaseReference usersRef = SetFirebase.getFirebaseDatabase().child("highschoolers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h_s_sign_up);

        userNameField           = findViewById(R.id.editUserName);
        nameField               = findViewById(R.id.editName);
        emailField              = findViewById(R.id.editEmail);
        passwordField           = findViewById(R.id.editPassword);
        confirmPasswordField    = findViewById(R.id.editConfirmPassword);
        btSignUp                = findViewById(R.id.btSignup);
        progressBar             = findViewById(R.id.progressBar);

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUserName          = userNameField.getText().toString();
                String txtName              = nameField.getText().toString();
                String txtEmail             = emailField.getText().toString();
                String txtPassword          = passwordField.getText().toString();
                String txtConfirmPassword   = confirmPasswordField.getText().toString();

                if (txtName.isEmpty() || txtEmail.isEmpty() || txtPassword.isEmpty() || txtConfirmPassword.isEmpty()) {
                    Toast.makeText(HSSignUpActivity.this, "Complete all fields", Toast.LENGTH_SHORT).show();
                } else if(txtUserName.length() < 2) {
                    Toast.makeText(HSSignUpActivity.this, "Username must have more than 1 character", Toast.LENGTH_SHORT).show();
                } else if(!isValidUsername(txtUserName)) {
                    Toast.makeText(HSSignUpActivity.this, "Username invalid", Toast.LENGTH_SHORT).show();
                } else if(!checkUserName(txtUserName)){
                    Toast.makeText(HSSignUpActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                }  else if(txtEmail.length() <= 5) {
                    Toast.makeText(HSSignUpActivity.this, "Email is invalid", Toast.LENGTH_SHORT).show();
                } else if (txtEmail.substring(txtEmail.length() - 4).equals(".edu")) {
                    Toast.makeText(HSSignUpActivity.this, "Email is a college email", Toast.LENGTH_SHORT).show();
                    emailField.setText("");
                } else if (!txtConfirmPassword.equals(txtPassword)) {
                    Toast.makeText(HSSignUpActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    confirmPasswordField.setText("");
                } else {
                    Query searchUsername = usersRef.orderByChild("userName").equalTo(txtUserName);

                    searchUsername.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(HSSignUpActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
                                user = new HighSchooler(txtUserName.toLowerCase(), txtName, txtEmail, txtPassword);
                                user.setSearchName(txtName.toUpperCase());
                                signUp();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }
        });
    }

    private boolean isValidUsername(String name) {
        String regex = "^(?!.*\\.\\.)(?!.*\\.$)[^\\W][\\w.]{0,29}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);

        // Return if the username
        // matched the ReGex
        return m.matches();
    }

    private boolean checkUserName(String txtUserName) {
        Query searchUsername = usersRef.orderByChild("username").equalTo(txtUserName);

        searchUsername.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(HSSignUpActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return true;
    }

    public void signUp() {
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);

                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(HSSignUpActivity.this, "Please verify you email.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(HSSignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );

                    String idUser = task.getResult().getUser().getUid();
                    user.setIdUser(idUser);
                    user.save();
                    SetFirebaseUser.updateUsersName(user.getName());
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//                    finish();
                } else {
                    progressBar.setVisibility(View.GONE);
                    String exception = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        exception = "Password is too weak";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exception = "Email is not valid";
                    } catch (FirebaseAuthUserCollisionException e) {
                        exception = "This email has already been used";
                    }catch (Exception e) {
                        exception = "Error: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(HSSignUpActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}