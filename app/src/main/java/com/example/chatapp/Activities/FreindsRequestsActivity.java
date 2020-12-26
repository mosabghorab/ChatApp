package com.example.chatapp.Activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapters.FriendsRequestsRecyclerAdapter;
import com.example.chatapp.Cons;
import com.example.chatapp.Models.FriendRequest;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class FreindsRequestsActivity extends AppCompatActivity {
    private RecyclerView rvFriendsRequests;
    FriendsRequestsRecyclerAdapter friendsRequestsRecyclerAdapter;
    private List<FriendRequest> friendRequests;
    private String email;
    private User currentUser;
    TextView textViewBlank;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freinds_requests);
        textViewBlank = findViewById(R.id.text_view_blank);
        // user email
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("email")) {
            email = intent.getStringExtra("email");
        }

        //Get current user
        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                currentUser.setEmail(documentSnapshot.getId());
                rvFriendsRequests = findViewById(R.id.rv_friends_requests);
                rvFriendsRequests.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                friendRequests = new ArrayList<>();
                friendsRequestsRecyclerAdapter = new FriendsRequestsRecyclerAdapter(getBaseContext(), friendRequests, currentUser);
                rvFriendsRequests.setAdapter(friendsRequestsRecyclerAdapter);
                SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).collection(Cons.FRIENDS_REQUESTS_COLLECTION_REF).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        id = 0;
                        if (friendsRequestsRecyclerAdapter == null)
                            friendsRequestsRecyclerAdapter = new FriendsRequestsRecyclerAdapter(getBaseContext(), friendRequests, currentUser);
                        if (friendRequests == null)
                            friendRequests = new ArrayList<>();
                        friendRequests.clear();
                        if (queryDocumentSnapshots == null) return;
                        if (queryDocumentSnapshots.size() == 0) {
                            rvFriendsRequests.setVisibility(View.GONE);
                            textViewBlank.setVisibility(View.VISIBLE);
                        } else {
                            rvFriendsRequests.setVisibility(View.VISIBLE);
                            textViewBlank.setVisibility(View.GONE);
                        }
                        for (DocumentSnapshot DocumentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            FriendRequest friendRequest = DocumentSnapshot.toObject(FriendRequest.class);
                            friendRequest.setEmail(DocumentSnapshot.getId());
                            friendRequests.add(friendRequest);
                        }
                        friendsRequestsRecyclerAdapter.setFriendRequests(friendRequests);
                        friendsRequestsRecyclerAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FreindsRequestsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getFriendsRequests(){
        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).collection(Cons.FRIENDS_REQUESTS_COLLECTION_REF).orderBy("id", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (friendsRequestsRecyclerAdapter == null)
                    friendsRequestsRecyclerAdapter = new FriendsRequestsRecyclerAdapter(getBaseContext(), friendRequests, currentUser);
                if (friendRequests == null)
                    friendRequests = new ArrayList<>();
                friendRequests.clear();
                if (queryDocumentSnapshots == null) return;
                if (queryDocumentSnapshots.size() == 0) {
                    rvFriendsRequests.setVisibility(View.GONE);
                    textViewBlank.setVisibility(View.VISIBLE);
                } else {
                    rvFriendsRequests.setVisibility(View.VISIBLE);
                    textViewBlank.setVisibility(View.GONE);
                }
                for (DocumentSnapshot DocumentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    FriendRequest friendRequest = DocumentSnapshot.toObject(FriendRequest.class);
                    friendRequest.setEmail(DocumentSnapshot.getId());
                    friendRequests.add(friendRequest);
                }
                friendsRequestsRecyclerAdapter.setFriendRequests(friendRequests);
                friendsRequestsRecyclerAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FreindsRequestsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
