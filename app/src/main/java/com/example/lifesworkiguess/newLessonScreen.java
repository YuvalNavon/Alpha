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

    String lessonName;
    int lessonPosition;



    private ActivityNewLessonScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityNewLessonScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent getLessonName = getIntent();
        lessonName = getLessonName.getStringExtra("Lesson Name");
        lessonPosition = getLessonName.getIntExtra("Lesson Position in List", MyConstants.NO_LESSON_POSITION);

        Bundle intentDataForLessonFrag = new Bundle();
        intentDataForLessonFrag.putString("Lesson Name",lessonName );
        intentDataForLessonFrag.putInt("Lesson Position in List", lessonPosition);

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