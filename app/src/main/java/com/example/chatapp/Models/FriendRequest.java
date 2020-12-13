package com.example.chatapp.Models;

import com.google.firebase.firestore.Exclude;

public class FriendRequest {
    private int id;
    private String date;
    private String email;

    public FriendRequest(int id, String date, String email) {
        this.id = id;
        this.date = date;
        this.email = email;
    }

    public FriendRequest(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    @Exclude
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
