package org.hunch.enums.core;

public enum LatLong {

    USA_CA(36.5668831, -119.5869091),
    INDIA_DELHI(28.5366261, 77.2694981),
    USA_NY(40.7663865, -73.9796065),;

    private final double latitude;
    private final double longitude;

    LatLong(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCoordinates() {
        return latitude + "," + longitude;
    }

}
