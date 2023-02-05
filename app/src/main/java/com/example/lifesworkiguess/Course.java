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

    public Course(ArrayList<Lesson> lessonsList, String courseName) {
        this.lessonsList = lessonsList;
        this.courseName = courseName;
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

    public void clearLessonsList(){
        lessonsList.clear();
    }

    public void addLesson(Lesson addedLesson){
        lessonsList.add(addedLesson);
    }

    public void sortLessonsListByNumber(){  //By default, FB sorts items by ABC, so this is used to sort lessons by predetermined numbers set by me
        ArrayList<Lesson> sorted =new ArrayList<>();
        int searchedIndex = 0;
        while(sorted.size()!=lessonsList.size()){
            for (int i = 0; i<lessonsList.size();i++ ){
                if (lessonsList.get(i).getNumber()==searchedIndex){
                    sorted.add(lessonsList.get(i));
                    searchedIndex+=1;
                }
            }
        }
        lessonsList = sorted;



    }

}
