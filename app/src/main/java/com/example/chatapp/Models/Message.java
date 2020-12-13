package com.example.chatapp.Models;

import com.google.firebase.firestore.Exclude;

public class Message {
    private int id;
    private String date;
    private String email;
    private boolean sender;
    private String content;

    public Message(int id, String date) {
        this.id = id;
        this.date = date;
    }

    public Message(int id, String date,boolean sender) {
        this.id = id;
        this.date = date;
        this.sender = sender;
    }

    public Message(){}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSender() {
        return sender;
    }

    public void setSender(boolean sender) {
        this.sender = sender;
    }

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
