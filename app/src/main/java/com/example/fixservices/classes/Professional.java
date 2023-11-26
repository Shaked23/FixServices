package com.example.fixservices.classes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Professional {
    private String name, phone, domain, UID, token;
    private double score;
    private int raters, experience;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private String dateCreate;

//    private Map<String, Object> update = new HashMap<>();

    public Professional(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    public Professional(String name, String phone, String domain, int experience, double score) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Professionals");
        if(UID != null) {
            myRef = database.getReference("Professionals/" + UID);
        }
        setName(name);
        setPhone(phone);
        setDomain(domain);
        setExperience(experience);
        setScore(score);
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
        if (UID != null) {
            myRef = database.getReference("Professionals/" + this.UID);
        }
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getRaters() {
        return raters;
    }

    public void setRaters() {
        if(UID != null) {
            this.raters += 1;
            myRef.child("raters").setValue(this.raters);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void updateScore(double score){
        if(UID != null) {
            if(this.raters == 0) {
                this.score = score;
            } else {
                this.score = (getScore() * raters + score) / (raters+1);
            }
            myRef.child("score").setValue(this.score);
            setRaters();
        }
    }

    public void calculateExperience() {
        if (dateCreate != null && !dateCreate.isEmpty()) {
            // Parse the year from the dateOfCreation (assuming dateOfCreation is in "yyyy-MM-dd" format)
            int creationYear = Integer.parseInt(dateCreate.substring(0, 4));

            // Get the current year
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);

            // Calculate experience
            experience = experience + (currentYear - creationYear);
        }
    }
}
