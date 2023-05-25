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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent gi = getIntent();

        //We check if the user got to this activity from the finish screen or from the activity before this one

        if (gi.getStringExtra("Previous Activity").equals(MyConstants.FROM_FINISH_SCREEN)){
            //Right now I do not allow users to edit by pressing on items from the finish screen, so  this will remain empty for now
        }

        else if (gi.getStringExtra("Previous Activity").equals(MyConstants.NOT_FROM_FINISH_SCREEN)){

        }


        binding = ActivityTrulyFinalCreateRecipeFinishScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Overview
        TrulyFinalCreateRecipeOverviewFrag overviewFrag = new TrulyFinalCreateRecipeOverviewFrag();

        //Ingredients
        TrulyFinalCreateRecipeViewIngredientsFrag viewIngredientsFrag = new TrulyFinalCreateRecipeViewIngredientsFrag();

        //Steps
        TrulyFinalCreateRecipeViewStepsFrag viewStepsFrag = new TrulyFinalCreateRecipeViewStepsFrag();

        Bundle stepsBundle = new Bundle();
        stepsBundle.putString(MyConstants.CUSTOM_RECIPE_STEPS_VIEW_MODE, MyConstants.CUSTOM_RECIPE_VIEW_STEPS_FINISH);

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