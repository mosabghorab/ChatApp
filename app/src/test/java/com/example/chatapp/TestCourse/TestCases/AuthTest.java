package com.example.chatapp.TestCourse.TestCases;

import androidx.annotation.NonNull;

import com.example.chatapp.Cons;
import com.example.chatapp.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AuthTest {
    FirebaseFirestore firebaseFirestore;
    private boolean isUserCreatedSuccessfully;
    private boolean isUserLoggedInSuccessfully;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("BeforeClass");
    }

    @Before
    public void before() {
        System.out.println("Before");
        firebaseFirestore =  FirebaseFirestore.getInstance();
        isUserCreatedSuccessfully = false;
        isUserLoggedInSuccessfully = false;
    }

    @Test
    public void loginTest() {
        System.out.println("Login Test Case");
        User user = new User("mosab@gmail.com", "123456"
                , true);
       firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).
               document(user.getEmail())
               .get().
               addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null)
                        isUserLoggedInSuccessfully = true;

                }
                assertTrue(isUserLoggedInSuccessfully);
            }
        });
    }

    @Test
    public void signUpTest() {
        System.out.println("Sign Up Test Case");
        User user = new User("mosab@gmail.com"
                ,"123456",false);
        firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).
                document(user.getEmail()).set(user).
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    isUserCreatedSuccessfully = true;
                assertTrue(isUserCreatedSuccessfully);
            }
        });
    }

    @After
    public void after() {
        System.out.println("After");
        isUserCreatedSuccessfully = false;
        isUserLoggedInSuccessfully = false;
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("AfterClass");
    }
}
