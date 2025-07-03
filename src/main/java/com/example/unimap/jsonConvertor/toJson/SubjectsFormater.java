package com.example.unimap.jsonConvertor.toJson;

import com.example.unimap.jsonConvertor.fromJson.SubjectFromJson;
import com.example.unimap.jsonConvertor.fromJson.SubjectsFromJson;

import java.time.LocalDate;

import static com.example.unimap.service.Translator.weekdaysFromEngToGeo;

public class SubjectsFormater {
    private static Subjects subjects;

    private static String subjectName;
    private static String lecture;
    private static String workerSquad;
    private static String lab;

    private static String todayInEng;
    private static String todayInGeo;

    private static String lecturer, auditorium, day, time;

    public static Subjects formate(SubjectsFromJson subsFromJson) {
        subjects = new Subjects();
        todayInEng = LocalDate.now().getDayOfWeek().toString();
        todayInGeo = weekdaysFromEngToGeo.get(todayInEng);

        for (SubjectFromJson sub : subsFromJson.getSubjects()) {
            retrieveSubjectInfo(sub);
            Subject subject = new Subject(subjectName);
            subject.addLesson(createLesson("ლექცია", lecture));
            subject.addLesson(createLesson("სამუშაო ჯგუფი", workerSquad));
            subject.addLesson(createLesson("ლაბორატორიული", lab));
            if (!subject.isLessonsEmpty()) {
                subjects.addSubject(subject);
            }
        }

        return subjects;
    }

    private static void retrieveSubjectInfo(SubjectFromJson sub) {
        subjectName = sub.getSubjectName();
        lecture = sub.getLecture();
        workerSquad = sub.getWorkerSquad();
        lab = sub.getLab();
    }

    private static Lesson createLesson(String lessonType, String lessonAsStr) {
        if (lessonAsStr.isEmpty()) {
            return null;
        }

        lecturer = lessonAsStr.substring(14, lessonAsStr.indexOf("\nაუდიტორია"));
        auditorium = lessonAsStr.substring(lessonAsStr.indexOf("სართული") + 8, lessonAsStr.indexOf("\nთარიღი"));
        day = lessonAsStr.substring(lessonAsStr.indexOf("დრო") + 5, lessonAsStr.lastIndexOf(", "));
        time = lessonAsStr.substring(lessonAsStr.lastIndexOf(", ") + 2);

        if (!day.equals(todayInGeo)) {
            return null;
        }
        return new Lesson(lessonType, lecturer, auditorium, time);
    }
}

