package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
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
import java.util.Collections;

public class HomeScreen extends AppCompatActivity implements CustomViewHolder.OnItemClickListener {

    TextView selectedCourseTV;
    ImageView iv;
    RecyclerView lessonView;
    FirebaseDatabase FBDB;
    DatabaseReference refUsers, refLessons;
    FirebaseUser loggedInUser;
    FirebaseAuth fAuth;

    String userEmail, selectedCourseName;
    Course selectedCourseGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        fAuth = FirebaseAuth.getInstance();
        loggedInUser = fAuth.getCurrentUser();

        //PFP Load Code
        iv = findViewById(R.id.homePFPIV);
        myServices.getProfilePhotoFromFirebase(iv);



        //Course Load Code
        selectedCourseTV = findViewById(R.id.selectedCourseTV);
        userEmail = loggedInUser.getEmail();
        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        refUsers=FBDB.getReference("Users").child(loggedInUser.getUid());
        ValueEventListener courseGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                        User currentlyLoggedUser = snapshot.getValue(User.class);
                        selectedCourseName = currentlyLoggedUser.getSelectedCourse();
                        selectedCourseTV.setText(selectedCourseName + " Course");
                        refLessons = FBDB.getReference("Courses").child(selectedCourseName);
                        ValueEventListener lessonLoader = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Course selectedCourse = new Course(selectedCourseName);
                                selectedCourse.clearLessonsList();
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    Lesson addedLesson = data.getValue(Lesson.class);
                                    selectedCourse.addLesson(addedLesson);

                                }
                                //By default, FB sorts items by ABC, so this is used to sort lessons by predetermined numbers set by me:
                                selectedCourse.sortLessonsListByNumber();
                                makeCourseRecyclerView(selectedCourse);

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                        refLessons.addListenerForSingleValueEvent(lessonLoader);


                if (currentlyLoggedUser.hasFinishedCourse() && currentlyLoggedUser.getFinishedCourse()!=MyConstants.FINISHED_COURSE){

                    Toast.makeText(HomeScreen.this, "CONGRATS", Toast.LENGTH_LONG).show();
                    currentlyLoggedUser.setFinishedCourse(MyConstants.FINISHED_COURSE);
                    if (currentlyLoggedUser.getCompletedCourses().get(MyConstants.COMPLETED_COURSES_PLACEHOLDER_INDEX).equals(MyConstants.COMPLETED_COURSES_PLACEHOLDER) )
                    {
                        currentlyLoggedUser.setCompletedCourses(new ArrayList<>());
                    }
                    currentlyLoggedUser.getCompletedCourses().add(currentlyLoggedUser.getSelectedCourse());
                    refUsers.setValue(currentlyLoggedUser);

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addListenerForSingleValueEvent(courseGetter);




    }

    public void goToProfile(View view){
       myServices.goToProfilePage(HomeScreen.this);
    }

//    public void goToCommunityPage(View view){
//        myServices.goToCommunityPage(HomeScreen.this);
//    }

    public void makeCourseRecyclerView(Course course){
        lessonView = findViewById(R.id.lessonView);

        // Create an instance of your adapter
        customAdapter adapter = new customAdapter(HomeScreen.this, course, this::onItemClick);


        // Set the layout manager for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeScreen.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        lessonView.setLayoutManager(linearLayoutManager);

        // Set the adapter for the RecyclerView
        lessonView.setAdapter(adapter);
        selectedCourseGlobal = course;
    }

    public boolean setLastLesson(Course course, int position){
        ArrayList<Lesson> lessons = course.getLessonsList();
        if (position == lessons.size()-1) return true;
        else return false;
    }

    @Override
    public void onItemClick(int position) {

        Toast.makeText(HomeScreen.this,"PRESSED " + selectedCourseGlobal.getLessonsList().get(position).getLessonName() , Toast.LENGTH_LONG ).show();
        Intent toLessonIntro = new Intent(HomeScreen.this, LessonIntro.class);
        toLessonIntro.putExtra("Lesson Position - 1", position);
        toLessonIntro.putExtra("Is Lesson Final", setLastLesson(selectedCourseGlobal, position));
        toLessonIntro.putExtra("Course Name", selectedCourseGlobal.getCourseName());
        toLessonIntro.putExtra("Lesson Name", selectedCourseGlobal.getLessonsList().get(position).getLessonName());
        startActivity(toLessonIntro);


    }
}