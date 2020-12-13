package com.example.chatapp.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chatapp.Cons;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    Button btnSave;
    ImageView imageViewCoverImage, imageViewPopupMenuCoverImage, imageViewPopupMenuPersonalImage, imageViewDate;
    CircleImageView imageViewPersonalImage;
    EditText editTextName, editTextDescription;
    TextView textViewEmail, textViewDate;
    PopupMenu popupMenuCoverImage, popupMenuPersonalImage;
    String coverImageUrl, personalImageUrl;
    Uri coverImageUri, personalImageUri;
    long dateOfBirth = -1;
    ProgressDialog uploadProgressDialog, storeProgressDialog;
    String  password, email;
    Handler handler;
    StorageReference personalStorageRef, coverStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 5);
        }
        inflateViews();
        imageViewPopupMenuCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenuCoverImage.show();
            }
        });
        imageViewPopupMenuPersonalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenuPersonalImage.show();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri uri = data.getData();
            Bitmap bitmap = null;
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                bitmap = (Bitmap) bundle.get("data");
            }
            if (uri != null) {
                if (requestCode == 2) {
                    personalImageUri = uri;
                    Picasso.get().load(personalImageUri).into(imageViewPersonalImage);
                } else if (requestCode == 4) {
                    coverImageUri = uri;
                    Picasso.get().load(coverImageUri).into(imageViewCoverImage);
                }
            } else {
                if (requestCode == 1) {
                    personalImageUri = getImageUri(this, bitmap);
                    Picasso.get().load(personalImageUri).into(imageViewPersonalImage);
                } else if (requestCode == 3) {
                    coverImageUri = getImageUri(this, bitmap);
                    Picasso.get().load(coverImageUri).into(imageViewCoverImage);
                }
            }
        }
    }

    //***************************** Private Methods ***************************************//
    private void inflateViews() {
        btnSave = findViewById(R.id.btn_save);
        editTextName = findViewById(R.id.edit_text_name);
        editTextDescription = findViewById(R.id.edit_text_description);
        textViewDate = findViewById(R.id.text_view_date);
        textViewEmail = findViewById(R.id.text_view_email);
        imageViewCoverImage = findViewById(R.id.image_view_cover_image);
        imageViewPersonalImage = findViewById(R.id.image_view_personal_image);
        imageViewPopupMenuCoverImage = findViewById(R.id.image_view_pop_up_menu_cover_image);
        imageViewPopupMenuPersonalImage = findViewById(R.id.image_view_pop_up_menu_personal_image);
        imageViewDate = findViewById(R.id.image_view_date);
        personalStorageRef = SplashActivity.firebaseStorage.getReference(Cons.PERSONAL_IMAGES_STORAGE);
        coverStorageRef = SplashActivity.firebaseStorage.getReference(Cons.COVER_IMAGES_STORAGE);
        handler = new Handler();
        uploadProgressDialog = new ProgressDialog(this);
        uploadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadProgressDialog.setCanceledOnTouchOutside(false);
        storeProgressDialog = new ProgressDialog(this);
        storeProgressDialog.setTitle("User data");
        storeProgressDialog.setMessage("Saving user data...");
        popupMenuPersonalImage = new PopupMenu(this, imageViewPopupMenuPersonalImage);
        popupMenuPersonalImage.inflate(R.menu.popup_menu);
        popupMenuPersonalImage.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.btn_camera) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (item.getItemId() == R.id.btn_gallery) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 2);
                }
                return true;
            }
        });
        popupMenuCoverImage = new PopupMenu(this, imageViewPopupMenuCoverImage);
        popupMenuCoverImage.inflate(R.menu.popup_menu);
        popupMenuCoverImage.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.btn_camera) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 3);
                } else if (item.getItemId() == R.id.btn_gallery) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 4);
                }
                return true;
            }
        });
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("email") && intent.hasExtra("password")) {
            email = intent.getStringExtra("email");
            textViewEmail.setText(email);
            password = intent.getStringExtra("password");
        }
        imageViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentDate = Calendar.getInstance();
                int year = currentDate.get(Calendar.YEAR);
                int month = currentDate.get(Calendar.MONTH);
                final int day = currentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(SetupActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(Calendar.YEAR, year);
                        newDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        newDate.set(Calendar.MONTH, month);
                        dateOfBirth = newDate.getTimeInMillis();
                        textViewDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void save() {
        final String name = editTextName.getText().toString();
        final String description = editTextDescription.getText().toString();
        if (name.trim().equals("") || description.trim().equals("") || dateOfBirth == -1) {
            Toast.makeText(this, "Missing data please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (personalImageUri != null && coverImageUri != null) {
            uploadProgressDialog.setMessage("Uploading personal image...");
            uploadProgressDialog.show();
            personalStorageRef.child(email).putFile(personalImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    uploadProgressDialog.setProgress(100);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            uploadProgressDialog.dismiss();
                            personalStorageRef.child(email).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    personalImageUrl = uri.toString();
                                    uploadProgressDialog.setMessage("Uploading cover image...");
                                    uploadProgressDialog.show();
                                    coverStorageRef.child(email).putFile(coverImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            uploadProgressDialog.setProgress(100);
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    uploadProgressDialog.dismiss();
                                                    coverStorageRef.child(email).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            coverImageUrl = uri.toString();
                                                            storeProgressDialog.show();
                                                            User user = new User(email, password, name, personalImageUrl, coverImageUrl, description, dateOfBirth, true, true);
                                                            SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                    editor.putBoolean("isLogin",true);
                                                                    editor.putString("email",email);
                                                                    editor.apply();
                                                                    Toast.makeText(SetupActivity.this, "User saved successfully", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(getBaseContext(),MainActivity.class).putExtra("email",email));
                                                                    finish();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }, 1000);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            uploadProgressDialog.dismiss();
                                            Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            uploadProgressDialog.setProgress((int) ((taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) * 100));
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("ggggg", e.toString());
                                    Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }, 1000);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadProgressDialog.dismiss();
                    Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadProgressDialog.setProgress((int) ((taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) * 100));
                }
            });
        } else if (personalImageUri != null) {
            uploadProgressDialog.setMessage("Uploading personal image...");
            uploadProgressDialog.show();
            personalStorageRef.child(email).putFile(personalImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadProgressDialog.setProgress(100);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            uploadProgressDialog.dismiss();
                            personalStorageRef.child(email).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    personalImageUrl = uri.toString();
                                    storeProgressDialog.show();
                                    User user = new User(email, password, name, personalImageUrl, coverImageUrl, description, dateOfBirth, true, true);
                                    SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            storeProgressDialog.dismiss();
                                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putBoolean("isLogin",true);
                                            editor.putString("email",email);
                                            editor.apply();
                                            Toast.makeText(SetupActivity.this, "User saved successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getBaseContext(), MainActivity.class).putExtra("email",email));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }, 1000);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadProgressDialog.dismiss();
                    Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadProgressDialog.setProgress((int) ((taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) * 100));
                }
            });
        } else if (coverImageUri != null) {
            uploadProgressDialog.setMessage("Uploading cover image...");
            uploadProgressDialog.show();
            coverStorageRef.child(email).putFile(coverImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadProgressDialog.setProgress(100);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            uploadProgressDialog.dismiss();
                            coverStorageRef.child(email).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    coverImageUrl = uri.toString();
                                    storeProgressDialog.show();
                                    User user = new User(email, password, name, personalImageUrl, coverImageUrl, description, dateOfBirth, true, true);
                                    SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            storeProgressDialog.dismiss();
                                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putBoolean("isLogin",true);
                                            editor.putString("email",email);
                                            editor.apply();
                                            Toast.makeText(SetupActivity.this, "User saved successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getBaseContext(),MainActivity.class).putExtra("email",email));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }, 1000);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadProgressDialog.dismiss();
                    Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadProgressDialog.setProgress((int) ((taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) * 100));
                }
            });
        } else {
            storeProgressDialog.show();
            User user = new User(email, password, name, personalImageUrl, coverImageUrl, description, dateOfBirth, true, true);
            SplashActivity.firebaseFirestore.collection(Cons.USERS_COLLECTION_REF).document(email).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    storeProgressDialog.dismiss();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLogin",true);
                    editor.putString("email",email);
                    editor.apply();
                    Toast.makeText(SetupActivity.this, "User saved successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getBaseContext(),MainActivity.class).putExtra("email",email));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    storeProgressDialog.dismiss();
                    Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}
