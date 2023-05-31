/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Activity is where the user can see the details and Ingredients of the Lesson they're
 * about to start.
 * if the Lesson is a CommunityLesson, the user can also view the Lesson's Steps and Reviews.
 */
package com.example.lifesworkiguess;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lifesworkiguess.databinding.ActivityNewLessonIntroBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class NewLessonIntro extends AppCompatActivity {

    private ActivityNewLessonIntroBinding binding;

    //For Overview
        //For PermanentLessonSetup
        String courseName, recipeImageURI;
        int lessonPosition;
        boolean lessonFinal;

        //For CommunityLessonSetup
        CommunityLesson selectedCommunityLesson;
        String creatorUsername, creatorID, lessonDescription;
        int lessonNumber;

        //For Both
        Intent getLessonData;
        String lessonName;
        Bundle overviewBundle, ingredientsBundle, stepsBundle, ratingsBundle;
        FirebaseDatabase FBDB;
        DatabaseReference refLessons;
        ValueEventListener lessonGetter;
        Recipe recipe;
        //ALMOST All of the properties that both lessons share (name, time, difficulty, etc.) are defined in their setups
        //the properties declarations are a bit inconsistent but still

    //For Ingredients

    //For Steps


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");

        overviewBundle = new Bundle();
        ingredientsBundle = new Bundle();
        stepsBundle = new Bundle();

        getLessonData = getIntent();
        int fromHomeOrCommunity = getLessonData.getIntExtra(MyConstants.LESSON_INTRO_MODE_KEY, MyConstants.LESSON_INTRO_MODE_ERROR);
        overviewBundle.putInt(MyConstants.LESSON_INTRO_MODE_KEY, fromHomeOrCommunity);

        if (fromHomeOrCommunity==MyConstants.PERMENANT_LESSON_INTRO){
            permanentLessonSetup();
        }

        else if (fromHomeOrCommunity==MyConstants.COMMUNITY_LESSON_INTRO)
        {
            communityLessonSetup();
        }



    }

    @Override
    protected void onPause() {

        super.onPause();
        if (refLessons!=null && lessonGetter !=null) refLessons.removeEventListener(lessonGetter);


    }


    @Override
    protected void onResume() {

        super.onResume();
        if (refLessons!=null && lessonGetter !=null) refLessons.addListenerForSingleValueEvent(lessonGetter);

    }

    public void onDestroy() {

        super.onDestroy();
        if (refLessons!=null && lessonGetter !=null) refLessons.removeEventListener(lessonGetter);


    }

    /**
     * this function sets the Fragments for the ViewPagerAdapter and sets the screen as visible
     *
     * @param

     *
     *
     * @return
     */
    public void setUpScreen(){

        //Used right after recipe is downloaded

        binding = ActivityNewLessonIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Overview
        NewLessonIntroOverviewFrag overviewFrag = new NewLessonIntroOverviewFrag();
        overviewFrag.setArguments(overviewBundle);

        //Ingredients
        IngredientsListFrag viewIngredientsFrag = new IngredientsListFrag();

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(overviewFrag);
        fragmentList.add(viewIngredientsFrag);
        ArrayList<String> fragmentTitleList = new ArrayList<>();
        fragmentTitleList.add("Overview");
        fragmentTitleList.add("Ingredients");

        int fromHomeOrCommunity = getLessonData.getIntExtra(MyConstants.LESSON_INTRO_MODE_KEY, MyConstants.LESSON_INTRO_MODE_ERROR);
        if (fromHomeOrCommunity==MyConstants.COMMUNITY_LESSON_INTRO) //I dont want the steps to be seen for permanent lessons
        {
            //Steps
            StepsListFrag viewStepsFrag = new StepsListFrag();
            viewStepsFrag.setArguments(stepsBundle);
            fragmentList.add(viewStepsFrag);
            fragmentTitleList.add("Steps");

            ViewRatings viewRatingsFrag = new ViewRatings();
            viewRatingsFrag.setArguments(ratingsBundle);
            fragmentList.add(viewRatingsFrag);
            fragmentTitleList.add("Reviews");


        }


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, fragmentTitleList);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
    }

    /**
     * this function gets the details for the picked PermanentLesson from the Intent
     * and from Firebase Database,packs them in a bundle
     * and calls the setUpIngredients() and setUpScreen() methods.
     *
     * @param
     *
     *
     *
     * @return
     */
    public void permanentLessonSetup(){
        lessonName = getLessonData.getStringExtra("Lesson Name");
        courseName = getLessonData.getStringExtra("Course Name");
        lessonPosition = getLessonData.getIntExtra("Lesson Position - 1", MyConstants.NO_LESSON_POSITION);
        lessonFinal = getLessonData.getBooleanExtra("Is Lesson Final", false);
        recipeImageURI = getLessonData.getStringExtra(MyConstants.PERMENANT_LESSON_RECIPE_IMAGE_URI_KEY);


        overviewBundle.putString("Lesson Name", lessonName);
        overviewBundle.putString("Course Name", courseName);
        overviewBundle.putInt("Lesson Position - 1", lessonPosition);
        overviewBundle.putBoolean("Is Lesson Final", lessonFinal);
        overviewBundle.putString(MyConstants.PERMENANT_LESSON_RECIPE_IMAGE_URI_KEY, recipeImageURI);


        refLessons = FBDB.getReference("Courses").child(courseName);
        lessonGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    PermanentLesson checkedPermanentLesson = data.getValue(PermanentLesson.class);

                    if (lessonName.equals(checkedPermanentLesson.getLessonName())){

                        PermanentLesson selectedPermanentLesson = checkedPermanentLesson;

                        myServices.downloadXML(NewLessonIntro.this, selectedPermanentLesson.getLessonRecipeName(), "Courses/" + courseName + "/" + lessonName);


                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                // Actions to do after 3 second (to let Recipe Download)
                                recipe = myServices.XMLToRecipe(NewLessonIntro.this, MyConstants.DOWNLOADED_RECIPE_NAME);

                                String recipeTitle = recipe.getTitle();
                                String lessonTime = selectedPermanentLesson.getTime();
                                String lessonDifficulty = selectedPermanentLesson.getDifficulty();
                                boolean lessonKosher = selectedPermanentLesson.isKosher();
                                int lessonServeCount = selectedPermanentLesson.getServeCount();

                                overviewBundle.putString(MyConstants.PERMENANT_LESSON_RECIPE_TITLE_KEY, recipeTitle);
                                overviewBundle.putString(MyConstants.LESSON_TIME_KEY, lessonTime);
                                overviewBundle.putString(MyConstants.LESSON_DIFFICULTY_KEY, lessonDifficulty);
                                overviewBundle.putBoolean(MyConstants.LESSON_KOSHER_KEY, lessonKosher);
                                overviewBundle.putInt(MyConstants.LESSON_SERVE_COUNT_KEY, lessonServeCount);

                                setUpIngredients();

                                setUpScreen();
                            }
                        }, 3000);

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refLessons.addListenerForSingleValueEvent(lessonGetter);


    }

    /**
     * this function gets the details for the picked CommunityLesson from the Intent
     * and from Firebase Database,packs them in a bundle
     * and calls the setUpIngredients(), setUpSteps(), setupRatings(), and setUpScreen() methods.
     *
     * @param
     *
     *
     *
     * @return
     */
    public void communityLessonSetup(){
        lessonName = getLessonData.getStringExtra(MyConstants.LESSON_NAME_KEY);
        creatorUsername = getLessonData.getStringExtra(MyConstants.LESSON_CREATOR_USERNAME_KEY);
        creatorID = getLessonData.getStringExtra(MyConstants.LESSON_CREATOR_ID_KEY);
        lessonNumber = getLessonData.getIntExtra(MyConstants.COMMUNITY_LESSON_NUMBER_KEY, MyConstants.NO_COMMUNITY_LESSON_NUMBER_ERROR);

        overviewBundle.putString(MyConstants.LESSON_NAME_KEY, lessonName);
        overviewBundle.putString(MyConstants.LESSON_CREATOR_USERNAME_KEY, creatorUsername);
        overviewBundle.putString(MyConstants.LESSON_CREATOR_ID_KEY, creatorID);
        overviewBundle.putInt(MyConstants.COMMUNITY_LESSON_NUMBER_KEY, lessonNumber);

        refLessons = FBDB.getReference("Community Lessons").child(creatorID + " , " + lessonNumber  );
        lessonGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                selectedCommunityLesson = snapshot.getValue(CommunityLesson.class);

                myServices.downloadXML(NewLessonIntro.this, MyConstants.RECIPE_STORAGE_NAME,
                        "Community Recipes/" + creatorID + "/" + lessonNumber);


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 3 second (to let Recipe Download)
                        recipe = myServices.XMLToRecipe(NewLessonIntro.this, MyConstants.DOWNLOADED_RECIPE_NAME);

                        String lessonTime = selectedCommunityLesson.getTime();
                        String lessonDifficulty = selectedCommunityLesson.getDifficulty();
                        boolean lessonKosher = selectedCommunityLesson.isKosher();
                        int lessonServeCount = selectedCommunityLesson.getServeCount();
                        lessonDescription = getLessonData.getStringExtra(MyConstants.COMMUNITY_LESSON_DESCRIPTION_KEY);


                        overviewBundle.putString(MyConstants.LESSON_TIME_KEY, lessonTime);
                        overviewBundle.putString(MyConstants.LESSON_DIFFICULTY_KEY, lessonDifficulty);
                        overviewBundle.putBoolean(MyConstants.LESSON_KOSHER_KEY, lessonKosher);
                        overviewBundle.putInt(MyConstants.LESSON_SERVE_COUNT_KEY, lessonServeCount);
                        overviewBundle.putString(MyConstants.COMMUNITY_LESSON_DESCRIPTION_KEY, lessonDescription);

                        setUpIngredients();
                        setUpSteps();
                        setUpRatings();

                        setUpScreen();
                    }
                }, 3000);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refLessons.addListenerForSingleValueEvent(lessonGetter);
    }


    /**
     * this function gets the Ingredients list from the recipe of the picked Lesson,
     * and packs it as an ArrayList<String[]> in a bundle .
     *
     * @param
     *
     *
     *
     * @return
     */
    public void setUpIngredients(){

        ArrayList<Ingredient> ingredientsList = recipe.getIngredients();
        ArrayList<String[]> ingredientsInStringLists = new ArrayList<>();

        for (Ingredient ingredient: ingredientsList){
            String[] addedIngredientList = new String[]{ingredient.getName(), ingredient.getAmount(), ingredient.getUnits()};

            ingredientsInStringLists.add(addedIngredientList);
        }

        Gson gson = new Gson();
        String jsonOfIngredients = gson.toJson(ingredientsInStringLists);
        ingredientsBundle.putString(MyConstants.CUSTOM_RECIPE_INGREDIENTS, jsonOfIngredients);

    }


    /**
     * this function gets the Steps list from the recipe of the picked Lesson,
     * and packs it as an ArrayList<String[]> in a bundle.
     *
     * @param
     *
     *
     *
     * @return
     */
    public void setUpSteps(){

        ArrayList<Step> stepsList = recipe.getSteps();
        ArrayList<String[]> stepsInStringLists = new ArrayList<>();

        for (Step step: stepsList){
            //No need for Step Number - the index in the list is the number

            String[] currStepInString = new String[]{step.getName(), step.getDescription(), step.getTime(), step.getAction()};

            stepsInStringLists.add(currStepInString);
        }

        Gson gson = new Gson();
        String jsonOfSteps = gson.toJson(stepsInStringLists);
        stepsBundle.putString(MyConstants.CUSTOM_RECIPE_STEPS_VIEW_MODE, MyConstants.CUSTOM_RECIPE_VIEW_STEPS_FINISH);
        stepsBundle.putString(MyConstants.CUSTOM_RECIPE_STEPS, jsonOfSteps);
        stepsBundle.putBoolean("From Lesson Intro", true);
    }

    /**
     * this function gets the Ratings list from the recipe of the picked Lesson,
     * and packs it as an ArrayList<ArrayList<String>> in a bundle.
     *
     * @param
     *
     *
     *
     * @return
     */
    public void setUpRatings()
    {

        ArrayList<ArrayList<String>> ratings = selectedCommunityLesson.getRatings();
        ratingsBundle = new Bundle();
        Gson gson = new Gson();
        String jsonofRatings = gson.toJson(ratings);
        ratingsBundle.putString(MyConstants.COMMUNITY_LESSON_RATINGS_KEY, jsonofRatings);
        ratingsBundle.putString(MyConstants.LESSON_CREATOR_ID_KEY, creatorID);
        ratingsBundle.putInt(MyConstants.COMMUNITY_LESSON_NUMBER_KEY, lessonNumber);

    }




}