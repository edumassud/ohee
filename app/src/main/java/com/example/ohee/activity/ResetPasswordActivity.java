package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ohee.R;
import com.example.ohee.helpers.SetFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private TextInputEditText editEmail;
    private Button btReset;
    private ProgressBar progressBar;

    private FirebaseAuth auth = SetFirebase.getFirebaseAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editEmail       = findViewById(R.id.editEmail);
        btReset         = findViewById(R.id.btReset);
        progressBar     = findViewById(R.id.progressBar);

        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String txtEmail = editEmail.getText().toString();

                if (txtEmail != null && !txtEmail.isEmpty()) {
                    auth.sendPasswordResetEmail(txtEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                finish();
                            } else {
                                Toast.makeText(ResetPasswordActivity.this, task.getException() + "", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Please type in your email adress", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
