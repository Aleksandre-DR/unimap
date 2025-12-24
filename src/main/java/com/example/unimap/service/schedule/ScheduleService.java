package com.example.unimap.service.schedule;

import com.example.unimap.service.schedule.objsForJson.Lesson;
import com.example.unimap.service.schedule.objsForJson.Schedule;
import com.example.unimap.service.schedule.objsForJson.Subject;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import com.example.unimap.exception.*;


@Service
public class ScheduleService {
    private static final Gson gson = new Gson();

    private ScheduleService() {
    }

    public static String getSchedule(String username, String password) {
        UniTsuSite tsuSite = new UniTsuSite(username, password);

        List<WebElement> scheduleHeadValues = tsuSite.getScheduleHeadValues();
        List<WebElement> scheduleBodyRows = tsuSite.getScheduleBodyRows();

        Schedule scheduleObj = scheduleToSubjects(scheduleHeadValues, scheduleBodyRows);

        return gson.toJson(scheduleObj);
    }

    public static Schedule scheduleToSubjects(List<WebElement> headValues, List<WebElement> bodyRows) {
        Schedule schedule = new Schedule();

        // traversing schedule rows. one row represents one subject
        for (WebElement oneRow : bodyRows) {
            List<WebElement> rowValues = oneRow.findElements(By.tagName("td"));

            // first column in a row shows subject's name. any other column represents a lesson.
            // each lesson has its type in schedule's head. if subject has no lesson for
            // a specific type, its corresponding column is an empty string.

            String subjectName = rowValues.get(0).getText();
            Subject subject = new Subject(subjectName);

            for (int i = 1; i < rowValues.size(); i++) {
                String lessonType = headValues.get(i).getText();
                String lessonInfo = rowValues.get(i).getText();
                if (!lessonInfo.isEmpty()) {            // if lesson is present
                    ArrayList<Lesson> lessons = createLessons(lessonType, lessonInfo);
                    subject.addLessons(lessons);
                }
            }
            schedule.addSubject(subject);
        }
        return schedule;
    }

    private static ArrayList<Lesson> createLessons(String lessonType, String lessonInfo) {
        ArrayList<Lesson> lessons = new ArrayList<>();

        // checking if there are more than one lesson for a specific type
        if (lessonInfo.contains("\nპედაგოგ(ებ)ი")) {
            int indexOfSecondLesson = lessonInfo.lastIndexOf("\nპედაგოგ(ებ)ი");
            String firstLessonInfo = lessonInfo.substring(0, indexOfSecondLesson);
            String secondLessonInfo = lessonInfo.substring(indexOfSecondLesson + 1);

            lessons.add(createLesson(lessonType, firstLessonInfo));
            lessons.add(createLesson(lessonType, secondLessonInfo));
        } else {           // one lesson case
            lessons.add(createLesson(lessonType, lessonInfo));
        }

        return lessons;
    }

    private static Lesson createLesson(String lessonType, String lessonInfo) {
        String lecturer, auditorium, day, time;     // parts of lesson's whole info

        lecturer = lessonInfo.substring(14, lessonInfo.indexOf("\nაუდიტორია"));
        auditorium = lessonInfo.substring(lessonInfo.indexOf("სართული") + 8, lessonInfo.indexOf("\nთარიღი"));
        day = lessonInfo.substring(lessonInfo.indexOf("დრო") + 5, lessonInfo.lastIndexOf(", "));
        time = lessonInfo.substring(lessonInfo.lastIndexOf(", ") + 2);

        return new Lesson(lessonType, lecturer, auditorium, day, time);
    }
}
