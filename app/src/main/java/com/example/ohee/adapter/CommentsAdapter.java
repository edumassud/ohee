package com.example.ohee.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brouding.doubletaplikeview.DoubleTapLikeView;
import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.activity.HSVisitProfileActivity;
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

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {
    private List<Comment> comments;
    private Context context;
    private boolean isCollege;

    public CommentsAdapter(List<Comment> comments, Context context, boolean isCollege) {
        this.comments = comments;
        this.context = context;
        this.isCollege = isCollege;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View comment = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comment, parent, false);
        return new CommentsAdapter.MyViewHolder(comment);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comment comment = comments.get(position);

        // Get user's info
        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        if (isCollege) {
            DatabaseReference usersRef = databaseReference.child("user").child(comment.getIdUser());
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    // Set user's info
                    String fullComment = "<b>" + user.getName() + "</b>" + "  " + comment.getComment();
                    holder.txtName.setText(Html.fromHtml(fullComment));
                    String picPath = user.getPicturePath();
                    if (picPath != null) {
                        Uri url = Uri.parse(picPath);
                        Glide.with(context)
                                .load(url)
                                .into(holder.imgProfile);
                        holder.imgProfile.setRotation(user.getRotation());
                    } else {
                        holder.imgProfile.setImageResource(R.drawable.avatar);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            DatabaseReference usersRef = databaseReference.child("highschoolers").child(comment.getIdUser());
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HighSchooler user = dataSnapshot.getValue(HighSchooler.class);

                    // Set user's info
                    String fullComment = "<b>" + user.getName() + "</b>" + "  " + comment.getComment();
                    holder.txtName.setText(Html.fromHtml(fullComment));
                    String picPath = user.getPicturePath();
                    if (picPath != null) {
                        Uri url = Uri.parse(picPath);
                        Glide.with(context)
                                .load(url)
                                .into(holder.imgProfile);
                    } else {
                        holder.imgProfile.setImageResource(R.drawable.avatar);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        // Set imgProfile click
        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference usersRef = databaseReference.child("user").child(comment.getIdUser());
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (comment.getIdUser().equals(SetFirebaseUser.getUsersId())) {
                            holder.imgProfile.setClickable(false);
                        } else {
                            if (isCollege) {
                                Intent i = new Intent(context, VisitProfileActivity.class);
                                i.putExtra("selectedUser", user);
                                context.startActivity(i);
                            } else {
                                Intent i = new Intent(context, HSVisitProfileActivity.class);
                                i.putExtra("selectedUser", user);
                                context.startActivity(i);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        // Set like info
        DatabaseReference commentsRef = databaseReference.child("comments").child(comment.getIdComment());
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Comment commentRef = dataSnapshot.getValue(Comment.class);
                holder.txtLikesCount.setText(commentRef.getLikedBy().size() + " Likes");
                if (commentRef.getLikedBy().contains(SetFirebaseUser.getUsersId())) {
                    holder.btLike.setLiked(true);
                } else {
                    holder.btLike.setLiked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Set like event
        holder.btLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (!comment.getLikedBy().contains(SetFirebaseUser.getUsersId())) {
                    comment.getLikedBy().add(SetFirebaseUser.getUsersId());
                    comment.upDateLikes();
                    holder.txtLikesCount.setText(comment.getLikedBy().size() + " Likes");
                    holder.btLike.setLiked(true);

                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                comment.getLikedBy().remove(SetFirebaseUser.getUsersId());
                comment.upDateLikes();
                holder.txtLikesCount.setText(comment.getLikedBy().size() + " Likes");

            }
        });

//        holder.doubleTapper.setOnTapListener(new DoubleTapLikeView.OnTapListener() {
//            @Override
//            public void onDoubleTap(View view) {
//                if (!comment.getLikedBy().contains(SetFirebaseUser.getUsersId())) {
//                    holder.txtLikesCount.setText(comment.getLikedBy().size() + " Likes");
//                    holder.btLike.setLiked(true);
//                    Toast.makeText(context, comment.getPostDomain(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(context, comment.getIdPost(), Toast.LENGTH_SHORT).show();
//
//                    DatabaseReference postRef = databaseReference.child("posts").child(comment.getPostDomain()).child(comment.getIdPost());
//                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            Post post = dataSnapshot.getValue(Post.class);
//                            comment.getLikedBy().add(SetFirebaseUser.getUsersId());
//                            post.upDateComments();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onTap() {
//
//            }
//        });

        // Set name click
        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference usersRef = databaseReference.child("user").child(comment.getIdUser());
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (comment.getIdUser().equals(SetFirebaseUser.getUsersId()) ) {
                            holder.txtName.setClickable(false);
                        }
                        else {
                            if (isCollege) {
                                Intent i = new Intent(context, VisitProfileActivity.class);
                                i.putExtra("selectedUser", user);
                                context.startActivity(i);
                            } else {
                                Intent i = new Intent(context, HSVisitProfileActivity.class);
                                i.putExtra("selectedUser", user);
                                context.startActivity(i);
                            }
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
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgProfile;
        private TextView txtName, txtLikesCount;
        //private DoubleTapLikeView doubleTapper;
        private LikeButton btLike;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile      = itemView.findViewById(R.id.imgProfile);
            txtName         = itemView.findViewById(R.id.txtName);
            txtLikesCount   = itemView.findViewById(R.id.txtLikesCount);
            //doubleTapper    = itemView.findViewById(R.id.doubleTapper);
            btLike          = itemView.findViewById(R.id.btLike);
        }
    }
}
