/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class is for the CommunityLesson Object.
 */

package com.example.lifesworkiguess;

import java.util.ArrayList;

public class Course {

    private ArrayList<PermanentLesson> lessonsList;
    private String courseName;


    public Course(){
        this.lessonsList = null;
        this.courseName = "ERROR";
    }
    public Course(String courseName) {
        this.lessonsList = new ArrayList<>();
        this.courseName = courseName;
    }

    public Course(ArrayList<PermanentLesson> lessonsList, String courseName) {
        this.lessonsList = lessonsList;
        this.courseName = courseName;
    }

    public ArrayList<PermanentLesson> getLessonsList() {
        return lessonsList;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setLessonsList(ArrayList<PermanentLesson> lessonsList) {
        this.lessonsList = lessonsList;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void clearLessonsList(){
        lessonsList.clear();
    }

    public void addLesson(PermanentLesson addedPermanentLesson){
        lessonsList.add(addedPermanentLesson);
    }


    /**
     * this function sorts the Course's PermanentLesson list by their number, from low to high.
     * <p>
     *
     * @param
     *
     *
     * @return	None
     */
    public void sortLessonsListByNumber(){  //By default, FB sorts items by ABC, so this is used to sort lessons by predetermined numbers set by me
        ArrayList<PermanentLesson> sorted =new ArrayList<>();
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
