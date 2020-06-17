package com.example.ohee.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.adapter.MessagesAdapter;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Chat;
import com.example.ohee.model.Group;
import com.example.ohee.model.Message;
import com.example.ohee.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView textViewNome;
    private CircleImageView circleImageView;
    private User usuarioDestinatario;
    private Group grupo;
    private EditText editMensagem;
    private RecyclerView recyclerMensagens;
    private ImageView imageCamera;

    private MessagesAdapter adapter;
    private List<Message> mensagens = new ArrayList<>();

    private User usuarioemetente;
    private String idUsuarioRemetente, idUsuarioDestinatario;

    private DatabaseReference database;
    private DatabaseReference mensagensRef;
    private StorageReference storage;

    private ChildEventListener childEventListenerMensagens;
    private static final int SELECAO_CAMERA = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewNome = findViewById(R.id.txtViewNomeChat);
        circleImageView = findViewById(R.id.circleImgFoto);
        editMensagem = findViewById(R.id.editMensagem);
        recyclerMensagens = findViewById(R.id.recyclerMensagens);
        imageCamera = findViewById(R.id.imageCamera);

        //recuperar dados do usurio remetente
        idUsuarioRemetente = SetFirebaseUser.getUsersId();
        usuarioemetente = SetFirebaseUser.getUserData();

        //Recuperar dados
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("chatGrupo")) {
                grupo = (Group) bundle.getSerializable("chatGrupo");
                idUsuarioDestinatario = grupo.getId();
                textViewNome.setText(grupo.getNome());
                String foto = grupo.getFoto();
                if (foto != null) {
                    Uri url = Uri.parse(foto);
                    Glide.with(ChatActivity.this)
                            .load(url)
                            .into(circleImageView);
                } else {
                    circleImageView.setImageResource(R.drawable.avatar);
                }

            } else {
                usuarioDestinatario = (User) bundle.getSerializable("chatContato");
                textViewNome.setText(usuarioDestinatario.getName());

                String foto = usuarioDestinatario.getPicturePath();
                if (foto != null) {
                    Uri url = Uri.parse(usuarioDestinatario.getPicturePath());
                    Glide.with(ChatActivity.this)
                            .load(url)
                            .into(circleImageView);
                } else {
                    circleImageView.setImageResource(R.drawable.avatar);
                }

                //recuperar dados suario destinatario
                idUsuarioDestinatario = usuarioDestinatario.getIdUser();
            }

        }

        database = SetFirebase.getFirebaseDatabase();
        storage = SetFirebase.getFirebaseStorage();
        mensagensRef = database.child("messages")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);

        //clique na camera
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, SELECAO_CAMERA);
                }
            }
        });

        //Confiurar adapter
        adapter = new MessagesAdapter(mensagens, getApplicationContext());

        //configurar Recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap img = null;

            try {
                switch (requestCode) {
                    case SELECAO_CAMERA :
                        img = (Bitmap) data.getExtras().get("data");
                        break;
                }

                String nomeFoto = UUID.randomUUID().toString();

                if (img != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imageRef = storage
                            .child("imagens")
                            .child("fotos")
                            .child(nomeFoto);


                    UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, "Erro", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ChatActivity.this, "Sucesso", Toast.LENGTH_SHORT).show();

                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            if (usuarioDestinatario != null) {
                                                Message mensagem = new Message();
                                                mensagem.setIdUsuario(idUsuarioRemetente);
                                                mensagem.setMensagem("image");
                                                mensagem.setImagem(uri.toString());

                                                salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                                                salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                                            } else {
                                                for (User membro : grupo.getMembros()) {
                                                    String idRemetenteGrupo = membro.getIdUser();
                                                    String idUsuarioLogadoGrupo = SetFirebaseUser.getUsersId();

                                                    Message mensagem = new Message();
                                                    mensagem.setIdUsuario(idUsuarioLogadoGrupo);
                                                    mensagem.setMensagem("img.jpeg");
                                                    mensagem.setNome(usuarioemetente.getName());
                                                    mensagem.setImagem(uri.toString());

                                                    //salvar para membro
                                                    salvarMensagem(idRemetenteGrupo, idUsuarioDestinatario, mensagem);

                                                    //salvar conversa p remetente
                                                    salvarConversa(idRemetenteGrupo, idUsuarioDestinatario, usuarioDestinatario, mensagem, true);
                                                }
                                            }
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

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener(childEventListenerMensagens);
    }

    public void enviarMensagem(View view) {
        String txtMensagem = editMensagem.getText().toString();
        if (!txtMensagem.isEmpty()) {
            if (usuarioDestinatario != null) {
                Message mensagem = new Message();
                mensagem.setIdUsuario(idUsuarioRemetente);
                mensagem.setMensagem(txtMensagem);

                //salvar para remetente
                salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                //salvar para destinatario
                salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                //salvar conversa p remetente
                salvarConversa(idUsuarioRemetente, idUsuarioDestinatario, usuarioDestinatario, mensagem, false);

                //salvar conversa p destinatario
                salvarConversa(idUsuarioDestinatario, idUsuarioRemetente, usuarioemetente, mensagem, false);
            } else {
                for (User membro : grupo.getMembros()) {
                    String idRemetenteGrupo = membro.getIdUser();
                    String idUsuarioLogadoGrupo = SetFirebaseUser.getUsersId();

                    Message mensagem = new Message();
                    mensagem.setIdUsuario(idUsuarioLogadoGrupo);
                    mensagem.setMensagem(txtMensagem);
                    mensagem.setNome(usuarioemetente.getName());

                    salvarMensagem(idRemetenteGrupo, idUsuarioDestinatario, mensagem);

                    //salvar conversa p remetente
                    salvarConversa(idRemetenteGrupo, idUsuarioDestinatario, usuarioDestinatario, mensagem, true);
                }
            }
        }
    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Message mensagem) {
        DatabaseReference database = SetFirebase.getFirebaseDatabase();
        DatabaseReference mensagemRef = database.child("messages");

        mensagemRef.child(idRemetente).child(idDestinatario).push().setValue(mensagem);

        editMensagem.setText("");
    }

    private void salvarConversa(String idRemetente, String idDestinatario, User usuarioExibicao, Message msg, boolean isGroup) {
        Chat conversaRemetente = new Chat();
        conversaRemetente.setIdRemetente(idRemetente);
        conversaRemetente.setIdDestinatario(idDestinatario);
        conversaRemetente.setUltimaMensagem(msg.getMensagem());
        if (isGroup) {
            conversaRemetente.setIsGroup("true");
            conversaRemetente.setGrupo(grupo);
        } else {
            conversaRemetente.setUsuarioExibicao(usuarioExibicao);
        }
        conversaRemetente.salvar();
    }

    private void recuperarMensagens() {
        mensagens.clear();
        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message mensagem = dataSnapshot.getValue(Message.class);
                mensagens.add(mensagem);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
