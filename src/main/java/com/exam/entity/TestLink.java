package com.exam.entity;

import jakarta.persistence.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "test_info")
public class TestLink {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Hibernate 6+ supports this
    @Column(name = "test_id", updatable = false, nullable = false, unique = true)
    private String testId;
    @Column(name = "test_name")
    private String testName;
    @Column(name = "test_Desc")
    private String testDesc;
    @Column(name = "test_link")
    private String testLink;
    @Column(name = "test_type")
    private String testType;
    @Column(name = "test_for")
    private String testFor;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "time_duration")
    private Long timeDuration;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "crate_date")
    private Date createDate;
    @Column(name = "total_marks")
    private int testTotalMarks;
    private String testCategory;
    private boolean resultPublish = false;
    private boolean resultFileUploaded = false;
    private Date resultUploadDate;
    private String TestProviderName;
    @Column(name = "hide_test_info", nullable = false)
    private boolean hideTestInfo = false; // by default it is published after creation
    private boolean automateQuiz =false;
    public String getTestCategory() {
        return testCategory;
    }
    public void setTestCategory(String testCategory) {
        this.testCategory = testCategory;
    }
    public String getTestType() {
        return testType;
    }
    public void setTestType(String testType) {
        this.testType = testType;
    }
    public int getTestTotalMarks() {
        return testTotalMarks;
    }
    public void setTestTotalMarks(int testTotalMarks) {
        this.testTotalMarks = testTotalMarks;
    }
    public String getTestId() {
        return testId;
    }
    public void setTestId(String testId) {
        this.testId = testId;
    }
    public String getTestName() {
        return testName;
    }
    public void setTestName(String testName) {
        this.testName = testName;
    }
    public String getTestLink() {
        return testLink;
    }
    public void setTestLink(String testLink) {
        this.testLink = testLink;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public Long getTimeDuration() {
        return timeDuration;
    }
    public void setTimeDuration(Long timeDuration) {
        this.timeDuration = timeDuration;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public String getTestDesc() {
        return testDesc;
    }
    public void setTestDesc(String testDesc) {
        this.testDesc = testDesc;
    }
    public boolean isResultPublish() {
        return resultPublish;
    }
    public void setResultPublish(boolean resultPublish) {
        this.resultPublish = resultPublish;
    }
    public boolean isHideTestInfo() {
        return hideTestInfo;
    }
    public void setHideTestInfo(boolean hideTestInfo) {
        this.hideTestInfo = hideTestInfo;
    }
    public boolean isResultFileUploaded() {
        return resultFileUploaded;
    }
    public void setResultFileUploaded(boolean resultFileUploaded) {
        this.resultFileUploaded = resultFileUploaded;
    }
    public String getTestFor() {
        return testFor;
    }
    public void setTestFor(String testFor) {
        this.testFor = testFor;
    }
    public Date getResultUploadDate() {
        return resultUploadDate;
    }
    public void setResultUploadDate(Date resultUploadDate) {
        this.resultUploadDate = resultUploadDate;
    }
    public String getTestProviderName() {
        return TestProviderName;
    }
    public void setTestProviderName(String testProviderName) {
        TestProviderName = testProviderName;
    }
    public boolean isAutomateQuiz() {
        return automateQuiz;
    }
    public void setAutomateQuiz(boolean automateQuiz) {
        this.automateQuiz = automateQuiz;
    }
}
