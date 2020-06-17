package com.example.ohee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ohee.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class AdapterFilters extends RecyclerView.Adapter<AdapterFilters.MyViewHolder> {
    private List<ThumbnailItem> listFilters;
    private Context context;

    public AdapterFilters(List<ThumbnailItem> listFilters, Context context) {
        this.listFilters = listFilters;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_filters, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ThumbnailItem item = listFilters.get(position);
        holder.pic.setImageBitmap(item.image);
        holder.filterName.setText(item.filterName);
    }

    @Override
    public int getItemCount() {
        return listFilters.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView filterName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pic = itemView.findViewById(R.id.imgFilter);
            filterName = itemView.findViewById(R.id.txtFilterName);
        }
    }
}
