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

    //For Permenant Lesson
    int lessonPosition;

    //For Community Lesson
    String creatorID, creatorUsername;

    //For Both
    String lessonName;



    private ActivityNewLessonScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityNewLessonScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent getLessonDetails = getIntent();

        int mode = getLessonDetails.getIntExtra(MyConstants.LESSON_INTRO_MODE_KEY, MyConstants.LESSON_INTRO_MODE_ERROR);
        Bundle intentDataForLessonFrag = new Bundle();
        intentDataForLessonFrag.putInt(MyConstants.LESSON_INTRO_MODE_KEY, mode);

        if (mode == MyConstants.PERMENANT_LESSON_INTRO)
        {
            lessonName = getLessonDetails.getStringExtra("Lesson Name");
            lessonPosition = getLessonDetails.getIntExtra("Lesson Position in List", MyConstants.NO_LESSON_POSITION);

            intentDataForLessonFrag.putString("Lesson Name",lessonName );
            intentDataForLessonFrag.putInt("Lesson Position in List", lessonPosition);
        }

        else if (mode == MyConstants.COMMUNITY_LESSON_INTRO)
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

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(lessonScreenFrag);
        fragmentList.add(ingredientsListFrag);

        ArrayList<String> fragmentTitleList = new ArrayList<>();
        fragmentTitleList.add("Steps");
        fragmentTitleList.add("Ingredients");

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, fragmentTitleList);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);





    }
}