package com.jlcsoftware.bloodbankcommunity.Models;

public class User_details_item {

    int photo;
    String details;


    public User_details_item(int photo, String details) {
        this.photo = photo;
        this.details = details;
    }

    public int getPhoto() {
        return photo;
    }

    public String getDetails() {
        return details;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
