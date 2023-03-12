package com.example.lifesworkiguess;

import java.util.ArrayList;

public class CommunityLesson  extends  Lesson {

    private ArrayList<Float> ratings;
    private String userID;
    private String Description;

    public CommunityLesson(){
        super();
    }

    public CommunityLesson(String lessonName, String lessonRecipeName, String logoUri, String userID) {
        super(lessonName, lessonRecipeName, logoUri);
        this.ratings = new ArrayList<>();
        this.userID =userID;

    }

    public CommunityLesson(String lessonName, String lessonRecipeName, String logoUri, int serveCount, String time, String difficulty, boolean kosher, String userID, String description) {
        super(lessonName, lessonRecipeName, logoUri, serveCount, time, difficulty, kosher);
        this.ratings = new ArrayList<>();
        this.userID = userID;
        this.Description = description;
    }

    public ArrayList<Float> getRatings() {
        return ratings;
    }

    public String getUserID() {
        return userID;
    }

    public String getDescription() {
        return Description;
    }

    public void setRatings(ArrayList<Float> ratings) {
        this.ratings = ratings;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
