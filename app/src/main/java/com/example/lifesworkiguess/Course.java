package com.example.lifesworkiguess;

import java.util.ArrayList;

public class Course {

    private ArrayList<Lesson> lessonsList;
    private String courseName;


    public Course(){
        this.lessonsList = null;
        this.courseName = "ERROR";
    }
    public Course(String courseName) {
        this.lessonsList = new ArrayList<>();
        this.courseName = courseName;
    }

    public void addLesson(Lesson addedLesson){
        lessonsList.add(addedLesson);
    }

    public void clearLessonsList(){
        lessonsList.clear();
    }

    public ArrayList<Lesson> getLessonsList() {
        return lessonsList;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setLessonsList(ArrayList<Lesson> lessonsList) {
        this.lessonsList = lessonsList;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
