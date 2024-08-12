package com.alphacoder.carrieraptitudetest.models;

import java.util.List;
import java.util.Map;

public class ResultDetail {

    private String studentId;
    private String id;
    private String studentName;
    private String testName;
    private long testDate;
    private int totalQuestions;
    private int attemptedQuestions;
    private String status;
    private Map<String,Category> categoryMap;

    // Default constructor required for Firebase
    public ResultDetail() {

    }





    // Getters and setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public long getTestDate() {
        return testDate;
    }

    public void setTestDate(long testDate) {
        this.testDate = testDate;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getAttemptedQuestions() {
        return attemptedQuestions;
    }

    public void setAttemptedQuestions(int attemptedQuestions) {
        this.attemptedQuestions = attemptedQuestions;
    }




    public String getResultStatus() {
        return status;
    }

    public void setResultStatus(String resultStatus) {
        this.status = resultStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public Map<String, Category> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(Map<String, Category> categoryMap) {
        this.categoryMap = categoryMap;
    }
}
