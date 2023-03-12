package com.example.lifesworkiguess;

import java.util.ArrayList;

public class MyConstants {



    //for Downloading Images
    public static final int SELECT_PICTURE = 1;

    //for Taking Pictures
    public static final int  CAMERA_PERM_CODE = 101;
    public static final int  CAMERA_REQUEST_CODE = 102;

    //for Users
    public static final int NOT_FINISHED_SETUP = 0;
    public static final int FINISHED_SETUP = 1;

    public static final int NOT_FINISHED_LESSON = 0;
    public static final int FINISHED_LESSON = 1;

    public static final int NOT_FINISHED_COURSE = 0;
    public static final int FINISHED_COURSE = 1;

    public static final String NOT_YET_RATED = "0";
    public static final int COURSE_NAME_POSITION_IN_LESSONS_RATINGS = 0;

    public static final String PROFILE_PICTURE = "Profile Picture";

    //for Course Select

    public static final int EDIT_PFP_SCREEN_SAVE_MODE = 1;
    public static final int EDIT_PFP_SCREEN_SAVE_AND_CHANGE_COURSE_MODE = 2;

    public static final String CHOOSE_COURSE_ORIGIN = "Previous Activity";
    public static final int NO_PREVIOUS_ACTIVITY_ERROR = 0;
    public static final int FROM_MAIN_ACTIVITY = 1;
    public static final int FROM_PROFILE = 2;


    public static final String COMPLETELY_NEW = "Completely New";
    public static final String BEGINNER_COURSE = "Beginner";

    public static final String MODERATELY_EXPERIENCED = "Moderately Experienced";
    public static final String INTERMEDIATE_COURSE = "Intermediate";

    public static final String VERY_EXPERIENCED = "Very Experienced";
    public static final String EXPERT_COURSE = "Expert";

    public static final String[] COOKING_STYLES = new String[]{"Israeli", "Moroccan"};
    public static final String[] EXPERIENCE_LEVELS = new String[]{COMPLETELY_NEW, MODERATELY_EXPERIENCED,VERY_EXPERIENCED};
    public static final String[] WEEKLY_HOURS = new String[]{"1 Hour / a Week", "2 Hours / a Week","4 Hours / a Week"};


    //for CompletedCoursed arrayList
    public static final String COMPLETED_COURSES_PLACEHOLDER = "PLACEHOLDER";
    public static final int COMPLETED_COURSES_PLACEHOLDER_INDEX = 0;

    //for Intro Screen of PermanentLesson
    public static final int NO_LESSON_POSITION = -999;
    public static final String[] LESSON_POSITIONS = new String[]{"First", "Second", "Third", "Fourth","Fifth"};


    //for Actual LessonScreen
    public static final String CURRENTLY_LEARNED_RECIPE = "Current Recipe.xml";


    //Community Dishes Options
    public static final String[] dishCatagoryNames = new String[]{"Pasta", "Chicken", "Sandwich", "Fish", "Salad", "Schnitzel", "Omelet", "Dessert"};
    public static final String[] dishCatagoryLogoNames = new String[]{"com_pasta", "com_chicken", "com_sandwich", "com_fish", "com_salad", "com_schnitzel", "com_omelet", "com_dessert"};







}