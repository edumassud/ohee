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
import com.example.ohee.activity.AnswersActivity;
import com.example.ohee.activity.CommentsActivity;
import com.example.ohee.activity.VisitHSActivity;
import com.example.ohee.activity.VisitProfileActivity;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Comment;
import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.Notification;
import com.example.ohee.model.Post;
import com.example.ohee.model.Question;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterQandA extends RecyclerView.Adapter<AdapterQandA.MyViewHolder> {
    private List<Question> questions;
    private Context context;

    public AdapterQandA(List<Question> questions, Context context) {
        this.questions = questions;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View post = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_qanda, parent, false);
        return new MyViewHolder(post);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Question question = questions.get(position);

        // Set likes count
        holder.txtLikesCount.setText(question.getLikedBy().size() + " Likes");
        if (question.getLikedBy().contains(SetFirebaseUser.getUsersId())) {
            holder.btLike.setLiked(true);
        }

        // Set question body
        holder.txtQuestion.setText(question.getQuestion());

        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference userRef = databaseReference.child("highschoolers").child(question.getIdUser());

        // Set user's info
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HighSchooler user = dataSnapshot.getValue(HighSchooler.class);
                if (user.getPicturePath() != null) {
                    Uri urlPost = Uri.parse(user.getPicturePath());
                    Glide.with(context).load(urlPost).into(holder.imgProfile);
                }
                holder.txtName.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Set university name
        if (question.getType().equals("specific")) {
            holder.txtUniversity.setVisibility(View.VISIBLE);
            holder.txtUniversity.setText(question.getSpecificUniversity().getName());
        }

        // Set like event
        holder.btLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (!question.getLikedBy().contains(SetFirebaseUser.getUsersId())) {
                    question.getLikedBy().add(SetFirebaseUser.getUsersId());
                    question.upDateLikes();
                    holder.txtLikesCount.setText(question.getLikedBy().size() + " Likes");
                    holder.btLike.setLiked(true);

//                    if (!SetFirebaseUser.getUsersId().equals(question.getIdUser())) {
//                        Notification notification = new Notification();
//                        notification.setIdReceiver(question.getIdUser());
//                        notification.setIdSender(SetFirebaseUser.getUsersId());
//                        notification.setAction("questionLiked");
//                        notification.setIdPost(question.getId());
//                        notification.save();
//                    }
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                question.getLikedBy().remove(SetFirebaseUser.getUsersId());
                question.upDateLikes();
                holder.txtLikesCount.setText(question.getLikedBy().size() + " Likes");

                if (!SetFirebaseUser.getUsersId().equals(question.getIdUser())) {
                    DatabaseReference notificationsRef = databaseReference.child("notifications").child(question.getIdUser());
                    notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Notification notification = ds.getValue(Notification.class);
                                if (notification.getIdPost().equals(question.getId())) {
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
                if (!question.getLikedBy().contains(SetFirebaseUser.getUsersId())) {
                    question.getLikedBy().add(SetFirebaseUser.getUsersId());
                    question.upDateLikes();
                    holder.txtLikesCount.setText(question.getLikedBy().size() + " Likes");
                    holder.btLike.setLiked(true);

//                    if (!SetFirebaseUser.getUsersId().equals(question.getIdUser())) {
//                        Notification notification = new Notification();
//                        notification.setIdReceiver(question.getIdUser());
//                        notification.setIdSender(SetFirebaseUser.getUsersId());
//                        notification.setAction("questionLiked");
//                        notification.setIdPost(question.getId());
//                        notification.save();
//                    }
                }
            }

            @Override
            public void onTap() {

            }
        });

        // Set comment event
        holder.btComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AnswersActivity.class);
                i.putExtra("question", question);
                context.startActivity(i);
            }
        });

        // Set comment count
        int commentCount = question.getAnswers().size();
        if (commentCount == 0) {
            holder.txtCommentsCount.setVisibility(View.GONE);
        } else if (commentCount == 1) {
            holder.txtCommentsCount.setText(commentCount + " comment");
        } else if (commentCount > 1){
            holder.txtCommentsCount.setText(commentCount + " comments");
        }

        // Set featured comment
        if (question.getAnswers().size() == 0) {
            holder.txtCommenter.setText("Be the first one to answer!");
            holder.txtComment.setText("");
        } else {
            Comment firstComment = question.getAnswers().get(0);

            DatabaseReference commenterRef = databaseReference.child("user").child(firstComment.getIdUser());
            commenterRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        String fullComment = "<b>" + user.getName() + "</b>" + "  " + firstComment.getComment();
                        holder.txtCommenter.setText(Html.fromHtml(fullComment));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference commenterHSRef = databaseReference.child("highschoolers").child(firstComment.getIdUser());
            commenterHSRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        HighSchooler user = dataSnapshot.getValue(HighSchooler.class);
                        String fullComment = "<b>" + user.getName() + "</b>" + "  " + firstComment.getComment();
                        holder.txtCommenter.setText(Html.fromHtml(fullComment));
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
                DatabaseReference usersRef = databaseReference.child("highschoolers").child(question.getIdUser());
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HighSchooler user = dataSnapshot.getValue(HighSchooler.class);

                        if (user.getIdUser().equals(SetFirebaseUser.getUsersId())) {
                            holder.imgProfile.setClickable(false);
                        } else {
                            Intent i = new Intent(context, VisitHSActivity.class);
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
        return questions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgProfile;
        private DoubleTapLikeView doubleTapper;
        private ImageView btComment;
        private TextView txtName, txtUniversity, txtQuestion, txtLikesCount, txtCommentsCount, txtCommenter, txtComment;
        private LikeButton btLike;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile       = itemView.findViewById(R.id.imgUser);
            txtName          = itemView.findViewById(R.id.txtUserName);
            txtQuestion      = itemView.findViewById(R.id.txtQuestion);
            txtLikesCount    = itemView.findViewById(R.id.txtLikesCount);
            txtUniversity    = itemView.findViewById(R.id.txtUniversity);
            btLike           = itemView.findViewById(R.id.btLike);
            btComment        = itemView.findViewById(R.id.btComment);
            txtCommentsCount = itemView.findViewById(R.id.txtCommentsCount);
            txtCommenter     = itemView.findViewById(R.id.txtCommenter);
            txtComment       = itemView.findViewById(R.id.txtComment);
            doubleTapper     = itemView.findViewById(R.id.doubleTapper);

        }
    }
}
