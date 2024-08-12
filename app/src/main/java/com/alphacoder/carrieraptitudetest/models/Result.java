package com.alphacoder.carrieraptitudetest.models;

public class Result {

    private String name;
    private int questionCount;
    private int totalQuestions;
    private int correctAns=0;
    private int icon;

    public Result(String name, int questionCount, int totalQuestions, int icon,int correctAns) {
        this.name = name;
        this.questionCount = questionCount;
        this.totalQuestions = totalQuestions;
        this.correctAns = correctAns;
        this.icon = icon;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(int correctAns) {
        this.correctAns = correctAns;
    }


}
