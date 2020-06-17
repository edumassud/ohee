package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.helpers.Permission;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private CircleImageView editProfileImg;
    private TextView txtChangePic;
    private TextInputEditText editName, editBio, emailField, universityField;
    private Button btLogOut, btSave;

    private String[] permissionsRequired = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private FirebaseAuth auth = SetFirebase.getFirebaseAuth();
    private StorageReference storageRef = SetFirebase.getFirebaseStorage();
    private User loggedUser = SetFirebaseUser.getUserData();
    private String userId = SetFirebaseUser.getUsersId();
    private FirebaseUser user = SetFirebaseUser.getUser();
    private DatabaseReference userRef = SetFirebase.getFirebaseDatabase().child("user").child(user.getUid());

    private static final int GALLERY = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Permissions
        Permission.validatePermissions(permissionsRequired, this, 1);

        // Screen components
        editProfileImg      = findViewById(R.id.editProfilePic);
        txtChangePic        = findViewById(R.id.txtChangePic);
        editName            = findViewById(R.id.editName);
        editBio             = findViewById(R.id.editBio);
        emailField          = findViewById(R.id.editEmail);
        universityField     = findViewById(R.id.editUniversity);
        btLogOut            = findViewById(R.id.btLogOut);
        btSave              = findViewById(R.id.btSave);

        emailField.setFocusable(false);
        universityField.setFocusable(false);

        // Set the data
        FirebaseUser userFB = SetFirebaseUser.getUser();
        editName.setText(userFB.getDisplayName());
        emailField.setText(userFB.getEmail());
        Uri url = userFB.getPhotoUrl();
        if (url != null) {
            Glide.with(EditProfileActivity.this)
                    .load(url)
                    .into(editProfileImg);
        } else {
            editProfileImg.setImageResource(R.drawable.avatar);
        }


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String bio = user.getBio();
                String university = user.getUniversity();

                editBio.setText(bio);
                universityField.setText(university);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editName.getText().toString();
                String newBio = editBio.getText().toString();

                SetFirebaseUser.updateUsersName(newName);

                loggedUser.setName(newName);
                loggedUser.setBio(newBio);
                loggedUser.updateInfo();
                finish();
            }
        });

        txtChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, GALLERY);
                }
            }
        });

        editProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, GALLERY);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap image = null;

            try {
                switch (requestCode) {
                    case GALLERY :
                        Uri selectedImg = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImg);
                        break;
                }
                if (image != null) {
                    // Place on screen
                    editProfileImg.setImageBitmap(image);

                    // Img data
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imgData = baos.toByteArray();

                    // Save to firebase
                    StorageReference imageRef = storageRef
                            .child("images")
                            .child("profile")
                            .child(userId + ".jpeg");
                    UploadTask uploadTask = imageRef.putBytes(imgData);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Error uploading the image", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    updateUsersPic(uri);
                                }
                            });
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUsersPic(Uri uri) {
        // Atualizar foto no perfil
        SetFirebaseUser.updateUsersPhoto(uri);

        // Atualizar foto no firebase
        loggedUser.setPicturePath(uri.toString());
        loggedUser.updateImg();
        Toast.makeText(this, "Picture succesfully updated", Toast.LENGTH_SHORT).show();
    }
}
