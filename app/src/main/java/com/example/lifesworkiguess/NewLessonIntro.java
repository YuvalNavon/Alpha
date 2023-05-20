package com.example.lifesworkiguess;

import static com.example.lifesworkiguess.FBref.FBDB;

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
        String creatorUsername, creatorID, lessonDescription;
        int lessonNumber;

        //For Both
        Intent getLessonData;
        String lessonName;
        Bundle overviewBundle, ingredientsBundle, stepsBundle;
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
            TrulyFinalCreateRecipeViewStepsFrag viewStepsFrag = new TrulyFinalCreateRecipeViewStepsFrag();
            viewStepsFrag.setArguments(stepsBundle);
            fragmentList.add(viewStepsFrag);
            fragmentTitleList.add("Steps");
        }


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, fragmentTitleList);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
    }

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
                                setUpSteps();

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
        refLessons.addValueEventListener(lessonGetter);


    }

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

                CommunityLesson selectedCommunityLesson = snapshot.getValue(CommunityLesson.class);

                myServices.downloadXML(NewLessonIntro.this, MyConstants.RECIPE_STORAGE_NAME,
                        "Community Recipes/" + creatorID + "/" + selectedCommunityLesson.getLessonRecipeName());


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
    }




}