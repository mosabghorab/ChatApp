package com.example.chatapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Activities.ChatActivity;
import com.example.chatapp.Activities.SplashActivity;
import com.example.chatapp.Cons;
import com.example.chatapp.Models.Friend;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<ContactsViewHolder> {
    private Context context;
    private List<Friend> friends;
    private String email;


    public ContactsRecyclerAdapter(Context context, List<Friend> friends, String email) {
        this.context = context;
        this.friends = friends;
        this.email = email;
    }

    public void setFriends(final List<Friend> friends) {
        this.friends = friends;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull final  ViewGroup viewGroup, int i) {
        return new ContactsViewHolder(LayoutInflater.from(context).inflate(R.layout.contacts_costum_design, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsViewHolder contactsViewHolder, final int position) {
        final Friend friend = friends.get(position);
        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(friend.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                final User user = documentSnapshot.toObject(User.class);
                user.setEmail(documentSnapshot.getId());
                contactsViewHolder.textViewName.setText(user.getName());
                contactsViewHolder.textViewDate.setText(friend.getDate());
                contactsViewHolder.imageViewActive.setVisibility(View.GONE);
                if (user.isAccountActive()) {
                    contactsViewHolder.imageViewActive.setVisibility(View.VISIBLE);
                }
                if (user.getPersonalImage() != null) {
                    Picasso.get().load(user.getPersonalImage()).placeholder(R.drawable.student_default_photo).into(contactsViewHolder.circleImageViewPersonalImage);
                } else {
                    contactsViewHolder.circleImageViewPersonalImage.setImageResource(R.drawable.student_default_photo);
                }
                    contactsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, ChatActivity.class).putExtra("currentEmail", email).putExtra("receiverEmail", friend.getEmail()));
                    }
                });
                contactsViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete friend");
                        builder.setMessage("Are you sure you want to delete " + user.getName() + "?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                contactsViewHolder.btnDelete.setVisibility(View.GONE);
                                contactsViewHolder.progressBarDelete.setVisibility(View.VISIBLE);
                                SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).collection(Cons.FRIENDS_COLLECTION_REF).document(user.getEmail()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(user.getEmail()).collection(Cons.FRIENDS_COLLECTION_REF).document(email).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                contactsViewHolder.btnDelete.setVisibility(View.VISIBLE);
                                                contactsViewHolder.progressBarDelete.setVisibility(View.GONE);
                                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
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
                        builder.setNeutralButton("Cancel", null);
                        builder.show();
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
        return friends.size();
    }
}

class ContactsViewHolder extends RecyclerView.ViewHolder {
    CircleImageView circleImageViewPersonalImage;
    TextView textViewName;
    TextView textViewDate;
    Button btnDelete;
    ImageView imageViewActive;
    ProgressBar progressBarDelete;

    public ContactsViewHolder(View view) {
        super(view);
        this.circleImageViewPersonalImage = view.findViewById(R.id.cmv_personal_image);
        this.imageViewActive = view.findViewById(R.id.image_view_active);
        this.textViewName = view.findViewById(R.id.text_view_name);
        this.textViewDate = view.findViewById(R.id.text_view_date);
        this.btnDelete = view.findViewById(R.id.btn_delete);
        this.progressBarDelete = view.findViewById(R.id.progress_bar_delete);
    }
}

