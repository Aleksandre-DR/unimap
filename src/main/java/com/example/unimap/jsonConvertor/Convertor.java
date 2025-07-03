package com.example.unimap.jsonConvertor;

import com.example.unimap.jsonConvertor.fromJson.SubjectsFromJson;
import com.example.unimap.jsonConvertor.toJson.SubjectsFormater;
import com.example.unimap.jsonConvertor.toJson.Subjects;
import com.google.gson.Gson;


public class Convertor {
    private static Gson gson = new Gson();

    public static String newSchedule(String oldSchedule) {
        SubjectsFromJson subsFromJson = gson.fromJson(oldSchedule, SubjectsFromJson.class);
        Subjects formatedSubjects = SubjectsFormater.formate(subsFromJson);
        return gson.toJson(formatedSubjects);
    }
}
