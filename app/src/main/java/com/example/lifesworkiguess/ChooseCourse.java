package com.example.lifesworkiguess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ChooseCourse extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner cookingStyleSpinner, experienceSpinner, hoursSpinner;
    String[] cookingStyles, experienceLevels, weeklyHours;
    String cookingStyle, experienceLevel, weeklyHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_course);

        cookingStyles = new String[]{"Israeli"};
        experienceLevels = new String[]{"Completely New"};
        weeklyHours = new String[]{"4 Hours / a Week"};

        cookingStyleSpinner = findViewById(R.id.cookingStyleSpinner);
        ArrayAdapter<String> adpCookingStyles = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, cookingStyles);
        cookingStyleSpinner.setAdapter(adpCookingStyles);
        cookingStyleSpinner.setOnItemSelectedListener(this);

        experienceSpinner = findViewById(R.id.experienceSpinner);
        ArrayAdapter<String> adpExperienceLevels = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, experienceLevels);
        experienceSpinner.setAdapter(adpExperienceLevels);
        experienceSpinner.setOnItemSelectedListener(this);

        hoursSpinner = findViewById(R.id.hoursSpinner);
        ArrayAdapter<String> adpWeeklyHours = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, weeklyHours);
        hoursSpinner.setAdapter(adpWeeklyHours);
        hoursSpinner.setOnItemSelectedListener(this);



    }








    public void signUp(View view){

        Intent makeUser = new Intent(this, SignUp.class);

        makeUser.putExtra("Cooking Style", cookingStyle);
        makeUser.putExtra("Experience Level", experienceLevel);
        makeUser.putExtra("Weekly Hours", weeklyHour);

        startActivity(makeUser);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId()== R.id.cookingStyleSpinner){
           cookingStyle = cookingStyles[position];
        }

        if (parent.getId()== R.id.experienceSpinner){
            experienceLevel = experienceLevels[position];

        }

        if (parent.getId()== R.id.hoursSpinner){
            weeklyHour = weeklyHours[position];

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}