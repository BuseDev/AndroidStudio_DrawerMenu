package com.example.drawer.models;

public class LabelModel {

    private String label;
    private  String description;

    public LabelModel() {

    }

    public LabelModel(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
