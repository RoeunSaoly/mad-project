package com.example.mad_project;

public class User {
    private String name;
    private String email;
    private String location;
    private String profileImageUrl;

    // Required empty public constructor for Firestore
    public User() {}

    public User(String name, String email, String location, String profileImageUrl) {
        this.name = name;
        this.email = email;
        this.location = location;
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
