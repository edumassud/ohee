package com.example.ohee.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.activity.VisitProfileActivity;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Notification;
import com.example.ohee.model.Post;
import com.example.ohee.model.University;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificatioinsAdapter extends RecyclerView.Adapter<NotificatioinsAdapter.MyViewHolder> {
    private List<Notification> notifications;
    private Context context;

    private String idLoggedUser = SetFirebaseUser.getUsersId();

    public NotificatioinsAdapter(List<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View notification = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_notifications, parent, false);
        return new NotificatioinsAdapter.MyViewHolder(notification);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference usersRef = databaseReference.child("user");
        DatabaseReference postsRef = databaseReference.child("posts");

        // Get logged user
        DatabaseReference loggedUserRef = usersRef.child(idLoggedUser);
        loggedUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User loggedUser = dataSnapshot.getValue(User.class);

                // Get post
                DatabaseReference postRef = postsRef.child(loggedUser.getUniversityDomain()).child(notification.getIdPost());
                postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);
                        Uri url = Uri.parse(post.getPath());
                        Glide.with(context)
                                .load(url)
                                .into(holder.imgPost);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // Set sender info
                DatabaseReference senderRef = usersRef.child(notification.getIdSender());
                senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User sender = dataSnapshot.getValue(User.class);

                        String fullNotification = "";
                        if (notification.getAction().equals("comment")) {
                            fullNotification = "<b>" + sender.getName() + "</b>" + " commented: " + notification.getComment();
                        } else if (notification.getAction().equals("postLiked")) {
                            fullNotification = "<b>" + sender.getName() + "</b>" + "  liked your post." ;
                        }
                        holder.txtName.setText(Html.fromHtml(fullNotification));

                        Post post = dataSnapshot.getValue(Post.class);
                        Uri url = Uri.parse(sender.getPicturePath());
                        Glide.with(context)
                                .load(url)
                                .into(holder.imgProfile);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgProfile;
        private TextView txtName;
        private ImageView imgPost;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtName    = itemView.findViewById(R.id.txtName);
            imgPost    = itemView.findViewById(R.id.imgPost);
        }
    }
}
