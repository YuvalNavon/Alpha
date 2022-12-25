package com.example.lifesworkiguess;

public class User {

    private String Username;
    private String Password;
    private String Email;
    private String CookingStyle;
    private String ExperienceLevel;
    private String SelectedCourse;
    private String Hours;
    private int FinishedSetUp;


    public User(){
        Username = "ERROR";
        Password = "ERROR";
        Email = "ERROR";
        CookingStyle = "ERROR";
        ExperienceLevel = "ERROR";
        SelectedCourse = "ERROR";
        Hours = "ERROR";
        FinishedSetUp = -999;
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
}
