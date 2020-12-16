package com.example.chatapp.TestCases;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.chatapp.Activities.SplashActivity;
import com.example.chatapp.Cons;
import com.example.chatapp.Models.Friend;
import com.example.chatapp.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ChatTest {
    private FirebaseFirestore firebaseFirestore;
    private boolean isFriedRequestSentSuccessfully;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("BeforeClass");
    }

    @Before
    public void before() {
        System.out.println("Before");
        firebaseFirestore = FirebaseFirestore.getInstance();
        isFriedRequestSentSuccessfully = false;
    }

    @Test
    public void sendFriendRequest() {
        System.out.println("send fried request");
        User user = new User("mosab@gmail.com", "123456",
                false);
        Friend friend = new Friend(10, "2020/20/8",
                user.getEmail());
        firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).
                document(user.getEmail()).collection(Cons.FRIENDS_COLLECTION_REF).
                document(user.getEmail()).set(friend).
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    isFriedRequestSentSuccessfully = true;
                assertTrue(isFriedRequestSentSuccessfully);

            }
        });
    }

    @After
    public void after() {
        System.out.println("After");
        isFriedRequestSentSuccessfully = false;
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("AfterClass");
    }
}
