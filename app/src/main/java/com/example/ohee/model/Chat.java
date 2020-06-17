package com.example.ohee.model;


import com.example.ohee.helpers.SetFirebase;
import com.google.firebase.database.DatabaseReference;

public class Chat {
    private String idRemetente, idDestinatario, ultimaMensagem;
    private User usuarioExibicao;
    private String isGroup;
    private Group grupo;

    public Chat() {
        this.setIsGroup("false");
    }

    public void salvar() {
        DatabaseReference database = SetFirebase.getFirebaseDatabase();
        DatabaseReference conversaRef = database.child("chats");

        conversaRef.child(this.getIdRemetente()).child(this.getIdDestinatario())
                .setValue(this);
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Group getGrupo() {
        return grupo;
    }

    public void setGrupo(Group grupo) {
        this.grupo = grupo;
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public User getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(User usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }
}
