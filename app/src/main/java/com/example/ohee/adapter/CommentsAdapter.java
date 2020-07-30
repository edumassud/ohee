package com.example.ohee.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.activity.HSVisitProfileActivity;
import com.example.ohee.activity.VisitProfileActivity;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Comment;
import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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

        // Set comment
//        holder.txtComment.setText(comment.getComment());

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
        private TextView txtName, txtComment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtName    = itemView.findViewById(R.id.txtName);
            txtComment = itemView.findViewById(R.id.txtComment);
        }
    }
}
