package com.example.chatapp.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.chatapp.Activities.FreindsRequestsActivity;
import com.example.chatapp.Activities.SplashActivity;
import com.example.chatapp.Adapters.FriendsRequestsRecyclerAdapter;
import com.example.chatapp.Cons;
import com.example.chatapp.Models.FriendRequest;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class FriendRequestsService extends IntentService {
    int id = 0;
    boolean running = true;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FriendRequestsService(String name) {
        super(name);
    }

    public FriendRequestsService() {
        super("myService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (true) {
            Log.e("ttttttttt", "service");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String email = sharedPreferences.getString("email", null);
            SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).collection(Cons.FRIENDS_REQUESTS_COLLECTION_REF).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots == null) return;
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                            Log.e("ttttttttt", "service inside event");
                            FriendRequest friendRequest = documentChange.getDocument().toObject(FriendRequest.class);
                            friendRequest.setEmail(documentChange.getDocument().getId());
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
                            builder.setContentTitle("New Friend Request");
                            builder.setContentText("send to you a friend request click to open the app");
                            builder.setSmallIcon(R.drawable.ic_launcher_background);
                            builder.setDefaults(Notification.DEFAULT_ALL);
                            builder.setPriority(Notification.PRIORITY_MAX);
                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.notify(++id, builder.build());
                        }
                    }
                }
            });
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
    }
}
