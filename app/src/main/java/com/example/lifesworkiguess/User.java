package com.example.lifesworkiguess;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class User {

    private String Username;
    private String Password;
    private String Email;
    private String CookingStyle;
    private String ExperienceLevel;
    private String SelectedCourse;
    private String Hours;
    private int FinishedSetUp;
    private ArrayList<Integer> LessonsStatus;
    private ArrayList<Float> LessonsRating;
    private ArrayList<String> CompletedCourses;
    private int FinishedCourse;



    public User(){
        Username = "ERROR";
        Password = "ERROR";
        Email = "ERROR";
        CookingStyle = "ERROR";
        ExperienceLevel = "ERROR";
        SelectedCourse = "ERROR";
        Hours = "ERROR";
        FinishedSetUp = -999;
        LessonsStatus = null;
    }
    public User(String username, String email, String password, String cookingStyle, String experienceLevel, String hours, int finishedSetUp) {
        Username = username;
        Password = password;
        Email = email;
        CookingStyle = cookingStyle;
        ExperienceLevel = experienceLevel;
        Hours = hours;
        FinishedSetUp = finishedSetUp;
        SelectedCourse = cookingStyle + " " + determineExperienceLevel(experienceLevel) ;
        LessonsStatus = new ArrayList<>();
        LessonsRating = new ArrayList<>();
        CompletedCourses = new ArrayList<>();
        CompletedCourses.add(MyConstants.COMPLETED_COURSES_PLACEHOLDER); //An arrayList has to have some value in it in order for FB to upload the property so this gets uploaded and then is reset when user
        //completes their first course (see homescreen)
        FinishedCourse = MyConstants.NOT_FINISHED_COURSE;


    }



    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getEmail() {
        return Email;
    }

    public String getCookingStyle() {
        return CookingStyle;
    }

    public String getExperienceLevel() {
        return ExperienceLevel;
    }

    public String getHours() {
        return Hours;
    }

    public int getFinishedSetUp() {
        return FinishedSetUp;
    }

    public String getSelectedCourse() {
        return SelectedCourse;
    }

    public ArrayList<Integer> getLessonsStatus() {
        return LessonsStatus;
    }

    public ArrayList<Float> getLessonsRating() {
        return LessonsRating;
    }

    public ArrayList<String> getCompletedCourses() {
        return CompletedCourses;
    }

    public int getFinishedCourse() {
        return FinishedCourse;
    }



    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setSelectedCourse(String selectedCourse) {
        SelectedCourse = selectedCourse;
    }

    public void setCookingStyle(String cookingStyle) {
        CookingStyle = cookingStyle;
    }

    public void setExperienceLevel(String experienceLevel) {
        ExperienceLevel = experienceLevel;
    }

    public void setHours(String hours) {
        Hours = hours;
    }

    public void setFinishedSetUp(int finishedSetUp) {
        FinishedSetUp = finishedSetUp;
    }

    public void setLessonsStatus(ArrayList<Integer> lessonsStatus) {
        LessonsStatus = lessonsStatus;
    }


    public void setLessonsRating(ArrayList<Float> lessonsRating) {
        LessonsRating = lessonsRating;
    }

    public void setCompletedCourses(ArrayList<String> completedCourses) {
        CompletedCourses = completedCourses;
    }

    public void setFinishedCourse(int finishedCourse) {
        FinishedCourse = finishedCourse;
    }



    public String determineExperienceLevel(String experienceLevel){
        if (experienceLevel.equals(MyConstants.COMPLETELY_NEW))
            return MyConstants.BEGINNER_COURSE;
        else if (experienceLevel.equals(MyConstants.MODERATELY_EXPERIENCED))
            return MyConstants.INTERMEDIATE_COURSE;
        else if (experienceLevel.equals(MyConstants.VERY_EXPERIENCED))
            return MyConstants.EXPERT_COURSE;
       else{
           return "ERROR";
        }
    }


    public void rateLesson(int lessonNumber, float rating){
       LessonsRating.set(lessonNumber, rating);
    }

    public void setLessonFinished(int lessonPosition){
        this.LessonsStatus.set(lessonPosition, MyConstants.FINISHED_LESSON);
    }

    public boolean hasFinishedCourse(){
        for (int i = 0; i<LessonsStatus.size();i++){
            if (LessonsStatus.get(i)==MyConstants.NOT_FINISHED_LESSON) return false;
        }
        return true;
    }

    //To be used only after setting new cooking style and/or experience level (use case: choose course screen when changing course)
    public void updateSelectedCourse(){
        SelectedCourse =  CookingStyle + " " + determineExperienceLevel(ExperienceLevel) ;

    }

    //INITIALIZATION OF LESSON NUMBERS AND STATUS (LessonsStatus) AND RATINGS (LessonRatings) ONLY OCCURS IN UsernameScreen due to firebase reasons

}
