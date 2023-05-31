/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Activity is where the user performs the Recipe of the Lesson they started.
 * they can view the ingredients of the Recipe as well.
 */

package com.example.lifesworkiguess;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.lifesworkiguess.databinding.ActivityNewLessonScreenBinding;

import java.util.ArrayList;

public class newLessonScreen extends AppCompatActivity {

    //For Permanent Lesson
    int lessonPosition;

    //For Community Lesson
    String creatorID, creatorUsername;

    //For Both
    String lessonName;

    Intent getLessonDetails;
    String fromIntroOrFromDuringCreatingRecipe;
    ArrayList<Fragment> fragmentList;
    ArrayList<String> fragmentTitleList;


    private ActivityNewLessonScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityNewLessonScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getLessonDetails = getIntent();

        fromIntroOrFromDuringCreatingRecipe = getLessonDetails.getStringExtra(MyConstants.VIEW_STEP_MODE_KEY);

        if (fromIntroOrFromDuringCreatingRecipe.equals(MyConstants.FROM_LESSON_INTRO))
        {
            lessonIntroSetUp();
        }




        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, fragmentTitleList);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);



    }


    /**
     * this function gets the details for the picked Lesson from the Intent
     * and passes it in a bundle to the LessonScreenFrag Fragment.
     *
     * @param
     *
     *
     *
     * @return
     */
    public void lessonIntroSetUp(){
        int permanentOrCommunity = getLessonDetails.getIntExtra(MyConstants.LESSON_INTRO_MODE_KEY, MyConstants.LESSON_INTRO_MODE_ERROR);
        Bundle intentDataForLessonFrag = new Bundle();
        intentDataForLessonFrag.putInt(MyConstants.LESSON_INTRO_MODE_KEY, permanentOrCommunity);
        intentDataForLessonFrag.putString(MyConstants.VIEW_STEP_MODE_KEY, MyConstants.FROM_LESSON_INTRO);
        if (permanentOrCommunity == MyConstants.PERMENANT_LESSON_INTRO)
        {
            lessonName = getLessonDetails.getStringExtra("Lesson Name");
            lessonPosition = getLessonDetails.getIntExtra("Lesson Position in List", MyConstants.NO_LESSON_POSITION);

            intentDataForLessonFrag.putString("Lesson Name",lessonName );
            intentDataForLessonFrag.putInt("Lesson Position in List", lessonPosition);
        }

        else if (permanentOrCommunity == MyConstants.COMMUNITY_LESSON_INTRO)
        {
            lessonName = getLessonDetails.getStringExtra(MyConstants.LESSON_NAME_KEY);
            creatorUsername = getLessonDetails.getStringExtra(MyConstants.LESSON_CREATOR_USERNAME_KEY);
            creatorID = getLessonDetails.getStringExtra(MyConstants.LESSON_CREATOR_ID_KEY);

            intentDataForLessonFrag.putString(MyConstants.LESSON_NAME_KEY, lessonName);
            intentDataForLessonFrag.putString(MyConstants.LESSON_CREATOR_USERNAME_KEY, creatorUsername);
            intentDataForLessonFrag.putString(MyConstants.LESSON_CREATOR_ID_KEY, creatorID);
        }

        LessonScreenFrag lessonScreenFrag = new LessonScreenFrag();
        lessonScreenFrag.setArguments(intentDataForLessonFrag);

        IngredientsListFrag ingredientsListFrag = new IngredientsListFrag();

        fragmentList = new ArrayList<>();
        fragmentList.add(lessonScreenFrag);
        fragmentList.add(ingredientsListFrag);

        fragmentTitleList = new ArrayList<>();
        fragmentTitleList.add("Steps");
        fragmentTitleList.add("Ingredients");



    }


}