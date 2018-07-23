package com.example.jeeweonlee.places_search;

import java.io.Serializable;

public class MyPlace implements Serializable{

    private String place_id;
    private String name;
    private String image;
    private String address;

    public MyPlace(String place_id, String name, String img, String address){
        this.place_id = place_id;
        this.name = name;
        this.image = img;
        this.address = address;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
