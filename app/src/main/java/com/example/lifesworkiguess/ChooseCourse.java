package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseCourse extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner cookingStyleSpinner, experienceSpinner, hoursSpinner;
    String cookingStyle, experienceLevel, weeklyHour;
    TextView titleTV;
    Button toSignUpORChangeCourseBTN;

    FirebaseAuth fAuth;
    FirebaseUser currentlyLoggedInUser;
    FirebaseDatabase FBDB;
    DatabaseReference refUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_course);

        titleTV = findViewById(R.id.chooseCourseScreenTitleTV);
        toSignUpORChangeCourseBTN = findViewById(R.id.chooseCourseScreenBTN);

        cookingStyleSpinner = findViewById(R.id.cookingStyleSpinner);
        ArrayAdapter<String> adpCookingStyles = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, MyConstants.COOKING_STYLES);
        cookingStyleSpinner.setAdapter(adpCookingStyles);
        cookingStyleSpinner.setOnItemSelectedListener(this);

        experienceSpinner = findViewById(R.id.experienceSpinner);
        ArrayAdapter<String> adpExperienceLevels = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, MyConstants.EXPERIENCE_LEVELS);
        experienceSpinner.setAdapter(adpExperienceLevels);
        experienceSpinner.setOnItemSelectedListener(this);

        hoursSpinner = findViewById(R.id.hoursSpinner);
        ArrayAdapter<String> adpWeeklyHours = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, MyConstants.WEEKLY_HOURS);
        hoursSpinner.setAdapter(adpWeeklyHours);
        hoursSpinner.setOnItemSelectedListener(this);


        Intent getOrigin = getIntent();
        if (getOrigin.getIntExtra("Previous Activity", MyConstants.NO_PREVIOUS_ACTIVITY_ERROR) == MyConstants.FROM_MAIN_ACTIVITY){
            titleTV.setText("Hello!");
            toSignUpORChangeCourseBTN.setText("Next");
        }
        else if (getOrigin.getIntExtra("Previous Activity", MyConstants.NO_PREVIOUS_ACTIVITY_ERROR) == MyConstants.FROM_PROFILE){
            titleTV.setText("Welcome Back!");
            toSignUpORChangeCourseBTN.setText("Start New Course!");

        }


        fAuth = FirebaseAuth.getInstance();
        currentlyLoggedInUser = fAuth.getCurrentUser();
        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        try {
            refUsers = FBDB.getReference("Users").child(currentlyLoggedInUser.getUid());

        }
        catch (Exception e){
            Log.e("ERROR", e.toString());
        }


    }







    public void signUpORChangeCourse(View view){

        Intent getOrigin = getIntent();
        if (getOrigin.getIntExtra("Previous Activity", MyConstants.NO_PREVIOUS_ACTIVITY_ERROR) == MyConstants.FROM_MAIN_ACTIVITY){
            Intent makeUser = new Intent(this, SignUp.class);

            makeUser.putExtra("Cooking Style", cookingStyle);
            makeUser.putExtra("Experience Level", experienceLevel);
            makeUser.putExtra("Weekly Hours", weeklyHour);

            startActivity(makeUser);
        }
        else if (getOrigin.getIntExtra("Previous Activity", MyConstants.NO_PREVIOUS_ACTIVITY_ERROR) == MyConstants.FROM_PROFILE){


                ValueEventListener changeUsersCourse = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User currentUser = snapshot.getValue(User.class);
                        if (experienceLevel.equals(currentUser.getExperienceLevel()) && cookingStyle.equals(currentUser.getCookingStyle())){

                            if (!weeklyHour.equals(currentUser.getHours())) {
                                currentUser.setHours(weeklyHour);
                                refUsers.setValue(currentUser);
                            }
                            Toast.makeText(ChooseCourse.this, "Same Course", Toast.LENGTH_SHORT).show();

                            Toast.makeText(ChooseCourse.this, "Please wait", Toast.LENGTH_SHORT).show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    Intent toHomeScreen = new Intent(ChooseCourse.this, HomeScreen.class);
                                    toHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(toHomeScreen);
                                  }
                                }, 2000);


                        }

                        else{
                            currentUser.setFinishedCourse(MyConstants.NOT_FINISHED_COURSE);
                            currentUser.setCookingStyle(cookingStyle);
                            currentUser.setExperienceLevel(experienceLevel);
                            currentUser.setHours(weeklyHour);
                            currentUser.updateSelectedCourse();
                            refUsers.setValue(currentUser);

                            //For lessonsStatus and lessonsRating initialization:
                            currentUser.setLessonsRating(new ArrayList<>());
                            currentUser.setLessonsStatus(new ArrayList<>());
                            DatabaseReference refLessons = FBDB.getReference("Courses").child(currentUser.getSelectedCourse());
                            ValueEventListener lessonLoader = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    long lessonCount = snapshot.getChildrenCount();
                                    for (int i = 0; i < lessonCount; i++) {

                                        currentUser.getLessonsStatus().add(0);
                                        currentUser.getLessonsRating().add(0F);

                                    }
                                    refUsers.setValue(currentUser);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };
                            refLessons.addListenerForSingleValueEvent(lessonLoader);

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                refUsers.addListenerForSingleValueEvent(changeUsersCourse);
            }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId()== R.id.cookingStyleSpinner){
           cookingStyle = MyConstants.COOKING_STYLES[position];
        }

        if (parent.getId()== R.id.experienceSpinner){
            experienceLevel = MyConstants.EXPERIENCE_LEVELS[position];

        }

        if (parent.getId()== R.id.hoursSpinner){
            weeklyHour = MyConstants.WEEKLY_HOURS[position];

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}