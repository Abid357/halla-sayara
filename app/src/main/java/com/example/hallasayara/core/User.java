package com.example.hallasayara.core;

import java.sql.Date;
import java.sql.Timestamp;

public class User {
    private int id;
    private String name;
    private int regNo;
    private Date dateOfBirth;
    private String phone;
    private char gender;
    private University university;
    private String email;
    private boolean emailStatus;
    private boolean phoneStatus;
    private boolean driverStatus;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User(User user){
        this(user.name, user.email, user.phone, user.university, user.regNo);
    }

    public User(String name, String email, String phone, University university, int regNo) {
        id = 0;
        this.name = name;
        this.regNo = regNo;
        this.dateOfBirth = null;
        this.phone = phone;
        this.gender = ' ';
        this.university = university;
        this.email = email;
        this.emailStatus = false;
        this.phoneStatus = false;
        this.driverStatus = false;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", regNo=" + regNo +
                ", dateOfBirth=" + dateOfBirth +
                ", phone='" + phone + '\'' +
                ", gender=" + gender +
                ", university=" + university +
                ", email='" + email + '\'' +
                ", emailStatus=" + emailStatus +
                ", phoneStatus=" + phoneStatus +
                ", driverStatus=" + driverStatus +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRegNo() {
        return regNo;
    }

    public void setRegNo(int regNo) {
        this.regNo = regNo;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(boolean emailStatus) {
        this.emailStatus = emailStatus;
    }

    public boolean isPhoneStatus() {
        return phoneStatus;
    }

    public void setPhoneStatus(boolean phoneStatus) {
        this.phoneStatus = phoneStatus;
    }

    public boolean isDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(boolean driverStatus) {
        this.driverStatus = driverStatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
