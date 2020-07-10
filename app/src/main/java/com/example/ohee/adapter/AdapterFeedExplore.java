package com.example.ohee.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
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
import com.example.ohee.activity.CommentsActivity;
import com.example.ohee.activity.LikesListActivity;
import com.example.ohee.activity.UniversityProfileActivity;
import com.example.ohee.activity.VisitProfileActivity;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Comment;
import com.example.ohee.model.FeedExplore;
import com.example.ohee.model.FeedFollowing;
import com.example.ohee.model.Notification;
import com.example.ohee.model.Post;
import com.example.ohee.model.University;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeedExplore extends RecyclerView.Adapter<AdapterFeedExplore.MyViewHolder> {
    private List<Post> posts;
    private Context context;

    public AdapterFeedExplore(List<Post> posts, Context context) {
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
//            holder.txtCaption.setText(post.getCaption());
            holder.txtLikesCount.setText(post.getLikedBy().size() + " Likes");
            if (post.getLikedBy().contains(SetFirebaseUser.getUsersId())) {
                holder.btLike.setLiked(true);
            }

            DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
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
//                    holder.txtNameCap.setText(user.getName());
                    String fullComment = "<b>" + user.getName() + "</b>" + "  "  + post.getCaption();
                    holder.txtNameCap.setText(Html.fromHtml(fullComment));
                    holder.txtUniversity.setVisibility(View.VISIBLE);
                    holder.txtUniversity.setText(user.getUniversityName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // Set like event
            holder.btLike.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    post.getLikedBy().add(SetFirebaseUser.getUsersId());
                    post.upDateLikes();
                    holder.txtLikesCount.setText(post.getLikedBy().size() + " Likes");

                    if (!SetFirebaseUser.getUsersId().equals(post.getIdUser())) {
                        Notification notification = new Notification();
                        notification.setIdReceiver(post.getIdUser());
                        notification.setIdSender(SetFirebaseUser.getUsersId());
                        notification.setAction("postLiked");
                        notification.setIdPost(post.getId());
                        notification.save();
                    }


                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    post.getLikedBy().remove(SetFirebaseUser.getUsersId());
                    post.upDateLikes();
                    holder.txtLikesCount.setText(post.getLikedBy().size() + " Likes");

                    if (!SetFirebaseUser.getUsersId().equals(post.getIdUser())) {
                        Notification notification = new Notification();
                        notification.setIdReceiver(post.getIdUser());
                        notification.setIdSender(SetFirebaseUser.getUsersId());
                        notification.setAction("postLiked");
                        notification.setIdPost(post.getId());
                        notification.deleteNotification();
                    }
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
                holder.txtComment.setText("");
            } else {
                Comment firstComment = post.getComments().get(0);

                holder.txtComment.setText(firstComment.getComment());

                DatabaseReference commenterRef = databaseReference.child("user").child(firstComment.getIdUser());
                commenterRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        holder.txtCommenter.setText(user.getName());
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

            // Set university click
            holder.txtUniversity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference universityRef = databaseReference.child("universities").child(post.getUniversityDomain());

                    universityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            University university = dataSnapshot.getValue(University.class);

                            Intent i = new Intent(context, UniversityProfileActivity.class);
                            i.putExtra("selectedUniversity", university);
                            context.startActivity(i);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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
        private ImageView imgPost, btComment;
        private TextView txtName, txtNameCap, txtCaption, txtUniversity, txtLikesCount, txtCommentsCount, txtCommenter, txtComment;
        private LikeButton btLike;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile       = itemView.findViewById(R.id.imgUser);
            imgPost          = itemView.findViewById(R.id.imgPost);
            txtName          = itemView.findViewById(R.id.txtUserName);
            txtNameCap       = itemView.findViewById(R.id.txtNamCap);
            txtCaption       = itemView.findViewById(R.id.txtCaption);
            txtUniversity    = itemView.findViewById(R.id.txtUniversity);
            txtLikesCount    = itemView.findViewById(R.id.txtLikesCount);
            btLike           = itemView.findViewById(R.id.btLike);
            btComment        = itemView.findViewById(R.id.btComment);
            txtCommentsCount = itemView.findViewById(R.id.txtCommentsCount);
            txtCommenter     = itemView.findViewById(R.id.txtCommenter);
            txtComment       = itemView.findViewById(R.id.txtComment);

        }
    }
}
