package com.example.chatapp.Activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.chatapp.Adapters.TabsAdapter;
import com.example.chatapp.Cons;
import com.example.chatapp.Fragments.ChatsFragment;
import com.example.chatapp.Fragments.ContactsFragment;
import com.example.chatapp.Fragments.GroupsFragment;
import com.example.chatapp.Models.FriendRequest;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private User currentUser;
    private String email;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inflateViews();
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
                initViewPager();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).collection(Cons.FRIENDS_REQUESTS_COLLECTION_REF).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                id = 0;
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                        FriendRequest friendRequest = documentChange.getDocument().toObject(FriendRequest.class);
                        friendRequest.setEmail(documentChange.getDocument().getId());
                        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(friendRequest.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User receiverUser = documentSnapshot.toObject(User.class);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(),"ch");
                                builder.setContentTitle("New Friend Request");
                                builder.setContentText(receiverUser.getName() + " send to you a friend request click to open the app");
                                builder.setSmallIcon(R.drawable.ic_launcher_background);
                                builder.setDefaults(Notification.DEFAULT_ALL);
                                builder.setPriority(Notification.PRIORITY_MAX);
                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(++id, builder.build());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

            }
        });

    }

    //************************************* Callback Methods ***********************************//


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_logout) {
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Logging out...");
            progressDialog.show();
            SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).update("accountActive", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    long currentMilliSecond = System.currentTimeMillis();
                    SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).update("lastActiveTime", currentMilliSecond).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "Logging out successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getBaseContext(), LoginActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else if (item.getItemId() == R.id.btn_find_friends) {
            startActivity(new Intent(this, FindFreindsActivity.class).putExtra("email", email));
        } else if (item.getItemId() == R.id.btn_friends_requests) {
            startActivity(new Intent(this, FreindsRequestsActivity.class).putExtra("email", email));
        } else if (item.getItemId() == R.id.btn_profile) {
            startActivity(new Intent(this, ProfileActivity.class).putExtra("email", email));
        }
        return true;
    }

    //************************************* Private Methods ***********************************//
    //  inflate views from xml file
    private void inflateViews() {
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        toolbar.setTitle("ChatApp");
        setSupportActionBar(toolbar);
    }

    // init view pager with tabs layout
    private void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        FragmentManager fragmentManager = getSupportFragmentManager();
        ChatsFragment chatsFragment = new ChatsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("currentUser", currentUser);
        chatsFragment.setArguments(bundle);
        GroupsFragment groupsFragment = new GroupsFragment();
        groupsFragment.setArguments(bundle);
        ContactsFragment contactsFragment = new ContactsFragment();
        contactsFragment.setArguments(bundle);
        fragments.add(chatsFragment);
        fragments.add(contactsFragment);
        fragments.add(groupsFragment);
        List<String> titles = new ArrayList<>();
        titles.add("Chats");
        titles.add("Contacts");
        titles.add("Groups");
        TabsAdapter tabsAdapter = new TabsAdapter(fragmentManager, fragments, titles);
        viewPager.setAdapter(tabsAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
