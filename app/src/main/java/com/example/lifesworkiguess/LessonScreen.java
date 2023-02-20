package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LessonScreen extends AppCompatActivity {

    String recipeName, lessonName, courseName;
    int currStepNumber, lessonPosition;

    TextView stepNumberTV, stepNameTV, stepDescriptionTV;
    ImageView stepImageIV;
    Button nextBtn;

    Recipe recipe;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_screen);





        Intent getLessonName = getIntent();
        recipeName = getLessonName.getStringExtra("Recipe Name");
        lessonName = getLessonName.getStringExtra("Lesson Name");
        courseName = getLessonName.getStringExtra("Course Name");
        lessonPosition = getLessonName.getIntExtra("Lesson Position in List", MyConstants.NO_LESSON_POSITION);

        Bundle intentDataForLessonFrag = new Bundle();
        intentDataForLessonFrag.putString("Recipe Name", recipeName);
        intentDataForLessonFrag.putString("Lesson Name",lessonName );
        intentDataForLessonFrag.putString("Course Name", courseName);
        intentDataForLessonFrag.putInt("Lesson Position in List", lessonPosition);

        LessonScreenFrag lessonScreenFrag = new LessonScreenFrag();
        lessonScreenFrag.setArguments(intentDataForLessonFrag);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragmentContainerView, lessonScreenFrag)
                .commit();







    }




}