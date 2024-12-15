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
    @Column(name = "user_id")
    private String userId;
    private int marks;
    private String result;
    private String answerSheetLink;

    @Column(name = "test_id")
    private Long testId;

    public int getMarksDifference() {
        return marksDifference;
    }

    public void setMarksDifference(int marksDifference) {
        this.marksDifference = marksDifference;
    }

    @Column(name = "marks_difference")
    private int marksDifference;

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)  // Lazy fetching to load the test details only when needed
    @JoinColumn(name = "test_id", referencedColumnName = "test_id", insertable = false, updatable = false)
    @JsonIgnore
    private TestLink testLink; // ManyToOne relationship with TestLink

    @ManyToOne(fetch = FetchType.LAZY, optional = false)  // Lazy fetching to load the test details only when needed
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user; // ManyToOne relationship with TestLink

    private Date createDate;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String startTime;
    private Long timeDuration;
    @Column(name = "student_rank")
    private Integer rank;

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

}
