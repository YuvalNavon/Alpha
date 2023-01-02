package com.example.lifesworkiguess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RatingBar;

public class LessonFinish extends AppCompatActivity {
RatingBar ratingBar;
float ratingGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_finish);

        ratingBar = findViewById(R.id.lessonFinishRB);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingGlobal = rating;
                ratingBar.setRating(rating);
            }
        });
    }



}