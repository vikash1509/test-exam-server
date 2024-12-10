package com.exam.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private String userId;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "user_dob")
    private LocalDate userDOB;
    @Column(name = "user_city")
    private String userCity;
    @Column(name = "user_school_or_college")
    private String userSchoolOrCollege;
    @Column(name = "user_mob")
    private String userMob;
    @Column(name = "user_mail_id")
    private String userMailId;
    @Column(name = "user_password")
    private String userPassword;
    @Column(name = "user_rank")
    private int userRank;
    
    private String otp; // Store OTP temporarily

    private LocalDateTime otpExpiry; // Expiry time for OTP


    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpExpiry(LocalDateTime otpExpiry) {
        this.otpExpiry = otpExpiry;
    }



    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    @Column(name = "user_rating")
    private int userRating = 1000;

    //User have many roles
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,mappedBy = "user")
    @JsonIgnore
    private Set<UserRole> userRoles = new HashSet<>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getUserDOB() {
        return userDOB;
    }

    public void setUserDOB(LocalDate userDOB) {
        this.userDOB = userDOB;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserSchoolOrCollege() {
        return userSchoolOrCollege;
    }

    public void setUserSchoolOrCollege(String userSchoolOrCollege) {
        this.userSchoolOrCollege = userSchoolOrCollege;
    }

    public String getUserMob() {
        return userMob;
    }

    public void setUserMob(String userMob) {
        this.userMob = userMob;
    }

    public String getUserMailId() {
        return userMailId;
    }

    public void setUserMailId(String userMailId) {
        this.userMailId = userMailId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public int getUserRank() {
        return userRank;
    }

    public void setUserRank(int userRank) {
        this.userRank = userRank;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }
}
