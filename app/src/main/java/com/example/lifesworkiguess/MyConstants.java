package com.example.lifesworkiguess;

import java.util.ArrayList;

public class MyConstants {

    //for Downloading Images
    public static final int SELECT_PICTURE = 1;

    //for Users
    public static final int FINISHED_SETUP = 1;
    public static final int NOT_FINISHED_SETUP = 0;

    //for Course Select
    public static final String COMPLETELY_NEW = "Completely New";
    public static final String BEGINNER_COURSE = "Beginner";

    public static final String MODERATELY_EXPERIENCED = "Moderately Experienced";
    public static final String INTERMEDIATE_COURSE = "Intermediate";

    public static final String VERY_EXPERIENCED = "VERY EXPERIENCED";
    public static final String EXPERT_COURSE = "Expert";

    //for Intro Screen of Lesson
    public static final int NO_LESSON_POSITION = -999;
    public static final String[] LESSON_POSITIONS = new String[]{"First", "Second", "Third", "Fourth","Fifth"};


    //for Lesson ExtraInfo Positions
    public static final int EXPECTED_TIME_POSITION = 0;
    public static final int DIFFICULTY_POSITION = 1;
    public static final int KOSHER_POSITION = 2;

    //for Actual LessonScreen
    public static final String CURRENTLY_LEARNED_RECIPE = "Current Recipe.xml";

    // Links for Action Gifs
    public static final String NO_GIF_ERROR = "shorturl.at/fGPSX";
    public static final String SLICE_GIF = "shorturl.at/HJLS8";
    public static final String SEASONING_GIF = "shorturl.at/nuJ49";
    public static final String POURING_BREAD_CRUMBS_GIF = "shorturl.at/uxLOQ";

    //Action GIFS ArrayList
    public static ArrayList<String> GIFS_LINKS_LIST = new ArrayList<>();
    public static ArrayList<String> GIFS_NAMES_LIST = new ArrayList<>();

    //Connector Words ArrayList
    public static final String[] CONNECTOR_WORDS = new String[]{"in", "on", "the", "into"};



    public static void SetGIFS_LISTS(){
        GIFS_LINKS_LIST.add(SLICE_GIF);
        GIFS_NAMES_LIST.add("SLICE");

        GIFS_LINKS_LIST.add(SEASONING_GIF);
        GIFS_NAMES_LIST.add("SEASON");

        GIFS_LINKS_LIST.add(POURING_BREAD_CRUMBS_GIF);
        GIFS_NAMES_LIST.add("PREPARE BREAD CRUMBS");


    }



}