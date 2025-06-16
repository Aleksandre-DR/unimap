package com.example.unimap.jsonConvertor.toJson;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Subjects {
    @SerializedName("საგნები")
    private ArrayList<Subject> subjects;

    public Subjects() {
        this.subjects = new ArrayList<>();
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }
}

