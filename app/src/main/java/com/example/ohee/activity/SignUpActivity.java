package com.example.ohee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ohee.R;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.University;
import com.example.ohee.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.ActionCodeSettings;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public class SignUpActivity extends AppCompatActivity {

    public interface GetDataService {
        @GET("/whoisserver/WhoisService")
        Call<String> getUniversity(@QueryMap Map<String, String> params);
    }

    private final static String WHO_IS_API_KEY = "at_Ug8nDYbZKxPmyPI2Nfx7TWL0xbk9g";

    private TextInputEditText nameField, emailField, passwordField, confirmPasswordField, userNameField;
    private TextView btHighSchooler;
    private Button btSignUp;
    private ProgressBar progressBar;

    private FirebaseAuth auth = SetFirebase.getFirebaseAuth();
    private User user;
    private DatabaseReference usersRef = SetFirebase.getFirebaseDatabase().child("user");

    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userNameField           = findViewById(R.id.editUserName);
        nameField               = findViewById(R.id.editName);
        emailField              = findViewById(R.id.editEmail);
        passwordField           = findViewById(R.id.editPassword);
        confirmPasswordField    = findViewById(R.id.editConfirmPassword);
        btSignUp                = findViewById(R.id.btSignup);
        progressBar             = findViewById(R.id.progressBar);
        btHighSchooler          = findViewById(R.id.btHighSchooler);

        String BASE_URL = "https://www.whoisxmlapi.com";

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        btHighSchooler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HSSignUpActivity.class));
            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUserName = userNameField.getText().toString();
                String txtName = nameField.getText().toString();
                String txtEmail = emailField.getText().toString();
                String txtPassword = passwordField.getText().toString();
                String txtConfirmPassword = confirmPasswordField.getText().toString();

                if (txtName.isEmpty() || txtEmail.isEmpty() || txtPassword.isEmpty() || txtConfirmPassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Complete all fields", Toast.LENGTH_SHORT).show();
                } else if(txtUserName.length() < 2) {
                    Toast.makeText(SignUpActivity.this, "Username must have more than 1 character", Toast.LENGTH_SHORT).show();
                } else if(!isValidUsername(txtUserName)) {
                    Toast.makeText(SignUpActivity.this, "Username invalid", Toast.LENGTH_SHORT).show();
                } else if(!checkUserName(txtUserName)){
                    Toast.makeText(SignUpActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                }  else if(txtEmail.length() <= 5) {
                    Toast.makeText(SignUpActivity.this, "Email is invalid", Toast.LENGTH_SHORT).show();
                } else if (!txtEmail.substring(txtEmail.length() - 4).equals(".edu")) {
                    Toast.makeText(SignUpActivity.this, "Email is not a college email", Toast.LENGTH_SHORT).show();
                    emailField.setText("");
                } else if (!txtConfirmPassword.equals(txtPassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    confirmPasswordField.setText("");
                } else {
                    Query searchUsername = usersRef.orderByChild("userName").equalTo(txtUserName);

                    searchUsername.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(SignUpActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
                                String domain = txtEmail.substring(txtEmail.lastIndexOf("@") + 1).replace(".", "");
                                DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
                                DatabaseReference universityRef = databaseReference
                                        .child("universities")
                                        .child(domain);

                                universityRef.addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    University university = dataSnapshot.getValue(University.class);
                                                    user = new User(txtUserName.toLowerCase(), txtName, txtEmail, txtPassword, university.getName(), university.getDomain());
                                                    user.setSearchName(txtName.toUpperCase());

                                                    signUpUser(university, false);

                                                } else {
                                                    Map<String, String> params = new HashMap<String, String>();
                                                    params.put("apiKey", WHO_IS_API_KEY);
                                                    params.put("domainName", txtEmail);
                                                    params.put("outputFormat", "JSON");

                                                    GetDataService service = retrofit.create(GetDataService.class);
                                                    Call<String> call = service.getUniversity(params);
                                                    call.enqueue(new Callback<String>() {
                                                        @Override
                                                        public void onResponse(Call<String> call, Response<String> response) {

                                                            try {
                                                                JSONObject responseObject = new JSONObject(response.body());
                                                                University university = new University(responseObject);
                                                                university.setDomain(domain);
                                                                university.save();

                                                                // Increment university count
                                                                university.setCount(university.getCount() + 1);
                                                                university.update();

                                                                user = new User(txtUserName, txtName, txtEmail, txtPassword, university.getName(), university.getDomain());
                                                                user.setSearchName(txtName.toUpperCase());

                                                                signUpUser(university, true);

                                                            }catch (JSONException e) {
                                                                Log.e("ERROR", e.getLocalizedMessage());
                                                            }

                                                        }

                                                        @Override
                                                        public void onFailure(Call<String> call, Throwable t) {
                                                            Log.e("PASSEI", "FALHOU");
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        }
                                );
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
//        String regex = "^[aA-zZ]\\w{5,29}$";
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
                    Toast.makeText(SignUpActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return true;
    }

    public void signUpUser(University university, boolean isNew) {
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
                                        if (!isNew) {
                                            // Increment university count
                                            university.setCount(university.getCount() + 1);
                                            university.update();
                                        }
                                        Toast.makeText(SignUpActivity.this, "Please verify you email.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );

                    String idUser = task.getResult().getUser().getUid();
                    user.setIdUser(idUser);
                    user.save();
                    university.addUser(user.getIdUser());
                    SetFirebaseUser.updateUsersName(user.getName());
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
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
                    Toast.makeText(SignUpActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
