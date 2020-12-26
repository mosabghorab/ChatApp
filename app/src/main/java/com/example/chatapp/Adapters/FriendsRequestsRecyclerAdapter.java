package com.example.chatapp.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Activities.SplashActivity;
import com.example.chatapp.Cons;
import com.example.chatapp.Models.Friend;
import com.example.chatapp.Models.FriendRequest;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsRequestsRecyclerAdapter extends RecyclerView.Adapter<FriendsRequestsViewHolder> {
    private Context context;
    private List<FriendRequest> friendRequests;
    private User currentUser;


    public FriendsRequestsRecyclerAdapter(Context context, List<FriendRequest> friendRequests, User currentUser) {
        this.context = context;
        this.friendRequests = friendRequests;
        this.currentUser = currentUser;
    }

    public void setFriendRequests(List<FriendRequest> friendRequests) {
        this.friendRequests = friendRequests;
    }

    @NonNull
    @Override
    public FriendsRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FriendsRequestsViewHolder(LayoutInflater.from(context).inflate(R.layout.freinds_requests_costum_design, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendsRequestsViewHolder friendsRequestsViewHolder, int position) {
        final FriendRequest friendRequest = friendRequests.get(position);
        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(friendRequest.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final User user = documentSnapshot.toObject(User.class);
                user.setEmail(documentSnapshot.getId());
                if (user.getPersonalImage() != null)
                    Picasso.get().load(user.getPersonalImage()).into(friendsRequestsViewHolder.circleImageViewPersonalImage);
                else
                    friendsRequestsViewHolder.circleImageViewPersonalImage.setImageResource(R.drawable.student_default_photo);
                friendsRequestsViewHolder.textViewName.setText(user.getName());
                friendsRequestsViewHolder.textViewDate.setText(friendRequest.getDate());
                friendsRequestsViewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(currentUser.getEmail()).collection(Cons.FRIENDS_REQUESTS_COLLECTION_REF).document(user.getEmail()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Date date = new Date();
                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
                                String fullDate = dateFormat.format(date);
                                currentUser.setFriendsId(currentUser.getFriendsId() + 1);
                                user.setFriendsId(user.getFriendsId() + 1);
                                final Friend friend = new Friend(currentUser.getFriendsId(), fullDate, currentUser.getEmail());
                                final Friend friend2 = new Friend(user.getFriendsId(), fullDate, user.getEmail());
                                SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(currentUser.getEmail()).update("friendsId", currentUser.getFriendsId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(currentUser.getEmail()).collection(Cons.FRIENDS_COLLECTION_REF).document(user.getEmail()).set(friend).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // here
                                                SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(user.getEmail()).collection(Cons.FRIENDS_COLLECTION_REF).document(currentUser.getEmail()).set(friend2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(user.getEmail()).update("friendsId", user.getFriendsId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(context, "Request accepted successfully", Toast.LENGTH_SHORT).show();
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
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                friendsRequestsViewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(currentUser.getEmail()).collection(Cons.FRIENDS_REQUESTS_COLLECTION_REF).document(user.getEmail()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Request deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

}

class FriendsRequestsViewHolder extends RecyclerView.ViewHolder {
    CircleImageView circleImageViewPersonalImage;
    TextView textViewName;
    TextView textViewDate;
    Button btnAccept;
    Button btnCancel;

    public FriendsRequestsViewHolder(View view) {
        super(view);
        this.circleImageViewPersonalImage = view.findViewById(R.id.cmv_personal_image);
        this.textViewName = view.findViewById(R.id.text_view_name);
        this.textViewDate = view.findViewById(R.id.text_view_date);
        this.btnAccept = view.findViewById(R.id.btn_accept);
        this.btnCancel = view.findViewById(R.id.btn_cancel);
    }
}

