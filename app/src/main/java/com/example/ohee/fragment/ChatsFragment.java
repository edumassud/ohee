package com.example.ohee.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ohee.R;
import com.example.ohee.activity.ChatActivity;
import com.example.ohee.adapter.ChatAdapter;
import com.example.ohee.helpers.RecyclerItemClickListener;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Chat;
import com.example.ohee.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    private RecyclerView recyclerViewConversas;
    private List<Chat> listaConversas = new ArrayList<>();
    private ChatAdapter adapter;
    private DatabaseReference database;
    private DatabaseReference conversasRef;
    private ChildEventListener childEventListenerConversas;

    public ChatsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        recyclerViewConversas   = view.findViewById(R.id.recyclerViewListaConversas);

        //Config adapter
        adapter = new ChatAdapter(listaConversas, getActivity());

        //config recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversas.setLayoutManager(layoutManager);
        recyclerViewConversas.setHasFixedSize(true);
        recyclerViewConversas.setAdapter(adapter);

        //congif clique
        recyclerViewConversas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(), recyclerViewConversas, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        List<Chat> listaConversasAtualizada = adapter.getConversas();
                        Chat conversaSelecionada = listaConversasAtualizada.get(position);
                        User usuarioSelecionado = conversaSelecionada.getUsuarioExibicao();

                        if (conversaSelecionada.getIsGroup().equals("true")) {
                            Intent i = new Intent(getActivity(), ChatActivity.class);
                            i.putExtra("chatGrupo", conversaSelecionada.getGrupo());
                            startActivity(i);
                        } else {
                            Intent i = new Intent(getActivity(), ChatActivity.class);
                            i.putExtra("chatContato", usuarioSelecionado);
                            startActivity(i);
                        }


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
                )
        );


        String identificadorUsuario = SetFirebaseUser.getUsersId();
        database = SetFirebase.getFirebaseDatabase();
        conversasRef = database.child("chats")
                .child(identificadorUsuario);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListenerConversas);
    }


    public void recuperarConversas() {
        listaConversas.clear();
        childEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat conversa = dataSnapshot.getValue(Chat.class);
                listaConversas.add(conversa);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void pesquisarConversas(String texto) {
        List<Chat> listaConversasBusca = new ArrayList<>();

        for (Chat conversa : listaConversas) {
            if (conversa.getUsuarioExibicao() != null) {
                String nome = conversa.getUsuarioExibicao().getName().toLowerCase();
                String ultimaMensagem = conversa.getUltimaMensagem().toLowerCase();
                if (nome.contains(texto) || ultimaMensagem.contains(texto)) {
                    listaConversasBusca.add(conversa);
                }
            } else {
                String nome = conversa.getGrupo().getNome().toLowerCase();
                String ultimaMensagem = conversa.getUltimaMensagem().toLowerCase();
                if (nome.contains(texto) || ultimaMensagem.contains(texto)) {
                    listaConversasBusca.add(conversa);
                }
            }

        }

        adapter = new ChatAdapter(listaConversasBusca, getActivity());
        recyclerViewConversas.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recarregarConversas() {
        adapter = new ChatAdapter(listaConversas, getActivity());
        recyclerViewConversas.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
