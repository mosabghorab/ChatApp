package com.example.chatapp.Activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapters.UserRecyclerAdapter;
import com.example.chatapp.Cons;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FindFreindsActivity extends AppCompatActivity {
    private RecyclerView rvUsers;
    UserRecyclerAdapter userRecyclerAdapter;
    private List<User> users;
    private String email;
    TextView textViewBlank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_freinds);
        textViewBlank = findViewById(R.id.text_view_blank);
        rvUsers = findViewById(R.id.rv_users);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        users = new ArrayList<>();
        // user email
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("email")) {
            email = intent.getStringExtra("email");
        }
        userRecyclerAdapter = new UserRecyclerAdapter(this, users, email);
        rvUsers.setAdapter(userRecyclerAdapter);
        getAllUsers();

    }

    private void getAllUsers() {
        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() == 0) {
                    textViewBlank.setVisibility(View.VISIBLE);
                    rvUsers.setVisibility(View.GONE);
                } else {
                    textViewBlank.setVisibility(View.GONE);
                    rvUsers.setVisibility(View.VISIBLE);
                }
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    User user = queryDocumentSnapshot.toObject(User.class);
                    user.setEmail(queryDocumentSnapshot.getId());
                    if (!user.getEmail().equals(email)) {
                        users.add(user);
                    }
                }
                userRecyclerAdapter.setUsers(users);
                userRecyclerAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FindFreindsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
