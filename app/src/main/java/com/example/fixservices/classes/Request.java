package com.example.fixservices.classes;

public class Request {
    private String location, nameUser, nameProfessional, phoneUser, phoneProfessional, uidUser, uidProfessional, description;
    private Double latitude, longitude;

    private boolean professional;
//    private Location locationXY;
    public Request() {

    }

    public Request(String location, String nameUser, String phoneUser, String description, String uidUid) {
        this.location = location;
        this.nameUser = nameUser;
        this.phoneUser = phoneUser;
        this.description = description;
        this.uidUser = uidUid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

//    public Location getLocationXY() {
//        return locationXY;
//    }

//    public void setLocationXY(Location locationXY) {
//        this.locationXY = locationXY;
//    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getNameProfessional() {
        return nameProfessional;
    }

    public void setNameProfessional(String nameProfessional) {
        this.nameProfessional = nameProfessional;
    }

    public String getPhoneUser() {
        return phoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        this.phoneUser = phoneUser;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }

    public String getUidProfessional() {
        return uidProfessional;
    }

    public void setUidProfessional(String uidProfessional) {
        this.uidProfessional = uidProfessional;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public boolean isProfessional() {
        return professional;
    }

    public void setProfessional(boolean professional) {
        this.professional = professional;
    }

    public String getPhoneProfessional() {
        return phoneProfessional;
    }

    public void setPhoneProfessional(String phoneProfessional) {
        this.phoneProfessional = phoneProfessional;
    }
}
