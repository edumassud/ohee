package com.example.ohee.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.activity.ChatActivity;
import com.example.ohee.activity.PostActivity;
import com.example.ohee.activity.VisitProfileActivity;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Notification;
import com.example.ohee.model.Post;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {
    private List<Notification> notifications;
    private Context context;

    private String idLoggedUser = SetFirebaseUser.getUsersId();
    private DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    private DatabaseReference usersRef          = databaseReference.child("user");
    private DatabaseReference postsRef          = databaseReference.child("posts");
    private DatabaseReference followingRef      = databaseReference.child("following");
    private DatabaseReference followerRef       = databaseReference.child("followers");

    public NotificationsAdapter(List<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View notification = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_notifications, parent, false);
        return new NotificationsAdapter.MyViewHolder(notification);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        // Get logged user
        DatabaseReference loggedUserRef = usersRef.child(idLoggedUser);
        loggedUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User loggedUser = dataSnapshot.getValue(User.class);


                if (notification.getAction().equals("postLiked") || notification.getAction().equals("comment")) {
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

                            holder.imgPost.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(context, PostActivity.class);
                                    i.putExtra("selectedPost", post);
                                    usersRef.child(post.getIdUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User selectedUser = dataSnapshot.getValue(User.class);
                                            i.putExtra("selectedUser", selectedUser);
                                            context.startActivity(i);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else if (notification.getAction().equals("followReq")) {
                    if (notification.getStatus().equals("answered")) {
                        holder.imgPost.setVisibility(View.GONE);
                        holder.buttons.setVisibility(View.GONE);
                    } else {
                        holder.imgPost.setVisibility(View.GONE);
                        holder.buttons.setVisibility(View.VISIBLE);
                        DatabaseReference senderRef = usersRef.child(notification.getIdSender());

                        holder.btAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User friendsUser = dataSnapshot.getValue(User.class);

                                        HashMap<String, Object> loggedUsersData = new HashMap<>();
                                        loggedUsersData.put("name", loggedUser.getName() );
                                        loggedUsersData.put("picturePath", loggedUser.getPicturePath() );

                                        HashMap<String, Object> hostData = new HashMap<>();
                                        hostData.put("name", friendsUser.getName() );
                                        hostData.put("picturePath", friendsUser.getPicturePath() );

                                        DatabaseReference followerNode = followerRef
                                                .child(loggedUser.getIdUser())
                                                .child(friendsUser.getIdUser());
                                        followerNode.setValue(friendsUser.getIdUser());

                                        DatabaseReference followingNode = followingRef
                                                .child(friendsUser.getIdUser())
                                                .child(loggedUser.getIdUser());
                                        followingNode.setValue(loggedUser.getIdUser());

//                                        friendsUser.setFollowingCount(friendsUser.getFollowingCount() + 1);
//                                        loggedUser.setFollowerCount(loggedUser.getFollowerCount() + 1);
//
//                                        friendsUser.changeFollowing(loggedUser.getIdUser(), "add");
//                                        loggedUser.changeFollower(friendsUser.getIdUser(), "add");

                                        notification.setStatus("answered");
                                        notification.updateStatus();

                                        String fullNotification = "<b>" + friendsUser.getName() + "</b>" + "  started Following you.";
                                        holder.txtName.setText(Html.fromHtml(fullNotification));
                                        holder.buttons.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }

                    holder.btDeny.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notification.deleteNotification();
                            holder.layout.setVisibility(View.GONE);

                        }
                    });
                } else if (notification.getAction().equals("message")) {
                    holder.imgPost.setVisibility(View.GONE);
                    holder.txtName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(context, ChatActivity.class);
                            usersRef.child(notification.getIdSender()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User selectedUser = dataSnapshot.getValue(User.class);
                                    i.putExtra("chatContato", selectedUser);
                                    context.startActivity(i);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    });
                }



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
                        } else if (notification.getAction().equals("followReq") && notification.getStatus().equals("sent")) {
                            fullNotification = "<b>" + sender.getName() + "</b>" + "  wants to follow you." ;
                        } else if (notification.getAction().equals("followReq") && notification.getStatus().equals("answered")) {
                            fullNotification = "<b>" + sender.getName() + "</b>" + "  started following you." ;
                        } else if (notification.getAction().equals("message")) {
                            fullNotification = "<b>" + sender.getName() + "</b>" + "  sent you a message" ;
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

        // Set imgProfile click
        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference usersRef = databaseReference.child("user").child(notification.getIdSender());
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user.getIdUser().equals(SetFirebaseUser.getUsersId())) {
                            holder.imgProfile.setClickable(false);
                        } else {
                            Intent i = new Intent(context, VisitProfileActivity.class);
                            i.putExtra("selectedUser", user);
                            context.startActivity(i);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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
        private Button btAccept, btDeny;
        private LinearLayout buttons, layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtName    = itemView.findViewById(R.id.txtName);
            imgPost    = itemView.findViewById(R.id.imgPost);
            btAccept   = itemView.findViewById(R.id.btAccept);
            btDeny     = itemView.findViewById(R.id.btDeny);
            buttons    = itemView.findViewById(R.id.buttons);
            layout     = itemView.findViewById(R.id.layout);
        }
    }
}
