package com.example.chatapp.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Activities.SplashActivity;
import com.example.chatapp.Cons;
import com.example.chatapp.Models.FriendRequest;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private Context context;
    private List<User> users;
    private String email;
    public static boolean newRequestSent = false;


    public UserRecyclerAdapter(Context context, List<User> users, String email) {
        this.context = context;
        this.users = users;
        this.email = email;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UserViewHolder(LayoutInflater.from(context).inflate(R.layout.users_costum_design, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder userViewHolder, int position) {
        final User user = users.get(position);
        userViewHolder.textViewName.setText(user.getName());
        if (user.getPersonalImage() != null)
            Picasso.get().load(user.getPersonalImage()).placeholder(R.drawable.student_default_photo).into(userViewHolder.circleImageViewPersonalImage);
        else
            userViewHolder.circleImageViewPersonalImage.setImageResource(R.drawable.student_default_photo);
        userViewHolder.btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userViewHolder.btnSendRequest.setVisibility(View.GONE);
                userViewHolder.progressBar.setVisibility(View.VISIBLE);
                user.setFriendRequestsId(user.getFriendRequestsId() + 1);
                SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(user.getEmail()).update("friendRequestsId", user.getFriendRequestsId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Date date = new Date();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
                        String fullDate = dateFormat.format(date);
                        FriendRequest friendRequest = new FriendRequest(user.getFriendRequestsId(),fullDate,email);
                        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(user.getEmail()).collection(Cons.FRIENDS_REQUESTS_COLLECTION_REF).document(email).set(friendRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                userViewHolder.btnSendRequest.setVisibility(View.VISIBLE);
                                userViewHolder.progressBar.setVisibility(View.GONE);
                                Toast.makeText(context, "Request sent successfully", Toast.LENGTH_SHORT).show();
                                newRequestSent = true;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                // Send request >>
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}

class UserViewHolder extends RecyclerView.ViewHolder {
    CircleImageView circleImageViewPersonalImage;
    TextView textViewName;
    Button btnSendRequest;
    ProgressBar progressBar;

    public UserViewHolder(View view) {
        super(view);
        this.circleImageViewPersonalImage = view.findViewById(R.id.cmv_personal_image);
        this.textViewName = view.findViewById(R.id.text_view_name);
        this.btnSendRequest = view.findViewById(R.id.btn_send);
        this.progressBar = view.findViewById(R.id.progress_bar_send_request);
    }
}

