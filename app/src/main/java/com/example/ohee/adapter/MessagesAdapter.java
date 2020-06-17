package com.example.ohee.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Message;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {
    private List<Message> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;

    public MessagesAdapter(List<Message> mensagens, Context context) {
        this.mensagens = mensagens;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = null;
        if (viewType == TIPO_REMETENTE) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_remetente, parent, false);
        } else if (viewType == TIPO_DESTINATARIO) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_destinatario, parent, false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message mensagem = mensagens.get(position);
        String nome = mensagem.getNome();
        if (mensagem.getImagem() != null) {
            Uri url = Uri.parse(mensagem.getImagem());
            Glide.with(context).load(url).into(holder.imagem);
            holder.mensagem.setVisibility(View.GONE);
            if (!nome.isEmpty()) {
                holder.nome.setText(nome);
            } else {
                holder.nome.setVisibility(View.GONE);
            }
        } else {
            holder.mensagem.setText(mensagem.getMensagem());
            holder.imagem.setVisibility(View.GONE);
            if (!nome.isEmpty()) {
                holder.nome.setText(nome);
            } else {
                holder.nome.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message mensagem = mensagens.get(position);
        String idUsuario = SetFirebaseUser.getUsersId();

        if (idUsuario.equals(mensagem.getIdUsuario())) {
            return TIPO_REMETENTE;
        }

        return TIPO_DESTINATARIO;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mensagem;
        ImageView imagem;
        TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mensagem = itemView.findViewById(R.id.textMensagemTexto);
            imagem = itemView.findViewById(R.id.imageMensagemFoto);
            nome = itemView.findViewById(R.id.textNomeExibicao);
        }
    }
}
