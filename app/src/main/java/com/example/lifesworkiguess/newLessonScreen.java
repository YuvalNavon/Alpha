package com.example.lifesworkiguess;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lifesworkiguess.databinding.ActivityNewLessonScreenBinding;

import java.util.ArrayList;

public class newLessonScreen extends AppCompatActivity {

    String recipeName, lessonName, courseName;
    int currStepNumber, lessonPosition;

    TextView stepNumberTV, stepNameTV, stepDescriptionTV;
    ImageView stepImageIV;
    Button nextBtn;

    Recipe recipe;

    private ActivityNewLessonScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityNewLessonScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle intentDataForLessonFrag = new Bundle();
        intentDataForLessonFrag.putString("Recipe Name", recipeName);
        intentDataForLessonFrag.putString("Lesson Name",lessonName );
        intentDataForLessonFrag.putString("Course Name", courseName);
        intentDataForLessonFrag.putInt("Lesson Position in List", lessonPosition);

        LessonScreenFrag lessonScreenFrag = new LessonScreenFrag();
        lessonScreenFrag.setArguments(intentDataForLessonFrag);

        IngredientsListFrag ingredientsListFrag = new IngredientsListFrag();

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(lessonScreenFrag);
        fragmentList.add(ingredientsListFrag);

        ArrayList<String> fragmentTitleList = new ArrayList<>();
        fragmentTitleList.add("StepS");
        fragmentTitleList.add("Ingredients");

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, fragmentTitleList);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        Intent getLessonName = getIntent();
        recipeName = getLessonName.getStringExtra("Recipe Name");
        lessonName = getLessonName.getStringExtra("Lesson Name");
        courseName = getLessonName.getStringExtra("Course Name");
        lessonPosition = getLessonName.getIntExtra("Lesson Position in List", MyConstants.NO_LESSON_POSITION);




    }
}