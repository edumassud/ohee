package com.example.ohee.model;

import com.example.ohee.helpers.SetFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {
    private String id, nome, foto;
    private List<User> membros;

    public Group() {
        DatabaseReference database = SetFirebase.getFirebaseDatabase();
        DatabaseReference grupoRef = database.child("groups");

        String idFirebase = grupoRef.push().getKey();

        setId(idFirebase);
    }

    public void salvar() {
        DatabaseReference database = SetFirebase.getFirebaseDatabase();
        DatabaseReference grupoRef = database.child("groups");

        grupoRef.child(getId()).setValue(this);

        //salvar conversa para membros do grupo
        for (User membro : getMembros()) {

            String idRemetente = membro.getIdUser();
            String idDestinatario = getId();

            Chat conversa = new Chat();
            conversa.setIdRemetente(idRemetente);
            conversa.setIdDestinatario(idDestinatario);
            conversa.setUltimaMensagem("");
            conversa.setIsGroup("true");
            conversa.setGrupo(this);

            conversa.salvar();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<User> getMembros() {
        return membros;
    }

    public void setMembros(List<User> membros) {
        this.membros = membros;
    }
}

