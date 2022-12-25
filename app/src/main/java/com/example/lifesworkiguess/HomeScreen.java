package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
    RecyclerView lessonView;
    ArrayList<Lesson> lessonList;
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

        selectedCourseTV = findViewById(R.id.selectedCourseTV);

        fAuth = FirebaseAuth.getInstance();
        loggedInUser = fAuth.getCurrentUser();
        userEmail = loggedInUser.getEmail();
        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        refUsers=FBDB.getReference("Users");
        ValueEventListener courseGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    User checkedUser = data.getValue(User.class);
                    if (userEmail.equals(checkedUser.getEmail())) {
                        selectedCourseName = checkedUser.getSelectedCourse();
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
                                //Just for Appeal:
                                ArrayList<Lesson> revList = selectedCourse.getLessonsList();
                                Collections.reverse(revList);
                                selectedCourse.setLessonsList(revList);
                                //End of Appeal
                                makeRecyclerView(selectedCourse);

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                        refLessons.addValueEventListener(lessonLoader);


                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addValueEventListener(courseGetter);











    }

    public void makeRecyclerView(Course course){
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
        Toast.makeText(this,"PRESSED " + selectedCourseGlobal.getLessonsList().get(position).getLessonName() , Toast.LENGTH_LONG ).show();
        Intent toLessonIntro = new Intent(this, LessonIntro.class);
        toLessonIntro.putExtra("Lesson Position - 1", position);
        toLessonIntro.putExtra("Is Lesson Final", setLastLesson(selectedCourseGlobal, position));
        toLessonIntro.putExtra("Course Name", selectedCourseGlobal.getCourseName());
        toLessonIntro.putExtra("Lesson Name", selectedCourseGlobal.getLessonsList().get(position).getLessonName());
        startActivity(toLessonIntro);
    }
}