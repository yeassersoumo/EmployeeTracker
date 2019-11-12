package com.example.soumo.locationtracker;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class CustomLocation {

    private Double latitude;
    private Double longitude;
    private Long time;

    public CustomLocation(){

    }

    @Override
    public String toString() {
        return "CustomLocation{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public CustomLocation(Double latitude, Double longitude, Long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;

    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {

        this.time = time;

    }
}
