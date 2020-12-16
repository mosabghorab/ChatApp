package com.example.chatapp.Models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Calendar;

public class User implements Serializable {
    private String email;
    private String mobile;
    private String password;
    private String name;
    private String personalImage;
    private String CoverImage;
    private String description;
    private long dateOfBirth;
    private boolean isAccountCompleted;
    private boolean isAccountActive;
    private int friendsNum;
    private int friendRequestsNum;
    private int friendRequestsId;
    private int friendsId;
    private long lastActiveTime;

    public User(String email,String mobile, String password, String name, String personalImage, String coverImage, String description, long dateOfBirth, boolean isAccountCompleted, boolean isAccountActive) {
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.name = name;
        this.personalImage = personalImage;
        this.CoverImage = coverImage;
        this.description = description;
        this.dateOfBirth = dateOfBirth;
        this.isAccountCompleted = isAccountCompleted;
        this.isAccountActive = isAccountActive;
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public User(String email, String password, boolean isAccountCompleted) {
        this.email = email;
        this.password = password;
        this.isAccountCompleted = isAccountCompleted;
    }

    public User() {
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public int getFriendsNum() {
        return friendsNum;
    }

    public void setFriendsNum(int friendsNum) {
        this.friendsNum = friendsNum;
    }

    public int getFriendRequestsNum() {
        return friendRequestsNum;
    }

    public void setFriendRequestsNum(int friendRequestsNum) {
        this.friendRequestsNum = friendRequestsNum;
    }

    public int getFriendRequestsId() {
        return friendRequestsId;
    }

    public void setFriendRequestsId(int friendRequestsId) {
        this.friendRequestsId = friendRequestsId;
    }

    public int getFriendsId() {
        return friendsId;
    }

    public void setFriendsId(int friendsId) {
        this.friendsId = friendsId;
    }


    @Exclude
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonalImage() {
        return personalImage;
    }

    public void setPersonalImage(String personalImage) {
        this.personalImage = personalImage;
    }

    public String getCoverImage() {
        return CoverImage;
    }

    public void setCoverImage(String coverImage) {
        CoverImage = coverImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isAccountCompleted() {
        return isAccountCompleted;
    }

    public void setAccountCompleted(boolean accountCompleted) {
        isAccountCompleted = accountCompleted;
    }

    public boolean isAccountActive() {
        return isAccountActive;
    }

    public void setAccountActive(boolean accountActive) {
        isAccountActive = accountActive;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Exclude
    public int getAge() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(this.dateOfBirth);
        int yearOfBirth = calendar1.get(Calendar.YEAR);
        Calendar calendar2 = Calendar.getInstance();
        int currentYear = calendar2.get(Calendar.YEAR);
        return currentYear - yearOfBirth;
    }
}
