package com.example.chatapp.Fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatapp.Activities.SplashActivity;
import com.example.chatapp.Adapters.ContactsRecyclerAdapter;
import com.example.chatapp.Cons;
import com.example.chatapp.Models.Friend;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {
    private RecyclerView rvContacts;
    private List<Friend> friends;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        friends = new ArrayList<>();
        Bundle bundle = getArguments();
        final User currentUser = (User) bundle.getSerializable("currentUser");
        rvContacts = view.findViewById(R.id.rv_contacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(view.getContext()));
        final ContactsRecyclerAdapter contactsRecyclerAdapter = new ContactsRecyclerAdapter(view.getContext(), friends, currentUser.getEmail());
        rvContacts.setAdapter(contactsRecyclerAdapter);
        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(currentUser.getEmail()).collection(Cons.FRIENDS_COLLECTION_REF).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                friends.clear();
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    Friend friend = queryDocumentSnapshot.toObject(Friend.class);
                    friend.setEmail(queryDocumentSnapshot.getId());
                    friends.add(friend);
                }
                contactsRecyclerAdapter.setFriends(friends);
                contactsRecyclerAdapter.notifyDataSetChanged();
            }
        });
        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(currentUser.getEmail()).collection(Cons.FRIENDS_COLLECTION_REF).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        friends.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            Friend friend = queryDocumentSnapshot.toObject(Friend.class);
                            friend.setEmail(queryDocumentSnapshot.getId());
                            friends.add(friend);
                        }
                        contactsRecyclerAdapter.setFriends(friends);
                        contactsRecyclerAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}
