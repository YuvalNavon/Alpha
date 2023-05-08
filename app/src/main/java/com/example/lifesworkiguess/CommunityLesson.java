package com.example.lifesworkiguess;

import java.util.ArrayList;
import java.util.HashMap;

public class CommunityLesson  extends  Lesson {

    private ArrayList<ArrayList<String>> ratings;
    private String userID;
    private String Description;
    private ArrayList<String> completedUsersList;
    private boolean active;
    private int number;

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
        //For this, lessonName doesnt really exist so its just recipeName
        //no need to save imageUri bc its saved in the recipe's folder in storage under a constant name.

        super(lessonName, lessonRecipeName, serveCount, time, difficulty, kosher);
        this.ratings = new ArrayList<>();
        this.userID = userID;
        this.Description = description;
        this.completedUsersList = new ArrayList<>();
        this.active = true;
    }

    public CommunityLesson(String lessonName, String lessonRecipeName, String logoUri, int serveCount, String time,
                           String difficulty, boolean kosher, String userID, String description) {

        //For this, lessonName doesnt really exist so its just recipeName
        //THIS ONE SAVES IMAGE URI, BUT
        // currently theres NO NEED to save imageUri bc its saved in the recipe's folder in storage under a constant name.
        //also, the inputted image uri is a bit problematic and you can check why
        super(lessonName, lessonRecipeName, logoUri, serveCount, time, difficulty, kosher);
        this.ratings = new ArrayList<>();
        this.userID = userID;
        Description = description;
    }

    public CommunityLesson(String lessonName, String lessonRecipeName, int serveCount, String time, String difficulty, boolean kosher,
                           ArrayList<ArrayList<String>> ratings, String userID, String description) {
        super(lessonName, lessonRecipeName, serveCount, time, difficulty, kosher);
        this.ratings = ratings;
        this.userID = userID;
        Description = description;
    }


    public CommunityLesson(String lessonName, String lessonRecipeName, int serveCount, String time, String difficulty, boolean kosher,
                           ArrayList<ArrayList<String>> ratings, String userID, String description, ArrayList<String> completedUsersList, int number, boolean active) {

        //For getting a lesson from the database
        super(lessonName, lessonRecipeName, serveCount, time, difficulty, kosher);
        this.ratings = ratings;
        this.userID = userID;
        Description = description;
        this.completedUsersList = completedUsersList;
        this.number = number;
        this.active = active;
    }

    public ArrayList<ArrayList<String>> getRatings() {
        return ratings;
    }

    public String getUserID() {
        return userID;
    }

    public String getDescription() {
        return Description;
    }

    public ArrayList<String> getCompletedUsersList() {
        return completedUsersList;
    }

    public int getNumber() {
        return number;
    }

    public boolean isActive() {
        return active;
    }

    public void setRatings(ArrayList<ArrayList<String>> ratings) {
        this.ratings = ratings;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setCompletedUsersList(ArrayList<String> completedUsersList) {
        this.completedUsersList = completedUsersList;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addUserWhoCompleted(String userID){
        if (completedUsersList==null) completedUsersList = new ArrayList<>();

        boolean userCompletedBefore = false;
        for (String checkedUserID : completedUsersList)
        {
            if (checkedUserID.equals(userID)) userCompletedBefore=true;
        }
        if (!userCompletedBefore)         completedUsersList.add(userID);

    }

    public void addReview (ArrayList<String> reviewList){

        //REVIEW LIST FORMAT: [USER ID, RATING, REVIEW ]

        //IF IM NOT MISTAKEN - THIS METHOD COULD BE USED TO UPDATE REVIEWS BY USERS WHO ALREADY REVIEWED THE LESSON;
        if (ratings==null) ratings = new ArrayList<>();

        boolean userReviewedBefore = false;
        int pos = 0;

        for (ArrayList<String> checkedReviewList: ratings)
        {
            if (checkedReviewList.get(0).equals(reviewList.get(0)))
            {
                userReviewedBefore = true;
                ratings.set(pos, reviewList);
            }
            pos+=1;
        }
        ratings.add(reviewList);
    }
}
