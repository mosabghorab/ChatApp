package com.example.chatapp.Activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapters.MessagesRecyclerAdapter;
import com.example.chatapp.Cons;
import com.example.chatapp.Models.Friend;
import com.example.chatapp.Models.Message;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvMessages;
    private EditText editTextMessage;
    private ImageView imageViewSendMessage, imageViewActive;
    private TextView textViewName, textViewActive;
    private CircleImageView circleImageViewImage;
    private String currentEmail, receiverEmail;
    private Friend receiverFriend;
    private User receiverUser;
    private List<Message> messages;
    private MessagesRecyclerAdapter messagesRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("currentEmail") && intent.hasExtra("receiverEmail")) {
            receiverEmail = intent.getStringExtra("receiverEmail");
            currentEmail = intent.getStringExtra("currentEmail");
        }
        inflateViews();

        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(receiverEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                receiverUser = documentSnapshot.toObject(User.class);
                receiverUser.setEmail(documentSnapshot.getId());
                textViewName.setText(receiverUser.getName());
                if (receiverUser.getPersonalImage() != null)
                    Picasso.get().load(receiverUser.getPersonalImage()).into(circleImageViewImage);
                if (receiverUser.isAccountActive()) {
                    imageViewActive.setVisibility(View.VISIBLE);
                    textViewActive.setVisibility(View.VISIBLE);
                }
                messagesRecyclerAdapter = new MessagesRecyclerAdapter(getBaseContext(), messages, receiverUser);
                rvMessages.setAdapter(messagesRecyclerAdapter);
                SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(currentEmail).collection(Cons.FRIENDS_COLLECTION_REF).document(receiverEmail).collection(Cons.MESSAGES_COLLECTION_REF).orderBy("id").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        messages.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            Message message = queryDocumentSnapshot.toObject(Message.class);
                            message.setEmail(queryDocumentSnapshot.getId());
                            messages.add(message);
                        }
                        messagesRecyclerAdapter.setMessages(messages);
                        messagesRecyclerAdapter.notifyDataSetChanged();
                        rvMessages.scrollToPosition(messages.size() - 1);
                    }
                });
                SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(receiverEmail).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        receiverUser = documentSnapshot.toObject(User.class);
                        if (receiverUser.isAccountActive()) {
                            textViewActive.setText("Active Now");
                            imageViewActive.setVisibility(View.VISIBLE);
                        }else {
                            long lastActiveTime = receiverUser.getLastActiveTime();
                            imageViewActive.setVisibility(View.GONE);
                            long currentTime= System.currentTimeMillis();
                            String timeAgo = DateUtils.getRelativeTimeSpanString(currentTime,lastActiveTime,DateUtils.SECOND_IN_MILLIS,DateUtils.FORMAT_ABBREV_ALL).toString();
                            textViewActive.setText(timeAgo);
                        }
                        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(currentEmail).collection(Cons.FRIENDS_COLLECTION_REF).document(receiverEmail).collection(Cons.MESSAGES_COLLECTION_REF).orderBy("id").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                messages.clear();
                                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                    Message message = queryDocumentSnapshot.toObject(Message.class);
                                    message.setEmail(queryDocumentSnapshot.getId());
                                    messages.add(message);
                                }
                                messagesRecyclerAdapter.setMessages(messages);
                                messagesRecyclerAdapter.setReceiverUser(receiverUser);
                                messagesRecyclerAdapter.notifyDataSetChanged();
                                rvMessages.scrollToPosition(messages.size() - 1);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        imageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(currentEmail).collection(Cons.FRIENDS_COLLECTION_REF).document(receiverEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        receiverFriend = documentSnapshot.toObject(Friend.class);
                        receiverFriend.setEmail(documentSnapshot.getId());
                        final int autoId = receiverFriend.getMessagesId() + 1;
                        String content = editTextMessage.getText().toString();
                        Date date = new Date();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
                        String fullDate = dateFormat.format(date);
                        final Message senderMessage = new Message(autoId, fullDate);
                        senderMessage.setSender(false);
                        senderMessage.setContent(content);
                        final Message receiverMessage = new Message(autoId, fullDate);
                        receiverMessage.setSender(true);
                        receiverMessage.setContent(content);

                        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(currentEmail).collection(Cons.FRIENDS_COLLECTION_REF).document(receiverEmail).update("messagesId", autoId)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(currentEmail).collection(Cons.FRIENDS_COLLECTION_REF).document(receiverEmail).collection(Cons.MESSAGES_COLLECTION_REF).document().set(receiverMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //here
                                                SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(receiverEmail).collection(Cons.FRIENDS_COLLECTION_REF).document(currentEmail).update("messagesId", autoId)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(receiverEmail).collection(Cons.FRIENDS_COLLECTION_REF).document(currentEmail).collection(Cons.MESSAGES_COLLECTION_REF).document().set(senderMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(ChatActivity.this, "message sent successfully", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void inflateViews() {
        rvMessages = findViewById(R.id.rv_messages);
        messages = new ArrayList<>();
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        editTextMessage = findViewById(R.id.edit_text_message);
        circleImageViewImage = findViewById(R.id.image_view_personal_image);
        imageViewSendMessage = findViewById(R.id.image_view_send_message);
        imageViewActive = findViewById(R.id.image_view_active);
        textViewName = findViewById(R.id.text_view_name);
        textViewActive = findViewById(R.id.text_view_active);
    }
}
