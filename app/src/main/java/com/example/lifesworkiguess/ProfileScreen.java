package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileScreen extends AppCompatActivity implements CompletedCoursesViewHolder.OnItemClickListener, CompletedLessonsViewHolder.OnItemClickListener {

    FirebaseAuth fAuth;
    FirebaseUser loggedInUser;
    FirebaseDatabase FBDB;
    DatabaseReference refUsers;
    TextView usernameTV, courseTV;
    ImageView profileScreenPFPIV,profileScreenMenuPFPIV ;
    Button changeCourse;
    LinearLayout profileScreen;
    User globalCurrentlyLoggedInUser;

    RecyclerView CompletedCoursesView, CompletedLessonsView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);


        //Recycler Views
        CompletedCoursesView = findViewById(R.id.CompletedCoursesRV);
        CompletedLessonsView = findViewById(R.id.CompletedLessonsRV);


        changeCourse = findViewById(R.id.PFPScreenChangeCourseBTN);

        fAuth = FirebaseAuth.getInstance();
        loggedInUser = fAuth.getCurrentUser();

        //To make Loading seem smooth 1
        profileScreen = findViewById(R.id.profileScreen);
        profileScreen.setVisibility(View.INVISIBLE);

        //Loading PFP's
        profileScreenPFPIV = findViewById(R.id.profileScreenPFPIV);
        profileScreenMenuPFPIV = findViewById(R.id.profileScreenMenuPFPIV);
        myServices.getProfilePhotoFromFirebase(profileScreenPFPIV);
        myServices.getProfilePhotoFromFirebase(profileScreenMenuPFPIV);


        //Loading User Data
        usernameTV = findViewById(R.id.profileScreenUsernameIV);
        courseTV = findViewById(R.id.profileScreenCourseTV);
        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        refUsers=FBDB.getReference("Users").child(loggedInUser.getUid());
        ValueEventListener courseGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User loggedInUser = snapshot.getValue(User.class);
                usernameTV.setText(loggedInUser.getUsername());
                courseTV.setText(loggedInUser.getSelectedCourse() + " Course");
                if (loggedInUser.getFinishedCourse()==MyConstants.FINISHED_COURSE){
                    changeCourse.setVisibility(View.VISIBLE);
                    changeCourse.setEnabled(true);
                }
                else{
                    changeCourse.setVisibility(View.INVISIBLE);
                    changeCourse.setEnabled(false);
                }

                //Making Completed Courses RV:
                makeCompletedCoursesRecyclerView(loggedInUser);




            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addValueEventListener(courseGetter);




        //To make Loading seem smooth 2
        profileScreen.setVisibility(View.VISIBLE);

    }

    public void makeCompletedCoursesRecyclerView(User currentUser){
        globalCurrentlyLoggedInUser = currentUser;

        // Create an instance of your adapter
        CompletedCoursesAdapter adapter = new CompletedCoursesAdapter(ProfileScreen.this, currentUser, this::onItemClick);


        // Set the layout manager for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfileScreen.this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        CompletedCoursesView.setLayoutManager(linearLayoutManager);

        // Set the adapter for the RecyclerView
        CompletedCoursesView.setAdapter(adapter);
    }

    public void makeCompletedLessonsRecyclerView(Course pickedCourse){
        //For smoothe loading 1
        CompletedLessonsView.setVisibility(View.INVISIBLE);
        CompletedLessonsView.setEnabled(true);

        CompletedLessonsAdapter adapter = new CompletedLessonsAdapter(ProfileScreen.this, globalCurrentlyLoggedInUser, pickedCourse, this::onItemClick2);
        // Set the layout manager for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfileScreen.this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        CompletedLessonsView.setLayoutManager(linearLayoutManager);

        // Set the adapter for the RecyclerView
        CompletedLessonsView.setAdapter(adapter);

        //For smoothe loading 2
        Toast.makeText(ProfileScreen.this, "Please wait", Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                CompletedLessonsView.setVisibility(View.VISIBLE);
                CompletedLessonsView.setEnabled(true);

            }
        }, 1000);
    }




    public void changeCourse(View view){
            Intent toChooseCourse = new Intent(this, ChooseCourse.class);
            toChooseCourse.putExtra(MyConstants.CHOOSE_COURSE_ORIGIN, MyConstants.FROM_PROFILE);
            startActivity(toChooseCourse);
    }

    public void editProfile(View view){
        Intent toEditProfileScreen = new Intent(this, EditProfile.class);
        startActivity(toEditProfileScreen);
    }

    public void goToHomePage(View view){
        myServices.goToHomePage(ProfileScreen.this);
    }


    @Override
    public void onItemClick(int position) {

        System.out.println("CLICKED");
        FirebaseDatabase FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        String pickedCourseName =  globalCurrentlyLoggedInUser.getCompletedCourses().get(position);
        DatabaseReference refLesson= FBDB.getReference("Courses").child(pickedCourseName);
        ValueEventListener getLessonName = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                    Course pickedCourse = new Course(pickedCourseName);
                for (DataSnapshot data : snapshot.getChildren()) {
                    Lesson addedLesson = data.getValue(Lesson.class);
                    pickedCourse.addLesson(addedLesson);

                }
                //By default, FB sorts items by ABC, so this is used to sort lessons by predetermined numbers set by me:
                pickedCourse.sortLessonsListByNumber();

                makeCompletedLessonsRecyclerView(pickedCourse);

                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refLesson.addValueEventListener(getLessonName);
    }

    @Override
    public void onItemClick2(int position) {

    }
}