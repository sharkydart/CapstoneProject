package com.homebrewforlife.sharkydart.anyonecanfish.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Fire_FishEvent {
    private Timestamp date;
    private String desc;
    private double latitude;
    private double longitude;
    private String image_url;
    private Boolean released;
    private String species;
    private GeoPoint geo_loc;

    public Fire_FishEvent(){}

    public Fire_FishEvent(Timestamp date, String desc, double latitude, double longitude, String image_url, Boolean released, String species) {
        this.date = date;
        this.desc = desc;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image_url = image_url;
        this.released = released;
        this.species = species;
    }

    public Fire_FishEvent(Timestamp date, String desc, String image_url, Boolean released, String species, GeoPoint geo_loc) {
        this.date = date;
        this.desc = desc;
        this.image_url = image_url;
        this.released = released;
        this.species = species;
        this.geo_loc = geo_loc;
        this.latitude = geo_loc.getLatitude();
        this.longitude = geo_loc.getLongitude();
    }

    public GeoPoint getGeo_loc() {
        return geo_loc;
    }

    public void setGeo_loc(GeoPoint geo_loc) {
        this.geo_loc = geo_loc;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Boolean getReleased() {
        return released;
    }

    public void setReleased(Boolean released) {
        this.released = released;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }
}
