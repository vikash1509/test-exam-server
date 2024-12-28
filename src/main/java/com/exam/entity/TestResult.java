package com.exam.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String submittedTime;
    private String name;
    private int marks;
    private String result;
    private String answerSheetLink;
    @Column(name = "test_id")
    private String testId;
    private String userRollNo;
    private int differenceForRating;
    private String startTime;
    private Long timeDuration;
    @Column(name = "student_rank")
    private Integer rank;
    private String userEmail;
    private String userSchoolOrCollegeName;
    private String userId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)  // Lazy fetching to load the test details only when needed
    @JoinColumn(name = "test_id", referencedColumnName = "test_id", insertable = false, updatable = false)
    @JsonIgnore
    private TestLink testLink; // ManyToOne relationship with TestLink
    // Added Many-to-One relationship with User entity
//    @ManyToOne(fetch = FetchType.LAZY)  // Lazy fetching for user details
//    @JoinColumn(name = "user_roll_no", referencedColumnName = "user_roll_no", insertable = false, updatable = false)
//    private User user; // **New field for the relationship**
    public String getTestId() {
    return testId;
}
    public void setTestId(String testId) {
        this.testId = testId;
    }
    public int getMarks() {
        return marks;
    }
    public void setMarks(int marks) {
        this.marks = marks;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSubmittedTime() {
        return submittedTime;
    }
    public void setSubmittedTime(String submittedTime) {
        this.submittedTime = submittedTime;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public String getAnswerSheetLink() {
        return answerSheetLink;
    }
    public void setAnswerSheetLink(String answerSheetLink) {
        this.answerSheetLink = answerSheetLink;
    }
    public TestLink getTestLink() {
        return testLink;
    }
    public void setTestLink(TestLink testLink) {
        this.testLink = testLink;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public Long getTimeDuration() {
        return timeDuration;
    }
    public void setTimeDuration(Long timeDuration) {
        this.timeDuration = timeDuration;
    }
    public Integer getRank() {
        return rank;
    }
    public void setRank(Integer rank) {
        this.rank = rank;
    }
    public String getUserRollNo() {
        return userRollNo;
    }
    public void setUserRollNo(String userRollNo) {
        this.userRollNo = userRollNo;
    }
    public int getDifferenceForRating() {
        return differenceForRating;
    }
    public void setDifferenceForRating(int marksDifference) {
        this.differenceForRating = marksDifference;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
//    public User getUser() {
//        return user;
//    }
//    public void setUser(User user) {
//        this.user = user;
//    }
    public String getUserSchoolOrCollegeName() {
        return userSchoolOrCollegeName;
    }
    public void setUserSchoolOrCollegeName(String userSchoolOrCollegeName) {
        this.userSchoolOrCollegeName = userSchoolOrCollegeName;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
