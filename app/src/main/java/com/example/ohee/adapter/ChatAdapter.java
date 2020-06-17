package com.example.ohee.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.model.Chat;
import com.example.ohee.model.Group;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<Chat> conversas;
    private Context c;

    public ChatAdapter(List<Chat> conversas, Context c) {
        this.conversas = conversas;
        this.c = c;
    }

    public List<Chat> getConversas() {
        return this.conversas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chat conversa = conversas.get(position);

        if (conversa.getIsGroup().equals("true")) {
            Group grupo = conversa.getGrupo();
            holder.nome.setText(grupo.getNome());
            if (grupo.getFoto() != null) {
                if (conversa.getUsuarioExibicao().getPicturePath() == null) {
                    holder.foto.setImageResource(R.drawable.avatar);
                } else {
                    Uri uri = Uri.parse(conversa.getUsuarioExibicao().getPicturePath());
                    Glide.with(c).load(uri).into(holder.foto);
                }
            } else {
                holder.foto.setImageResource(R.drawable.avatar);
            }
        } else {
            if (conversa.getUsuarioExibicao() != null) {
                holder.nome.setText(conversa.getUsuarioExibicao().getName());
                if (conversa.getUsuarioExibicao().getPicturePath() != null) {
                    Uri uri = Uri.parse(conversa.getUsuarioExibicao().getPicturePath());
                    Glide.with(c).load(uri).into(holder.foto);
                } else {
                    holder.foto.setImageResource(R.drawable.avatar);
                }
            }
        }
        holder.ultimaMensagem.setText(conversa.getUltimaMensagem());
    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView foto;
        private TextView nome, ultimaMensagem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imgViewFotoContato);
            nome = itemView.findViewById(R.id.txtNomeContato);
            ultimaMensagem = itemView.findViewById(R.id.txtUniversityContact);
        }
    }
}
