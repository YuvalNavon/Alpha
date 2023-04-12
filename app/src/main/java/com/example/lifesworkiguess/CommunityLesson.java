package com.example.lifesworkiguess;

import java.util.ArrayList;

public class CommunityLesson  extends  Lesson {

    private ArrayList<Float> ratings;
    private String userID;
    private String Description;

    public CommunityLesson(){
        super();
    }

    public CommunityLesson(String lessonName, String lessonRecipeName, String logoUri, String userID) { //no idea what this is for
        super(lessonName, lessonRecipeName, logoUri);
        this.ratings = new ArrayList<>();
        this.userID =userID;

    }


    public CommunityLesson(String lessonName, String lessonRecipeName, int serveCount, String time,
                           String difficulty, boolean kosher, String userID, String description) {
        //For this, lessonName doesnt really exist so its just
        //no need to save imageUri bc its saved in the recipe's folder in storage under a constant name.

        super(lessonName, lessonRecipeName, serveCount, time, difficulty, kosher);
        this.ratings = new ArrayList<>();
        this.userID = userID;
        this.Description = description;
    }

    public CommunityLesson(String lessonName, String lessonRecipeName, int serveCount, String time, String difficulty, boolean kosher,
                           ArrayList<Float> ratings, String userID, String description) {
        super(lessonName, lessonRecipeName, serveCount, time, difficulty, kosher);
        this.ratings = ratings;
        this.userID = userID;
        Description = description;
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
