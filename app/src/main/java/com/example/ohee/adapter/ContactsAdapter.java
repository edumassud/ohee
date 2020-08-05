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

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    private List<User> contatos;
    private Context context;

    public ContactsAdapter(List<User> listaContatos, Context c) {
        this.contatos = listaContatos;
        this.context = c;
    }

    public List<User> getContatos() {
        return this.contatos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User usuario = contatos.get(position);
        boolean cabecalho = usuario.getEmail().isEmpty();

        holder.nome.setText(usuario.getName());
        holder.university.setText(usuario.getUniversityName());

        if (usuario.getPicturePath() != null) {
            Uri uri = Uri.parse(usuario.getPicturePath());
            Glide.with(context).load(uri).into(holder.foto);
            holder.foto.setRotation(usuario.getRotation());
        } else {
            if (cabecalho) {
                holder.foto.setImageResource(R.drawable.ic_group_add_black_24dp);
                holder.university.setVisibility(View.GONE);
            } else {
                holder.foto.setImageResource(R.drawable.avatar);
            }

        }

    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView foto;
        private TextView nome, university;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto        = itemView.findViewById(R.id.imgViewFotoContato);
            nome        = itemView.findViewById(R.id.txtNomeContato);
            university  = itemView.findViewById(R.id.txtUniversityContact);
        }
    }
}
