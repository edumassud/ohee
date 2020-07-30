package com.example.ohee.adapter;

import android.content.Context;
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

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.MyViewHolder> {
    private List<Comment> comments;
    private Context context;

    public AnswersAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View comment = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comment, parent, false);
        return new MyViewHolder(comment);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comment comment = comments.get(position);

        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference userRef = databaseReference.child("user").child(comment.getIdUser());
        DatabaseReference highschoolerRef = databaseReference.child("highschoolers").child(comment.getIdUser());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

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
                } else {
                     highschoolerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             HighSchooler user = dataSnapshot.getValue(HighSchooler.class);

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
