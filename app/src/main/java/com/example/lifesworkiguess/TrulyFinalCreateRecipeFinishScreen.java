package com.example.lifesworkiguess;

import android.content.Intent;
import android.os.Bundle;

import com.example.lifesworkiguess.databinding.ActivityTrulyFinalCreateRecipeStepsTabbedBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.lifesworkiguess.databinding.ActivityTrulyFinalCreateRecipeFinishScreenBinding;

import java.util.ArrayList;

public class TrulyFinalCreateRecipeFinishScreen extends AppCompatActivity {

    private ActivityTrulyFinalCreateRecipeFinishScreenBinding binding;

    //From General
    String recipeName, recipeDescription;

    //From Image - Nothing

    //From Ingredients
    String jsonOfIngredients;

    //From Steps
    String jsonOfSteps;

    //From ExtraInfo
    String recipeTime, recipeDifficultyLevel;
    int recipeServeCount;
    boolean recipeKosher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent gi = getIntent();

        //We check if the user got to this activity from the finish screen or from the activity before this one

        if (gi.getStringExtra("Previous Activity").equals(MyConstants.FROM_FINISH_SCREEN)){
            //Right now I do not allow users to edit by pressing on items from the finish screen, so  this will remain empty for now
        }

        else if (gi.getStringExtra("Previous Activity").equals(MyConstants.NOT_FROM_FINISH_SCREEN)){

            //From General
            recipeName =  gi.getStringExtra(MyConstants.CUSTOM_RECIPE_NAME);
            recipeDescription = gi.getStringExtra(MyConstants.CUSTOM_RECIPE_DESCRIPTION);

            //From Image - Nothing, image is saved in files

            //From Ingredients
            jsonOfIngredients = gi.getStringExtra(MyConstants.CUSTOM_RECIPE_INGREDIENTS);

            //From Steps
            jsonOfSteps = gi.getStringExtra(MyConstants.CUSTOM_RECIPE_STEPS);

            //From ExtraInfo
            recipeTime = gi.getStringExtra(MyConstants.CUSTOM_RECIPE_TIME);
            recipeDifficultyLevel = gi.getStringExtra(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL);
            recipeServeCount = gi.getIntExtra(MyConstants.CUSTOM_RECIPE_SERVE_COUNT, 0);
            recipeKosher = gi.getBooleanExtra(MyConstants.CUSTOM_RECIPE_KOSHER, false);

        }


        binding = ActivityTrulyFinalCreateRecipeFinishScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Overview
        TrulyFinalCreateRecipeOverviewFrag overviewFrag = new TrulyFinalCreateRecipeOverviewFrag();

        Bundle overviewBundle = new Bundle();
        overviewBundle.putString(MyConstants.CUSTOM_RECIPE_NAME, recipeName);
        overviewBundle.putString(MyConstants.CUSTOM_RECIPE_DESCRIPTION, recipeDescription);
        overviewBundle.putString(MyConstants.CUSTOM_RECIPE_TIME, recipeTime);
        overviewBundle.putString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL, recipeDifficultyLevel);
        overviewBundle.putBoolean(MyConstants.CUSTOM_RECIPE_KOSHER, recipeKosher);
        overviewBundle.putInt(MyConstants.CUSTOM_RECIPE_SERVE_COUNT, recipeServeCount);
        overviewBundle.putString(MyConstants.CUSTOM_RECIPE_INGREDIENTS, jsonOfIngredients);
        overviewBundle.putString(MyConstants.CUSTOM_RECIPE_STEPS, jsonOfSteps);

        overviewFrag.setArguments(overviewBundle);

        //Ingredients
        TrulyFinalCreateRecipeViewIngredientsFrag viewIngredientsFrag = new TrulyFinalCreateRecipeViewIngredientsFrag();

        Bundle ingredientsBundle = new Bundle();
        ingredientsBundle.putString(MyConstants.CUSTOM_RECIPE_INGREDIENTS, jsonOfIngredients);

        viewIngredientsFrag.setArguments(ingredientsBundle);


        //Steps
        TrulyFinalCreateRecipeViewStepsFrag viewStepsFrag = new TrulyFinalCreateRecipeViewStepsFrag();

        Bundle stepsBundle = new Bundle();
        stepsBundle.putString(MyConstants.CUSTOM_RECIPE_STEPS_VIEW_MODE, MyConstants.CUSTOM_RECIPE_VIEW_STEPS_FINISH);
        stepsBundle.putString(MyConstants.CUSTOM_RECIPE_STEPS, jsonOfSteps);

        viewStepsFrag.setArguments(stepsBundle);

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(overviewFrag);
        fragmentList.add(viewIngredientsFrag);
        fragmentList.add(viewStepsFrag);

        ArrayList<String> fragmentTitleList = new ArrayList<>();
        fragmentTitleList.add("Overview");
        fragmentTitleList.add("Ingredients");
        fragmentTitleList.add("Steps");



        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, fragmentTitleList);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
    }
}