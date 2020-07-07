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
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.FeedExplore;
import com.example.ohee.model.FeedFollowing;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeedExplore extends RecyclerView.Adapter<AdapterFeedExplore.MyViewHolder> {
    private List<FeedExplore> posts;
    private Context context;

    public AdapterFeedExplore(List<FeedExplore> posts, Context context) {
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
        FeedExplore feed = posts.get(position);

        if (feed != null) {

            // Set imgs info
            if (feed.getUserPic() != null) {
                Uri urlProfile = Uri.parse(feed.getUserPic());
                Glide.with(context).load(urlProfile).into(holder.imgProfile);
            }
            if (feed.getPath() != null) {
                Uri urlPost = Uri.parse(feed.getPath());
                Glide.with(context).load(urlPost).into(holder.imgPost);
            }

            // Set txts
            holder.txtCaption.setText(feed.getCaption());
            holder.txtNameCap.setText(feed.getUserName());
            holder.txtName.setText(feed.getUserName());

            // Set university
            DatabaseReference userRef = SetFirebase.getFirebaseDatabase()
                    .child("user")
                    .child(feed.getIdUser());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    holder.txtUniversity.setVisibility(View.VISIBLE);
                    holder.txtUniversity.setText(user.getUniversityName());
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
        private TextView txtName, txtNameCap, txtCaption, txtUniversity;
        private LikeButton btLike;
        private LinearLayout linear;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile      = itemView.findViewById(R.id.imgUser);
            imgPost         = itemView.findViewById(R.id.imgPost);
            txtName         = itemView.findViewById(R.id.txtUserName);
            txtNameCap      = itemView.findViewById(R.id.txtNamCap);
            txtCaption      = itemView.findViewById(R.id.txtCaption);
            txtUniversity   = itemView.findViewById(R.id.txtUniversity);
            btLike          = itemView.findViewById(R.id.btLike);
            linear          = itemView.findViewById(R.id.linear);
        }
    }
}
