package com.example.lifesworkiguess;

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
    private ArrayList<ArrayList<String>> LessonsRating;
    private ArrayList<String> CompletedCourses;
    private ArrayList<String> UploadedRecipeNames;
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
        LessonsRating.add(new ArrayList<>());
        LessonsRating.get(0).add(SelectedCourse); //THE RATINGS THEMSELVES ARE ADDED IN THE USERNAMESCREEN,
        // AND WHEN A NEW COURSE IS SELECTED, THE COURSE NAME AND THE RATINGS ARE ADDED THERE
        CompletedCourses = new ArrayList<>();
        CompletedCourses.add(MyConstants.COMPLETED_COURSES_PLACEHOLDER); //An arrayList has to have some value in it in order for FB to upload the property so this gets uploaded and then is reset when user
        //completes their first course (see homescreen)
        FinishedCourse = MyConstants.NOT_FINISHED_COURSE;
        UploadedRecipeNames = new ArrayList<>();


    }

    public User(String username, String password, String email, String cookingStyle, String experienceLevel, String selectedCourse, String hours, int finishedSetUp,
                ArrayList<Integer> lessonsStatus, ArrayList<ArrayList<String>> lessonsRating, ArrayList<String> completedCourses,
                int finishedCourse) {
        Username = username;
        Password = password;
        Email = email;
        CookingStyle = cookingStyle;
        ExperienceLevel = experienceLevel;
        SelectedCourse = selectedCourse;
        Hours = hours;
        FinishedSetUp = finishedSetUp;
        LessonsStatus = lessonsStatus;
        LessonsRating = lessonsRating;
        CompletedCourses = completedCourses;
        UploadedRecipeNames = new ArrayList<>();
        FinishedCourse = finishedCourse;
    }

    public User(String username, String password, String email, String cookingStyle, String experienceLevel, String selectedCourse, String hours, int finishedSetUp,
                ArrayList<Integer> lessonsStatus, ArrayList<ArrayList<String>> lessonsRating, ArrayList<String> completedCourses,
                ArrayList<String> uploadedRecipeNames, int finishedCourse) {
        Username = username;
        Password = password;
        Email = email;
        CookingStyle = cookingStyle;
        ExperienceLevel = experienceLevel;
        SelectedCourse = selectedCourse;
        Hours = hours;
        FinishedSetUp = finishedSetUp;
        LessonsStatus = lessonsStatus;
        LessonsRating = lessonsRating;
        CompletedCourses = completedCourses;
        this.UploadedRecipeNames = uploadedRecipeNames;
        FinishedCourse = finishedCourse;
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

    public ArrayList<ArrayList<String>> getLessonsRating() {
        return LessonsRating;
    }

    public ArrayList<String> getCompletedCourses() {
        return CompletedCourses;
    }

    public ArrayList<String> getUploadedRecipeNames() {
        return UploadedRecipeNames;
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


    public void setLessonsRating(ArrayList<ArrayList<String>> lessonsRating) {
        LessonsRating = lessonsRating;
    }

    public void setCompletedCourses(ArrayList<String> completedCourses) {
        CompletedCourses = completedCourses;
    }

    public void setUploadedRecipeNames(ArrayList<String> uploadedRecipeNames) {
        this.UploadedRecipeNames = uploadedRecipeNames;
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


    public void rateLesson(String courseName, int lessonNumber, String rating){ //Only used for lessons as they are finished.
        // once as a default value when the user finishes the  LessonScreenFrag (MyConstants.NOT_YET_RATED)
        // and another  when the user rates the lesson at the LessonFinished activity

        for (int i = 0; i<this.getLessonsRating().size(); i++){
            ArrayList<String> lessonsRating = this.getLessonsRating().get(i);
            if (lessonsRating.get(MyConstants.COURSE_NAME_POSITION_IN_LESSONS_RATINGS).equals(courseName))
            {
                System.out.println("COURSE: " + lessonsRating.get(MyConstants.COURSE_NAME_POSITION_IN_LESSONS_RATINGS));
                int coursePosition = i;
                LessonsRating.get(coursePosition).set(lessonNumber+1, rating); //lessonPosition + 1 Because index 0 is reserved for the course name
                // and lesson positions start from 0 too


            }

        }
    }

    public float getRatingForHistory(String courseName, int positionofLesson){
        for (int i = 0; i<this.getLessonsRating().size(); i++){
            ArrayList<String> lessonsRating = this.getLessonsRating().get(i);
            if (lessonsRating.get(MyConstants.COURSE_NAME_POSITION_IN_LESSONS_RATINGS).equals(courseName))
            {
               return Float.parseFloat(this.getLessonsRating().get(i).get(positionofLesson+1)); //we add 1 to positionOfLesson bc the positionOfLesson
                // is its position in the lessonsStatus list/picked course LessonsList
                //those lists contain just the lessons, unlike the lists in lessonRating, which all start with the Course Name and only then have the the lessons themselves

            }

        }
        return  Float.parseFloat(MyConstants.NOT_YET_RATED); //this return line will never execute bc the for loop always finds the lesson but just in case
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

    public void addCustomRecipe(String recipeName){
        if (UploadedRecipeNames== null)  UploadedRecipeNames = new ArrayList<>(); //Supposedly this condition should never be met as we initialize the list for every constructor
        //except the error one
        UploadedRecipeNames.add(recipeName);
    }

    //INITIALIZATION OF LESSON NUMBERS AND STATUS (LessonsStatus) AND RATINGS (LessonRatings) ONLY OCCURS IN UsernameScreen due to firebase reasons

}
