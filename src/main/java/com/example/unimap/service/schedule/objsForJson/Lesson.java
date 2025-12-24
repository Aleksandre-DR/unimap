package com.example.unimap.service.schedule.objsForJson;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Lesson {
    @SerializedName("ტიპი")
    private String lessonType;
    @SerializedName("ლექტორი")
    private String lecturer;
    @SerializedName("აუდიტორია")
    private String auditorium;
    @SerializedName("დღე")
    private String day;
    @SerializedName("დრო")
    private String time;
}
