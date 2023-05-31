/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class is used to define all of the Constants of the app.
 */

package com.example.lifesworkiguess;

import android.Manifest;

public class MyConstants {

    //For Keeping Log In Info
    public static final String LOGIN_EMAIL = "Email";
    public static final String LOGIN_PASSWORD = "Password";

    //Error Messages for User Info Input

    public static final String USED_EMAIL_ERROR_MESSAGE = "Email Address Already Used";
    public static final String INVALID_FORMAT_EMAIL_ERROR_MESSAGE = "Please Enter a Valid Email Address";
    public static final String PASSWORD_ERROR_MESSAGE = "Password must be Longer than 6 characters";
    public static final String USERNAME_ERROR_MESSAGE = "Username is Taken";

    //For Steps Without Images
    public static final String NO_IMAGE_FOR_STEP = "No Image/Action";

    //for Getting Images from Gallery
    public static final int SELECT_PICTURE = 1;
    public static final int REQUEST_CODE_OPEN_DOCUMENT = 2;
    public static final String IMAGE_FILE_NAME = "selected_image.jpg";
    public static final String NO_IMAGE_FILE_NAME = "no image.jpg";

    //for Taking Pictures With Camera
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


    //for CompletedCourses arrayList
    public static final String COMPLETED_COURSES_PLACEHOLDER = "PLACEHOLDER";
    public static final int COMPLETED_COURSES_PLACEHOLDER_INDEX = 0;

    //For Intro Screen General
    public static final String LESSON_INTRO_MODE_KEY = "Lesson Intro Key"; //THIS IS USED FOR ALL LESSON SCREENS!!
    public static final int LESSON_INTRO_MODE_ERROR = 900;
    public static final int PERMENANT_LESSON_INTRO = 1;
    public static final int COMMUNITY_LESSON_INTRO = 2;
    public static final String LESSON_NAME_KEY = "Lesson Name";
    public static final String LESSON_TIME_KEY = "Lesson Time";
    public static final String LESSON_DIFFICULTY_KEY = "Lesson Difficulty";
    public static final String LESSON_KOSHER_KEY = "Lesson Kosher";
    public static final String LESSON_SERVE_COUNT_KEY = "Lesson Serve Count";
    public static final int NO_SERVE_COUNT_ERROR = 999;


    //for Intro Screen of PermanentLesson
    public static final int NO_LESSON_POSITION = -999;
    public static final String[] LESSON_POSITIONS = new String[]{"First", "Second", "Third", "Fourth","Fifth"};
    public static final String PERMENANT_LESSON_RECIPE_TITLE_KEY = "Lesson Recipe Title";
    public static final String PERMENANT_LESSON_RECIPE_IMAGE_URI_KEY = "Lesson Recipe Image Uri";

    //for Intro Screen of CommunityLesson
    public static final String LESSON_CREATOR_ID_KEY = "Creator ID";
    public static final String LESSON_CREATOR_USERNAME_KEY = "Creator Username";
    public static final String COMMUNITY_LESSON_DESCRIPTION_KEY = "Lesson Description";
    public static final String COMMUNITY_LESSON_NUMBER_KEY = "Lesson Number";
    public static final String COMMUNITY_LESSON_RATINGS_KEY = "Lesson Ratings";
    public static final int NO_COMMUNITY_LESSON_NUMBER_ERROR = 999;


    //for Actual LessonScreen
    public static final String CURRENTLY_LEARNED_RECIPE = "Current Recipe.xml";
    public static final String VIEW_STEP_MODE_KEY = "View Step Mode";
    public static final String FROM_CREATING_RECIPE = "From Creating Recipe";
    public static final String FROM_LESSON_INTRO = "From Lesson Intro";

    //For LessonFinished Screen
        //For CommunityLesson
    public static final String NO_RATING_FOR_COMMUNITY_LESSON = "No Rating";
    public static final String NO_REVIEW_FOR_COMMUNITY_LESSON = "No Review";

    //Community Dishes Options
    public static final String[] dishCatagoryNames = new String[]{"Pasta", "Chicken", "Sandwich", "Fish", "Salad", "Schnitzel", "Omelet", "Dessert"};
    public static final String[] dishCatagoryLogoNames = new String[]{"com_pasta", "com_chicken", "com_sandwich", "com_fish", "com_salad", "com_schnitzel", "com_omelet", "com_dessert"};


    //for Create Recipe Screens -> Intents Constants (Divided into screens: General, Image, and so on
    public static final String FROM_FINISH_SCREEN = "From Finish Screen";
    public static final String NOT_FROM_FINISH_SCREEN = "Not From Finish Screen";
    public static final String FROM_PROFILE_AKA_EDIT_MODE = "Edit Mode";

            //General
    public static final String CUSTOM_RECIPE_NAME = "Custom Recipe Name";
    public static final String CUSTOM_RECIPE_DESCRIPTION = "Custom Recipe Description";

            //Image
    public static final String CUSTOM_RECIPE_IMAGE_URI_STRING = "Custom Recipe Uri";

            //Ingredients
    public static final String CUSTOM_RECIPE_INGREDIENTS = "Custom Recipe Ingredients";
    public static final int STRING_LIST_INGREDIENT_NAME_INDEX = 0;
    public static final int STRING_LIST_INGREDIENT_AMOUNT_INDEX = 1;
    public static final int STRING_LIST_INGREDIENT_UNITS_INDEX = 2;

            //Steps
    public static final String CUSTOM_RECIPE_STEPS = "Custom Recipe Steps";
    public static final int STRING_LIST_STEP_NAME_INDEX = 0;
    public static final int STRING_LIST_STEP_DESCRIPTION_INDEX = 1;
    public static final int STRING_LIST_STEP_TIME_INDEX = 2;
    public static final int STRING_LIST_STEP_ACTION_INDEX = 3;
    public static final String CUSTOM_RECIPE_STEPS_VIEW_MODE = "Steps View Mode";
    public static final String CUSTOM_RECIPE_VIEW_STEPS_FINISH = "Finish";
    public static final String CUSTOM_RECIPE_VIEW_STEPS_DURING_MAKING = "Making";
    public static final String STEP_NUMBER_KEY = "Step Number";
    public static final String STEP_NAME_KEY = "Step Name";
    public static final String STEP_DESCRIPTION_KEY = "Step Description";
    public static final String STEP_TIME_KEY = "Step Time";



    //Extra Info
    public static final String CUSTOM_RECIPE_HOURS_SPINNER_CURR_POS = "Custom Recipe Hours Spinner Curr Pos";
    public static final String CUSTOM_RECIPE_MINUTES_SPINNER_CURR_POS = "Custom Recipe Minutes Spinner Curr Pos";
    public static final int CUSTOM_RECIPE_NO_SPINNER_POS_SAVED = -999;
    public static final String CUSTOM_RECIPE_TIME = "Custom Recipe Time";
    public static final String CUSTOM_RECIPE_DIFFICULTY_LEVEL = "Custom Recipe Difficulty Level";
    public static final String CUSTOM_RECIPE_SERVE_COUNT = "Custom Recipe Serve Count";
    public static final String CUSTOM_RECIPE_KOSHER = "Custom Recipe Kosher";

    public static final String[] CUSTOM_RECIPE_TIME_HOUR_OPTIONS_FOR_SPINNER = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
    "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    public static final String[] CUSTOM_RECIPE_TIME_MINUTE_OPTIONS_FOR_SPINNER = new String[]{"0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60" };
    public static final String CUSTOM_RECIPE_NO_TIME_INPUTTED = "Recipe has no Time";

    public static final String CUSTOM_RECIPE_DIFFICULTY_EASY = "Easy";
    public static final String CUSTOM_RECIPE_DIFFICULTY_STANDARD = "Standard";
    public static final String CUSTOM_RECIPE_DIFFICULTY_HARD = "Hard";


    // For Saving Recipe as XML on device (in preparation to upload) and File Name for Downloaded Recipes from Storage (Not their name in storage tho, thats the next string)
    public static final String DOWNLOADED_RECIPE_NAME = "Downloaded Recipe.xml"; //This goes after the getFilesDir.getPath

    //For Accessing Recipe from Storage
    public static final String RECIPE_STORAGE_NAME = "Recipe";
    public static final String RECIPE_IMAGE_STORAGE_NAME = "Recipe Image.jpg";

    //For Editing CommunityLessons
    public static final String ORIGINAL_CUSTOM_RECIPE_NUMBER = "Original Custom Recipe Number";
    public static final String ORIGINAL_CUSTOM_RECIPE_NAME = "Original Custom Recipe Name";
    public static final String ORIGINAL_CUSTOM_RECIPE_DESCRIPTION = "Original Custom Recipe Description";
    public static final String ORIGINAL_CUSTOM_RECIPE_INGREDIENTS = "Original Custom Recipe Ingredients";
    public static final String ORIGINAL_CUSTOM_RECIPE_STEPS = "Original Custom Recipe Steps";
    public static final String ORIGINAL_CUSTOM_RECIPE_TIME = "Original Custom Recipe Time";
    public static final String ORIGINAL_CUSTOM_RECIPE_DIFFICULTY_LEVEL = "Original Custom Recipe Difficulty Level";
    public static final String ORIGINAL_CUSTOM_RECIPE_SERVE_COUNT = "Original Custom Recipe Serve Count";
    public static final String ORIGINAL_CUSTOM_RECIPE_KOSHER = "Original Custom Recipe Kosher";

    public static final int NOT_EDITING_RECIPE = 0;
    public static final int EDITING_RECIPE = 1;



}