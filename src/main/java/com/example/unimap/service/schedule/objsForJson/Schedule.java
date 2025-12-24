package com.example.unimap.service.schedule.objsForJson;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Schedule {
    @SerializedName("საგნები")
    private ArrayList<Subject> subjects;

    public Schedule() {
        this.subjects = new ArrayList<>();
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }
}

