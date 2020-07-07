package com.example.ohee.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.model.Post;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeedHome extends RecyclerView.Adapter<AdapterFeedHome.MyViewHolder> {
    private List<Post> posts;
    private Context context;

    public AdapterFeedHome(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View post = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_posts, parent, false);
        return new MyViewHolder(post);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Post post = posts.get(position);

        if (post != null) {

            // Set post info
            if (post.getPath() != null) {
                Uri urlProfile = Uri.parse(post.getPath());
                Glide.with(context).load(urlProfile).into(holder.imgPost);
            }

            holder.txtCaption.setText(post.getCaption());

            DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
            DatabaseReference userRef = databaseReference.child("user").child(post.getIdUser());

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getPicturePath() != null) {
                        Uri urlPost = Uri.parse(user.getPicturePath());
                        Glide.with(context).load(urlPost).into(holder.imgProfile);
                     }
                    holder.txtName.setText(user.getName());
                    holder.txtNameCap.setText(user.getName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgProfile;
        private ImageView imgPost;
        private TextView txtName, txtNameCap, txtCaption;
        private LikeButton btLike;
        private LinearLayout linear;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile      = itemView.findViewById(R.id.imgUser);
            imgPost         = itemView.findViewById(R.id.imgPost);
            txtName         = itemView.findViewById(R.id.txtUserName);
            txtNameCap      = itemView.findViewById(R.id.txtNamCap);
            txtCaption      = itemView.findViewById(R.id.txtCaption);
            btLike          = itemView.findViewById(R.id.btLike);
            linear          = itemView.findViewById(R.id.linear);
        }
    }
}
