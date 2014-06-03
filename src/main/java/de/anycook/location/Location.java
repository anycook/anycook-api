package de.anycook.location;

import com.google.code.geocoder.model.LatLng;

public class Location {
    public static double distance(Location l1, Location l2) {
        double sec1 = Math.sin(l1.latitude)*Math.sin(l2.latitude);
        double dl=Math.abs(l1.longitude-l2.longitude);
        double sec2 = Math.cos(l1.latitude)* Math.cos(l2.latitude);
        //sec1,sec2,dl are in degree, need to convert to radians
        double centralAngle = Math.acos(sec1+sec2*Math.cos(dl));
        //Radius of Earth: 6378.1 kilometers
        return centralAngle * 6378.1;
    }

    private double latitude;
    private double longitude;

    public Location() {
        this.latitude = -1;
        this.longitude = -1;
    }

    public Location(double latitude, double longitude) {

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(LatLng location) {
        this.latitude = location.getLat().doubleValue();
        this.longitude = location.getLng().doubleValue();
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

    public double distance(Location l) {
        return distance(this, l);
    }

    public boolean isInRadius(double radius, Location l) {
        return distance(l) <= radius;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
