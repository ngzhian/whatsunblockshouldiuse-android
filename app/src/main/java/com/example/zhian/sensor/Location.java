package com.example.zhian.sensor;

import java.util.Calendar;

/**
 * Created by ZhiAn on 13/3/2015.
 */
public class Location {
    private double lat;
    private double lon;
    private Calendar cal;
    
    public Location(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        cal = Calendar.getInstance();
    }
    
    public double getLatitude() {
        return lat;
    }
    
    public double getLongitude() {
        return lon;
    }
    
    public double euclideanDistance(Location other) {
        // sqrt( x**2 + x**2)
        return Math.sqrt(
            Math.pow(lat - other.getLatitude(), 2.0)
            + Math.pow(lon - other.getLongitude(), 2.0)
        );
    }
    
    public String toString() {
        return lat + "," + lon;
    }
    
    public String toCsv() {
        return cal.getTimeInMillis() + "," + lat + "," + lon;
    }
}
