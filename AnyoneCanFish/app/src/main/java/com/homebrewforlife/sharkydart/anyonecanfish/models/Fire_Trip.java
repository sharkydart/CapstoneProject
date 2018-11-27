package com.homebrewforlife.sharkydart.anyonecanfish.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Fire_Trip {
    private double latitude;
    private double longitude;
    private String name;
    private String desc;
    private Timestamp dateStart;
    private Timestamp dateEnd;
    private GeoPoint geo_loc;

    public Fire_Trip(){}

    public Fire_Trip(double latitude, double longitude, String name, String desc, Timestamp dateStart, Timestamp dateEnd) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.desc = desc;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.geo_loc = new GeoPoint(latitude, longitude);
    }
    public Fire_Trip(GeoPoint geo_loc, String name, String desc, Timestamp dateStart, Timestamp dateEnd){
        this.geo_loc = geo_loc;
        this.latitude = geo_loc.getLatitude();
        this.longitude = geo_loc.getLongitude();
        this.name = name;
        this.desc = desc;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
//        Fire_Trip(geo_loc.getLatitude(), geo_loc.getLongitude(), name, desc, dateStart, dateEnd);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Timestamp getDateStart() {
        return dateStart;
    }

    public void setDateStart(Timestamp dateStart) {
        this.dateStart = dateStart;
    }

    public Timestamp getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Timestamp dateEnd) {
        this.dateEnd = dateEnd;
    }

    public GeoPoint getGeo_loc() {
        return geo_loc;
    }

    public void setGeo_loc(GeoPoint geo_loc) {
        this.geo_loc = geo_loc;
    }
}
