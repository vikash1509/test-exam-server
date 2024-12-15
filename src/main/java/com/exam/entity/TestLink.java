package com.exam.entity;

import jakarta.persistence.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "test_info")
public class TestLink {
    @Id
    @Column(name = "test_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testId;
    @Column(name = "test_name")
    private String testName;
    @Column(name = "test_Desc")
    private String testDesc;
    @Column(name = "test_link")
    private String testLink;
    @Column(name = "test_type")
    private String testType;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "time_duration")
    private int timeDuration;
    @Column(name = "result_file")
    private File resultFile;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "crate_date")
    private Date createDate;
    @Column(name = "total_marks")
    private int testTotalMarks;
    private String testCategory;
    private boolean resultPublish = false;
    @Column(name = "hide_test_info", nullable = false)
    private boolean hideTestInfo = false; // by default it is published after creation
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
    public Long getTestId() {
        return testId;
    }
    public void setTestId(Long testId) {
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
    public int getTimeDuration() {
        return timeDuration;
    }
    public void setTimeDuration(int timeDuration) {
        this.timeDuration = timeDuration;
    }
    public File getResultFile() {
        return resultFile;
    }
    public void setResultFile(File resultFile) {
        this.resultFile = resultFile;
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
}
