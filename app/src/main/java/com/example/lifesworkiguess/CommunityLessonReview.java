package com.example.lifesworkiguess;

public class CommunityLessonReview
{
    private float rating;
    private String userID;
    private String review;

    public CommunityLessonReview()
    {
        this.rating = Integer.parseInt(MyConstants.NOT_YET_RATED);
        this.userID = "ERROR USERID";
        this.review = "ERROR REVIEW";
    }

    public CommunityLessonReview(float rating, String userID, String review) {
        this.rating = rating;
        this.userID = userID;
        this.review = review;
    }

    public CommunityLessonReview(float rating, String userID) {
        this.rating = rating;
        this.userID = userID;
    }


    public float getRating() {
        return rating;
    }

    public String getUserID() {
        return userID;
    }

    public String getReview() {
        return review;
    }


    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
