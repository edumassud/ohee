package com.example.ohee.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ohee.R;
import com.example.ohee.activity.FilterActivity;
import com.example.ohee.helpers.Permission;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {
    private Button btOpenGallery, btOpenCamera;

    private static final int CAMERA_SELECTION = 100;
    private static final int GALLERY_SELECTION = 200;

    private String[] permissionsRequired = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_post, container, false);

        // Permissions
        Permission.validatePermissions(permissionsRequired, getActivity(), 1);

        btOpenCamera = view.findViewById(R.id.btOpenCamera);
        btOpenGallery = view.findViewById(R.id.btOpenGallery);

        btOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(i, CAMERA_SELECTION);
                }
            }
        });

        btOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(i, GALLERY_SELECTION);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && data != null) {
            Bitmap img = null;

            try {
                switch (requestCode) {
                    case CAMERA_SELECTION:
                        img = (Bitmap) data.getExtras().get("data");
                        break;
                    case GALLERY_SELECTION:
                        Uri selectedImage = data.getData();
                        img = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                }

                if (img != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imgData = baos.toByteArray();

                    // Send to filter application
                    Intent i = new Intent(getActivity(), FilterActivity.class);
                    i.putExtra("selectedPic", imgData);
                    startActivity(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
