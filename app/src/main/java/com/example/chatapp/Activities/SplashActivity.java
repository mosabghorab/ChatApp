package com.example.chatapp.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.chatapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
public class SplashActivity extends AppCompatActivity {
    public static FirebaseFirestore firebaseFirestore;
    public static FirebaseStorage firebaseStorage;

    static {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        createChannel();
        // Firebase variables implementation

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // this code will executed after 2 seconds
                checkUser();
            }
        }, 2000);
    }

    //************************************* Private Methods ***********************************//

    private void checkUser(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.getBoolean("isLogin",false)){
            String email = sharedPreferences.getString("email",null);
            startActivity(new Intent(getBaseContext(), MainActivity.class).putExtra("email",email));
            finish();
        }else{
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
            finish();
        }
    }
    private void createChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("ch","channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }
}
