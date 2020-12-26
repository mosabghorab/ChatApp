package com.example.chatapp.Activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Cons;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView textViewName, textViewDescription, textViewAge, textViewEmail;
    ImageView imageViewCoverImage;
    CircleImageView circleImageViewPersonalImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        inflateViews();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("email")) {
            final String email = intent.getStringExtra("email");
            SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    user.setEmail(documentSnapshot.getId());
                    textViewName.setText(user.getName());
                    textViewDescription.setText(user.getDescription());
                    textViewEmail.setText(email);
                    textViewAge.setText(user.getAge()+"");
                    if (user.getPersonalImage() != null)
                        Picasso.get().load(user.getPersonalImage()).placeholder(R.drawable.student_default_photo).into(circleImageViewPersonalImage);
                    if (user.getCoverImage() != null)
                        Picasso.get().load(user.getCoverImage()).placeholder(R.drawable.default_cover).into(imageViewCoverImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void inflateViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewAge = findViewById(R.id.text_view_age);
        textViewName = findViewById(R.id.text_view_name);
        textViewEmail = findViewById(R.id.text_view_email);
        textViewDescription = findViewById(R.id.text_view_description);
        imageViewCoverImage = findViewById(R.id.image_view_cover_image);
        circleImageViewPersonalImage = findViewById(R.id.cmv_personal_image);
    }
}
