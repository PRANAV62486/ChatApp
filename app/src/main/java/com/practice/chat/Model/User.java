package com.practice.chat.Model;

public class User {
    public String name,email,uid,profileImage;

    public User(String name, String email, String uid, String profileImage) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.profileImage = profileImage;
    }

    public User() {
    }
}
