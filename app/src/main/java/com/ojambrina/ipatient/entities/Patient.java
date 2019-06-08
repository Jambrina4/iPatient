package com.ojambrina.ipatient.entities;

import java.io.Serializable;
import java.util.List;

public class Patient implements Serializable {

    private String name;
    private String surname;
    private String bornDate;
    private String phone;
    private String email;
    private String profession;
    private String profileImage;
    private List<String> regularMedication;
    private List<String> medicConditions;
    private List<String> regularExercise;
    private List<String> surgicalOperations;
    private List<String> medicExamination;

    public Patient() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBornDate() {
        return bornDate;
    }

    public void setBornDate(String bornDate) {
        this.bornDate = bornDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public List<String> getRegularMedication() {
        return regularMedication;
    }

    public void setRegularMedication(List<String> regularMedication) {
        this.regularMedication = regularMedication;
    }

    public List<String> getMedicConditions() {
        return medicConditions;
    }

    public void setMedicConditions(List<String> medicConditions) {
        this.medicConditions = medicConditions;
    }

    public List<String> getRegularExercise() {
        return regularExercise;
    }

    public void setRegularExercise(List<String> regularExercise) {
        this.regularExercise = regularExercise;
    }

    public List<String> getSurgicalOperations() {
        return surgicalOperations;
    }

    public void setSurgicalOperations(List<String> surgicalOperations) {
        this.surgicalOperations = surgicalOperations;
    }

    public List<String> getMedicExamination() {
        return medicExamination;
    }

    public void setMedicExamination(List<String> medicExamination) {
        this.medicExamination = medicExamination;
    }
}
