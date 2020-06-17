package com.example.ohee.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ohee.R;
import com.example.ohee.adapter.SelectedGroupAdapter;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Group;
import com.example.ohee.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupFinalizationActivity extends AppCompatActivity {
    private List<User> listaMembrosSelecionados = new ArrayList<>();
    private TextView textTotalParticipantes;
    private FloatingActionButton fabSalvarGrupo;
    private EditText editNomeGRupo;
    private SelectedGroupAdapter adapter;
    private RecyclerView recyclerMembros;
    private CircleImageView imageGrupo;
    private Group grupo;

    private StorageReference storageReference;

    private static final int SELECAO_GALERIA = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("New group");
        toolbar.setSubtitle("Last step");
        setSupportActionBar(toolbar);

        storageReference = SetFirebase.getFirebaseStorage();
        textTotalParticipantes = findViewById(R.id.textTotalParticipantes);
        recyclerMembros = findViewById(R.id.recyclerMembrosGrupo);
        fabSalvarGrupo = findViewById(R.id.fabSalvarGrupo);
        editNomeGRupo = findViewById(R.id.editNomeGrupo);
        imageGrupo = findViewById(R.id.imageGrupo);
        grupo = new Group();

        imageGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, SELECAO_GALERIA);
                }
            }
        });

        //Recuperar lista de membros
        if (getIntent().getExtras() != null) {
            List<User> membros = (List<User>) getIntent().getExtras().getSerializable("membros");
            listaMembrosSelecionados.addAll(membros);

            textTotalParticipantes.setText("Members: " + listaMembrosSelecionados.size());
        }

        //config adapter
        adapter = new SelectedGroupAdapter(listaMembrosSelecionados, getApplicationContext());

        //config recycler
        RecyclerView.LayoutManager layoutManagerH = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerMembros.setLayoutManager(layoutManagerH);
        recyclerMembros.setHasFixedSize(true);
        recyclerMembros.setAdapter(adapter);

        fabSalvarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeGrupo = editNomeGRupo.getText().toString();

                if (!nomeGrupo.isEmpty()) {
                    listaMembrosSelecionados.add(SetFirebaseUser.getUserData());
                    grupo.setNome(nomeGrupo);
                    grupo.setMembros(listaMembrosSelecionados);
                    grupo.salvar();

                    Intent i = new Intent(GroupFinalizationActivity.this, ChatActivity.class);
                    i.putExtra("chatGrupo", grupo);
                    startActivity(i);
                } else {
                    Toast.makeText(GroupFinalizationActivity.this, "Choose a name for your group", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{
                Uri localImagemSelecionada = data.getData();
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);

                if (imagem != null){
                    imageGrupo.setImageBitmap(imagem);

                    //recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //salvar imagem no firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("groups")
                            .child(grupo.getId() + ".jpeg");

                    //códigos atualizados para o UploadTask das novas bibliotecas
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GroupFinalizationActivity.this,
                                    "Erro ao fazer upload!", Toast.LENGTH_SHORT).show();
                        }

                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(GroupFinalizationActivity.this,
                                    "Sucesso ao fazer upload!", Toast.LENGTH_SHORT).show();

                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //converter para string diretamente
                                            grupo.setFoto(uri.toString());
                                        }
                                    });
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
