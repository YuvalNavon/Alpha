package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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

public class HomeScreen extends AppCompatActivity implements CustomViewHolder.OnItemClickListener {

    TextView selectedCourseTV;
    ImageView iv;
    RecyclerView lessonView;
    FirebaseDatabase FBDB;
    DatabaseReference refUsers, refLessons;
    ValueEventListener courseGetter, lessonLoader;
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
        myServices.getProfilePhotoFromFirebase(iv, loggedInUser.getUid());



        //Course Load Code
        selectedCourseTV = findViewById(R.id.selectedCourseTV);
        userEmail = loggedInUser.getEmail();
        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        refUsers=FBDB.getReference("Users").child(loggedInUser.getUid());
        courseGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                        User currentlyLoggedUser = snapshot.getValue(User.class);
                        selectedCourseName = currentlyLoggedUser.getSelectedCourse();
                        selectedCourseTV.setText(selectedCourseName + " Course");
                        refLessons = FBDB.getReference("Courses").child(selectedCourseName);
                        lessonLoader = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Course selectedCourse = new Course(selectedCourseName);
                                selectedCourse.clearLessonsList();
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    PermanentLesson addedPermanentLesson = data.getValue(PermanentLesson.class);
                                    selectedCourse.addLesson(addedPermanentLesson);

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

                    boolean userPreviouslyCompletedCourse = false;

                    for (String checkedCompletedCourseName: currentlyLoggedUser.getCompletedCourses())
                    {
                        if (currentlyLoggedUser.getSelectedCourse().equals(checkedCompletedCourseName))
                            userPreviouslyCompletedCourse = true;

                    }
                    if (!userPreviouslyCompletedCourse)
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

    @Override
    protected void onPause() {

        super.onPause();
        if (refUsers!=null && courseGetter!=null) refUsers.removeEventListener(courseGetter);

    }


    @Override
    protected void onResume() {

        super.onResume();
        if (refUsers!=null && courseGetter!=null) refUsers.addValueEventListener(courseGetter);
        fAuth = FirebaseAuth.getInstance();
        loggedInUser = fAuth.getCurrentUser();
        myServices.getProfilePhotoFromFirebase(iv, loggedInUser.getUid());

    }

    public void onDestroy() {

        super.onDestroy();
        if (refUsers!=null && courseGetter!=null) refUsers.removeEventListener(courseGetter);

    }


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
        ArrayList<PermanentLesson> permanentLessons = course.getLessonsList();
        if (position == permanentLessons.size()-1) return true;
        else return false;
    }

    @Override
    public void onItemClick(int position) {

        Toast.makeText(HomeScreen.this,"PRESSED " + selectedCourseGlobal.getLessonsList().get(position).getLessonName() , Toast.LENGTH_LONG ).show();
        Intent toLessonIntro = new Intent(HomeScreen.this, NewLessonIntro.class);

        Lesson pickedLesson = selectedCourseGlobal.getLessonsList().get(position);
        toLessonIntro.putExtra(MyConstants.LESSON_INTRO_MODE_KEY, MyConstants.PERMENANT_LESSON_INTRO);
        toLessonIntro.putExtra("Lesson Position - 1", position); // THE MINUS 1 is bc we need the indexes of the lessons in the list,
        // the first lesson is 0 in the list, the second is 1, and so on
        toLessonIntro.putExtra("Is Lesson Final", setLastLesson(selectedCourseGlobal, position));
        toLessonIntro.putExtra("Course Name", selectedCourseGlobal.getCourseName());
        toLessonIntro.putExtra("Lesson Name", pickedLesson.getLessonName());
        toLessonIntro.putExtra(MyConstants.PERMENANT_LESSON_RECIPE_IMAGE_URI_KEY, pickedLesson.getLogoUri());
        startActivity(toLessonIntro);


    }

    public void goToProfile(View view){
        myServices.goToProfilePage(HomeScreen.this);
    }

    public void goToCommunityPage(View view){  myServices.goToCommunityPage(HomeScreen.this);
    }
}