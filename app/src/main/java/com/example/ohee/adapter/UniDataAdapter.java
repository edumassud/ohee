package com.example.ohee.adapter;

import android.content.Context;
import android.icu.text.CollationElementIterator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ohee.R;
import com.example.ohee.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UniDataAdapter extends RecyclerView.Adapter<UniDataAdapter.MyViewHolder> {
    private List<String> universities;
    private Context context;

    public UniDataAdapter(List<String> universities, Context context) {
        this.universities = universities;
        this.context = context;
    }

    private List<String> removeDuplicates() {
        List<String> universitiesNoDuplicates = new ArrayList<>();
        for (String uni : universities) {
            if (!universitiesNoDuplicates.contains(uni)) {
                universitiesNoDuplicates.add(uni);
            }
        }
        return universitiesNoDuplicates;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_uni_data, parent, false);
        return new UniDataAdapter.MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String university = removeDuplicates().get(position);

        float count = Collections.frequency(universities, university);

        holder.txtName.setText(university);
//        holder.txtCount.setText(count + "");
        holder.txtCount.setText(Math.round((count/universities.size()) * 100) + " %");
    }


    @Override
    public int getItemCount() {
        return removeDuplicates().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtCount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName  = itemView.findViewById(R.id.txtName);
            txtCount = itemView.findViewById(R.id.txtCount);
        }
    }
}
