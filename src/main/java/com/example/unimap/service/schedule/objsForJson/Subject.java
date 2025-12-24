package com.example.unimap.service.schedule.objsForJson;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Subject {
    @SerializedName("დასახელება")
    private String name;
    @SerializedName("გაკვეთილები")
    private ArrayList<Lesson> lessons;

    public Subject(String name) {
        this.name = name;
        lessons = new ArrayList<>();
    }

    public void addLessons(ArrayList<Lesson> lessonsList) {
        lessonsList.forEach(lesson -> lessons.add(lesson));
    }
}
