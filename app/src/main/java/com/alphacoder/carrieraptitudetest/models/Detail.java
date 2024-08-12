package com.alphacoder.carrieraptitudetest.models;

import java.util.List;

public class Detail {

    private String name;
    private String description;
    private List<String> careers;



    public Detail(){

    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getCareers() {
        return careers;
    }

    public void setCareers(List<String> careers) {
        this.careers = careers;
    }


}
