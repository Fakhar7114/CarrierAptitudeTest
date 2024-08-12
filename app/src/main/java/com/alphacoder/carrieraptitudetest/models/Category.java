package com.alphacoder.carrieraptitudetest.models;

public class Category {
    private String name;
    private  int icon;
    private double percentage;


    public Category(){

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
    public Category(String name,int icon){
        this.name=name;
        this.icon=icon;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

}
