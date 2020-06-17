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
import com.example.ohee.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedGroupAdapter extends RecyclerView.Adapter<SelectedGroupAdapter.MyViewHolder> {
    private List<User> usuariosSelecionados;
    private Context context;

    public SelectedGroupAdapter(List<User> usuariosSelecionados, Context context) {
        this.usuariosSelecionados = usuariosSelecionados;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_grupo_selecionado, parent, false);
        return new SelectedGroupAdapter.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User usuario = usuariosSelecionados.get(position);

        holder.nome.setText(usuario.getName());


        if (usuario.getPicturePath() != null) {
            Uri uri = Uri.parse(usuario.getPicturePath());
            Glide.with(context).load(uri).into(holder.foto);
        } else {
            holder.foto.setImageResource(R.drawable.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return usuariosSelecionados.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView foto;
        private TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewFotoMembroSelecionado);
            nome = itemView.findViewById(R.id.txtNoneMembroSelecionado);
        }
    }
}
