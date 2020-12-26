package com.example.chatapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Cons;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class SignupActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPassword;
    Button buttonSignup;
    ProgressBar progressBar;
    TextView textViewAlreadyHaveAccountLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        inflateViews();
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        textViewAlreadyHaveAccountLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                finish();
            }
        });
    }

    //************************************* Private Methods ***********************************//
    // inflate views from xml file
    private void inflateViews() {
        progressBar = findViewById(R.id.progressBar);
        buttonSignup = findViewById(R.id.btn_signup);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        textViewAlreadyHaveAccountLogin = findViewById(R.id.text_view_login);
    }

    // when signup button clicked
    private void signup() {
        progressBar.setVisibility(View.VISIBLE);
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        if (email.trim().equals("") || password.trim().equals("")) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(SignupActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        User user = new User(email, password, false);
        SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(getBaseContext(), SetupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
