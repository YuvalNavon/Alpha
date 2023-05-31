/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Activity is where the user can View all of the details for the CommunityLesson they have written/edited.
 */

package com.example.lifesworkiguess;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.tabs.TabLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lifesworkiguess.databinding.ActivityTrulyFinalCreateRecipeFinishScreenBinding;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CreateRecipeFinishScreen extends AppCompatActivity {

    private ActivityTrulyFinalCreateRecipeFinishScreenBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTrulyFinalCreateRecipeFinishScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Overview
        CreateRecipeOverviewFrag overviewFrag = new CreateRecipeOverviewFrag();

        //Ingredients
        CreateRecipeViewIngredientsFrag viewIngredientsFrag = new CreateRecipeViewIngredientsFrag();

        //Steps
        StepsListFrag viewStepsFrag = new StepsListFrag();

        Intent gi = getIntent();

        //We check if the user got to this activity from the finish screen or from the activity before this one

        if (gi.getStringExtra("Previous Activity").equals(MyConstants.FROM_FINISH_SCREEN)){
            //Right now I do not allow users to edit by pressing on items from the finish screen, so  this will remain empty for now
        }

        else if (gi.getStringExtra("Previous Activity").equals(MyConstants.FROM_PROFILE_AKA_EDIT_MODE)){

            Bundle onFinishBundle = new Bundle();
            onFinishBundle.putBoolean("On Create Recipe Finish Key", true);

            viewStepsFrag.setArguments(onFinishBundle);
            viewIngredientsFrag.setArguments(onFinishBundle);

            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true /* enabled by default */) {
                @Override
                public void handleOnBackPressed() {

                    AlertDialog.Builder exitWithoutSavingDialogBuilder = new AlertDialog.Builder(CreateRecipeFinishScreen.this);

                    exitWithoutSavingDialogBuilder.setTitle("Exit Without Saving?");
                    exitWithoutSavingDialogBuilder.setMessage("If You Exit Without Saving, Your Changes WON'T be Saved.\nAre You Sure You Want to Exit?");

                    exitWithoutSavingDialogBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            SharedPreferences settings= CreateRecipeFinishScreen.this.getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                            SharedPreferences.Editor editor=settings.edit();

                            editor.putInt(MyConstants.ORIGINAL_CUSTOM_RECIPE_NUMBER, MyConstants.NO_COMMUNITY_LESSON_NUMBER_ERROR);
                            editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_NAME,null);
                            editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_DESCRIPTION,null);
                            editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_INGREDIENTS, null);
                            editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_STEPS, null);
                            editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_TIME, null);
                            editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_DIFFICULTY_LEVEL,null);
                            editor.putBoolean(MyConstants.ORIGINAL_CUSTOM_RECIPE_KOSHER, false);
                            editor.putInt(MyConstants.ORIGINAL_CUSTOM_RECIPE_SERVE_COUNT,MyConstants.NO_SERVE_COUNT_ERROR);


                            editor.putString(MyConstants.CUSTOM_RECIPE_NAME, null);
                            editor.putString(MyConstants.CUSTOM_RECIPE_DESCRIPTION, null);
                            editor.putString(MyConstants.CUSTOM_RECIPE_INGREDIENTS, null);
                            editor.putString(MyConstants.CUSTOM_RECIPE_STEPS, null);
                            editor.putString(MyConstants.CUSTOM_RECIPE_TIME, null);
                            editor.putString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL, null);
                            editor.putBoolean(MyConstants.CUSTOM_RECIPE_KOSHER, false);
                            editor.putInt(MyConstants.ORIGINAL_CUSTOM_RECIPE_SERVE_COUNT, MyConstants.NO_SERVE_COUNT_ERROR);

                            editor.commit();


                            finish();

                        }
                    });


                    exitWithoutSavingDialogBuilder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                        }
                    });

                    AlertDialog exitWithoutSavingDialog = exitWithoutSavingDialogBuilder.create();
                    exitWithoutSavingDialog.show();
                }
            });

        }



        else {
            Bundle stepsBundle = new Bundle();
            stepsBundle.putString(MyConstants.CUSTOM_RECIPE_STEPS_VIEW_MODE, MyConstants.CUSTOM_RECIPE_VIEW_STEPS_FINISH);

            viewStepsFrag.setArguments(stepsBundle);
        }
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