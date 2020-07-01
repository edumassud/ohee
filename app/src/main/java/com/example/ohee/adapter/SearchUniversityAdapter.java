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
import com.example.ohee.model.University;
import com.example.ohee.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUniversityAdapter extends RecyclerView.Adapter<SearchUniversityAdapter.MyViewHolder> {
    private List<University> list;
    private Context context;

    public SearchUniversityAdapter(List<University> list, Context context) {
        this.list = list;
        this.context = context;
    }



    public List<University> getList() {
        return list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.adapter_search_university, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        University university = list.get(position);
        holder.txtUniversity.setText(university.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUniversity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUniversity = itemView.findViewById(R.id.txtUniversity);

        }
    }
}
