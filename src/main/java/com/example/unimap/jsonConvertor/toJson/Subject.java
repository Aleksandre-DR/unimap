package com.example.unimap.jsonConvertor.toJson;

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

    public void addLesson(Lesson lesson) {
        if (lesson == null) return;
        lessons.add(lesson);
    }

    public boolean isLessonsEmpty() {
        return lessons.isEmpty();
    }
}
