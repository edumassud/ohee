package com.example.ohee.model;

import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class Rating {
    private String university;
    private float parties, studentLife, campus, dorms, food, safety, professors, location, athletics, academics, diversity, careerCounsiling,  avarage;


    public void save() {

        setAvarage((getParties() +
                getStudentLife() +
                getCampus() +
                getDorms() +
                getFood() +
                getSafety() +
                getProfessors() +
                getLocation() +
                getAthletics() +
                getAcademics()) / 10);
        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        databaseReference.child("ratings")
                .child(this.getUniversity())
                .child(SetFirebaseUser.getUsersId())
                .setValue(this);


    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public float getParties() {
        return parties;
    }

    public void setParties(float parties) {
        this.parties = parties;
    }

    public float getStudentLife() {
        return studentLife;
    }

    public void setStudentLife(float studentLife) {
        this.studentLife = studentLife;
    }

    public float getCampus() {
        return campus;
    }

    public void setCampus(float campus) {
        this.campus = campus;
    }

    public float getDorms() {
        return dorms;
    }

    public void setDorms(float dorms) {
        this.dorms = dorms;
    }

    public float getFood() {
        return food;
    }

    public void setFood(float food) {
        this.food = food;
    }

    public float getSafety() {
        return safety;
    }

    public void setSafety(float safety) {
        this.safety = safety;
    }

    public float getProfessors() {
        return professors;
    }

    public void setProfessors(float professors) {
        this.professors = professors;
    }

    public float getLocation() {
        return location;
    }

    public void setLocation(float location) {
        this.location = location;
    }

    public float getAthletics() {
        return athletics;
    }

    public void setAthletics(float athletics) {
        this.athletics = athletics;
    }

    public float getAcademics() {
        return academics;
    }

    public float getDiversity() {
        return diversity;
    }

    public void setDiversity(float diversity) {
        this.diversity = diversity;
    }

    public void setAcademics(float academics) {
        this.academics = academics;
    }

    public float getCareerCounsiling() {
        return careerCounsiling;
    }

    public void setCareerCounsiling(float careerCounsiling) {
        this.careerCounsiling = careerCounsiling;
    }

    public float getAvarage() {
        return avarage;
    }

    public void setAvarage(float avarage) {
        this.avarage = avarage;
    }
}
