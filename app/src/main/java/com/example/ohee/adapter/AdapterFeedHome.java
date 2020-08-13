package com.example.ohee.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brouding.doubletaplikeview.DoubleTapLikeView;
import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.activity.CommentsActivity;
import com.example.ohee.activity.LikesListActivity;
import com.example.ohee.activity.VisitProfileActivity;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Comment;
import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.Notification;
import com.example.ohee.model.Post;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeedHome extends RecyclerView.Adapter<AdapterFeedHome.MyViewHolder> {
    private List<Post> posts;
    private Context context;

    DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();

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

            DatabaseReference userRef = databaseReference.child("user").child(post.getIdUser());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getPicturePath() != null) {
                        Uri urlPost = Uri.parse(user.getPicturePath());
                        Glide.with(context).load(urlPost).into(holder.imgProfile);
                     }
                    holder.txtName.setText(user.getName());
                    String fullComment = "<b>" + user.getName() + "</b>" + "  " + post.getCaption();
                    holder.imgProfile.setRotation(user.getRotation());
                    holder.txtNameCap.setText(Html.fromHtml(fullComment));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.txtLikesCount.setText(post.getLikedBy().size() + " Likes");
            if (post.getLikedBy().contains(SetFirebaseUser.getUsersId())) {
                holder.btLike.setLiked(true);
            } else {
                holder.btLike.setLiked(false);
            }

            // Set like event
            holder.btLike.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    if (!post.getLikedBy().contains(SetFirebaseUser.getUsersId())) {
                        post.getLikedBy().add(SetFirebaseUser.getUsersId());
                        post.upDateLikes();
                        holder.txtLikesCount.setText(post.getLikedBy().size() + " Likes");
                        holder.btLike.setLiked(true);

                        if (!SetFirebaseUser.getUsersId().equals(post.getIdUser())) {
                            Notification notification = new Notification();
                            notification.setIdReceiver(post.getIdUser());
                            notification.setIdSender(SetFirebaseUser.getUsersId());
                            notification.setAction("postLiked");
                            notification.setIdPost(post.getId());
                            notification.save();
                        }
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    post.getLikedBy().remove(SetFirebaseUser.getUsersId());
                    post.upDateLikes();
                    holder.txtLikesCount.setText(post.getLikedBy().size() + " Likes");

                    if (!SetFirebaseUser.getUsersId().equals(post.getIdUser())) {
                        DatabaseReference notificationsRef = databaseReference.child("notifications").child(post.getIdUser());
                        notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Notification notification = ds.getValue(Notification.class);
                                    if (notification.getAction().equals("postLiked") && notification.getIdPost().equals(post.getId())) {
                                        notificationsRef.child(notification.getIdNotification()).removeValue();
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });

            holder.doubleTapper.setOnTapListener(new DoubleTapLikeView.OnTapListener() {
                @Override
                public void onDoubleTap(View view) {
                    if (!post.getLikedBy().contains(SetFirebaseUser.getUsersId())) {
                        post.getLikedBy().add(SetFirebaseUser.getUsersId());
                        post.upDateLikes();
                        holder.txtLikesCount.setText(post.getLikedBy().size() + " Likes");
                        holder.btLike.setLiked(true);

                        if (!SetFirebaseUser.getUsersId().equals(post.getIdUser())) {
                            Notification notification = new Notification();
                            notification.setIdReceiver(post.getIdUser());
                            notification.setIdSender(SetFirebaseUser.getUsersId());
                            notification.setAction("postLiked");
                            notification.setIdPost(post.getId());
                            notification.save();
                        }
                    }
                }

                @Override
                public void onTap() {

                }
            });


            // Set comment count
            int commentCount = post.getComments().size();
            if (commentCount == 0) {
                holder.txtCommentsCount.setVisibility(View.GONE);
            } else if (commentCount == 1) {
                holder.txtCommentsCount.setText(commentCount + " comment");
            } else if (commentCount > 1){
                holder.txtCommentsCount.setText(commentCount + " comments");
            }

            // Set featured comment
            if (post.getComments().size() == 0) {
                holder.txtCommenter.setText("Be the first one to comment!");
            } else {
                DatabaseReference commentsRef = databaseReference.child("comments");
                commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Comment> comments = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Comment comment = ds.getValue(Comment.class);
                            if (comment.getIdPost().equals(post.getId())) {
                                comments.add(comment);
                            }
                        }
                        Comment display = comments.get(0);
                        for (int i = 0; i < comments.size(); i++) {
                            if (comments.get(i).getLikedBy().size() > display.getLikedBy().size()) {
                                display = comments.get(i);
                            }
                        }
                        display(display, holder);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


            // Set comment event
            holder.btComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, CommentsActivity.class);
                    i.putExtra("post", post);
                    context.startActivity(i);
                }
            });

            // Set imgProfile click
            holder.imgProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference usersRef = databaseReference.child("user").child(post.getIdUser());
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

            // Set name click
            holder.txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference usersRef = databaseReference.child("user").child(post.getIdUser());
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);

                            if (user.getIdUser().equals(SetFirebaseUser.getUsersId())) {
                                holder.txtName.setClickable(false);
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

            // Set name click
            holder.txtNameCap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference usersRef = databaseReference.child("user").child(post.getIdUser());
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);

                            if (user.getIdUser().equals(SetFirebaseUser.getUsersId())) {
                                holder.txtNameCap.setClickable(false);
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

            // Set comment click
            holder.txtCommenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (post.getComments().size() == 0) {
                        holder.txtCommenter.setClickable(false);
                    } else {
                        Comment comment = post.getComments().get(0);
                        DatabaseReference usersRef = databaseReference.child("user").child(comment.getIdUser());
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);

                                if (user.getIdUser().equals(SetFirebaseUser.getUsersId())) {
                                    holder.txtCommenter.setClickable(false);
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
                }
            });

            // Set like click
            holder.txtLikesCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, LikesListActivity.class);
                    i.putExtra("post", post);
                    context.startActivity(i);
                }
            });

        }
    }

    private void display(Comment comment, @NonNull AdapterFeedHome.MyViewHolder holder) {
        DatabaseReference commenterRef = SetFirebase.getFirebaseDatabase().child("user").child(comment.getIdUser());
        commenterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    String fullComment = "<b>" + user.getName() + "</b>" + "  " + comment.getComment();
                    holder.txtCommenter.setText(Html.fromHtml(fullComment));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference commenterHSRef = SetFirebase.getFirebaseDatabase().child("highschoolers").child(comment.getIdUser());
        commenterHSRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HighSchooler user = dataSnapshot.getValue(HighSchooler.class);
                    String fullComment = "<b>" + user.getName() + "</b>" + "  " + comment.getComment();
                    holder.txtCommenter.setText(Html.fromHtml(fullComment));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgProfile;
        private DoubleTapLikeView doubleTapper;
        private ImageView imgPost, btComment;
        private TextView txtName, txtNameCap, txtCaption, txtLikesCount, txtCommentsCount, txtCommenter, txtComment;
        private LikeButton btLike;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile       = itemView.findViewById(R.id.imgUser);
            imgPost          = itemView.findViewById(R.id.imgPost);
            txtName          = itemView.findViewById(R.id.txtUserName);
            txtNameCap       = itemView.findViewById(R.id.txtNamCap);
            txtCaption       = itemView.findViewById(R.id.txtCaption);
            txtLikesCount    = itemView.findViewById(R.id.txtLikesCount);
            btLike           = itemView.findViewById(R.id.btLike);
            btComment        = itemView.findViewById(R.id.btComment);
            txtCommentsCount = itemView.findViewById(R.id.txtCommentsCount);
            txtCommenter     = itemView.findViewById(R.id.txtCommenter);
            txtComment       = itemView.findViewById(R.id.txtComment);
            doubleTapper     = itemView.findViewById(R.id.doubleTapper);
        }
    }
}
