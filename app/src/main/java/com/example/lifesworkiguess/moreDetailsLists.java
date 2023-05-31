package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Space;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class moreDetailsLists extends AppCompatActivity implements
        CompletedCoursesAdapter.OnItemClickListener, CompletedLessonsAdapter.OnItemClickListener{

    String mode;
    TextView titleTV;
    RecyclerView mainRV, secondaryRV;
    Space secondaryDetailsRVSpace;

    User globalCurrentlyLoggedInUser;
    ValueEventListener infoGetter, getLessonName, getCreatorUsernames;
    DatabaseReference refUsers, refLesson, refCreatorUsernames;

    ArrayList<String> completedCoursesNames;
    ArrayList<String> finishedCommunityLessonsCreatorsUsernames;
    ArrayList<CommunityLesson> finishedCommunityLessons;
    ArrayList<CommunityLesson> activeRecipesMadeByUser;

    FirebaseDatabase FBDB;
    FirebaseAuth fAuth;
    FirebaseUser loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_made_community_lessons_list);

        titleTV = findViewById(R.id.moreDetailsLists_TitleTV);
        mainRV = findViewById(R.id.mainDetailsRV);
        secondaryRV = findViewById(R.id.secondaryDetailsRV);
        secondaryDetailsRVSpace = findViewById(R.id.secondaryDetailsRVSpace);

        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        fAuth = FirebaseAuth.getInstance();
        loggedInUser = fAuth.getCurrentUser();

        Intent gi = getIntent();

        mode = gi.getStringExtra("View Mode");

        if (mode.equals("Completed Lessons & Courses"))
        {
            titleTV.setText("Completed Lessons & Courses");
            getCompletedLessonsAndCourses();
        }

        if (mode.equals("Completed Community Recipes"))
        {
            titleTV.setText("Completed Community Recipes");
            secondaryRV.setVisibility(View.GONE);
            secondaryDetailsRVSpace.setVisibility(View.GONE);
            getCompletedCommunityLessonsList();

        }

        if (mode.equals("Your Recipes"))
        {
            titleTV.setText("Your Recipes");
            secondaryRV.setVisibility(View.GONE);
            secondaryDetailsRVSpace.setVisibility(View.GONE);
            getRecipesMadeByUser();
        }

    }

    @Override
    protected void onPause() {

        super.onPause();
        if (refUsers!=null && infoGetter !=null) refUsers.removeEventListener(infoGetter);
        if (refLesson!=null && getLessonName!=null) refLesson.removeEventListener(getLessonName);

    }


    @Override
    protected void onResume() {

        super.onResume();

    }

    public void onDestroy() {

        super.onDestroy();
        if (refUsers!=null && infoGetter !=null) refUsers.removeEventListener(infoGetter);
        if (refLesson!=null && getLessonName!=null) refLesson.removeEventListener(getLessonName);

    }

    //Completed Lessons and Courses:

    public void getCompletedLessonsAndCourses()
    {

        refUsers = FBDB.getReference("Users").child(loggedInUser.getUid());
        infoGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                globalCurrentlyLoggedInUser = snapshot.getValue(User.class);

                completedCoursesNames = new ArrayList<>();

                completedCoursesNames.add(globalCurrentlyLoggedInUser.getSelectedCourse());


                for (String completedCourseName: globalCurrentlyLoggedInUser.getCompletedCourses())
                {
                    if (!globalCurrentlyLoggedInUser.getSelectedCourse().equals(completedCourseName) && !completedCourseName.equals(MyConstants.COMPLETED_COURSES_PLACEHOLDER))
                        completedCoursesNames.add(completedCourseName);

                }

                makeCompletedCoursesRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addListenerForSingleValueEvent(infoGetter);
    }

    public void makeCompletedCoursesRecyclerView(){

        // Create an instance of your adapter
        CompletedCoursesAdapter adapter = new CompletedCoursesAdapter(moreDetailsLists.this, completedCoursesNames, this::onItemClickCompletedCourses);


        // Set the layout manager for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(moreDetailsLists.this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        secondaryRV.setLayoutManager(linearLayoutManager);

        // Set the adapter for the RecyclerView
        secondaryRV.setAdapter(adapter);


    }




    @Override
    public void onItemClickCompletedCourses(int position) {

        String pickedCourseName =  completedCoursesNames.get(position);

        refLesson= FBDB.getReference("Courses").child(pickedCourseName);
        getLessonName = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<String> lessonsNames = new ArrayList<>();
                ArrayList<String> lessonsRatings = new ArrayList<>();

                Course pickedCourse = new Course(pickedCourseName);
                for (DataSnapshot data : snapshot.getChildren()) {
                    PermanentLesson addedPermanentLesson = data.getValue(PermanentLesson.class);
                    pickedCourse.addLesson(addedPermanentLesson);

                }

                //By default, FB sorts items by ABC, so this is used to sort lessons by predetermined numbers set by me:
                pickedCourse.sortLessonsListByNumber();


                if (position==0) //User picked Currently Selected Course, which may not have been completed
                {
                    for (int i=0; i<pickedCourse.getLessonsList().size(); i++)
                    {
                        if (globalCurrentlyLoggedInUser.getLessonsStatus().get(i).equals(MyConstants.FINISHED_LESSON))
                        {
                            lessonsNames.add(pickedCourse.getLessonsList().get(i).getLessonName());

                        }
                    }
                    for (ArrayList<String> checkedRatingList: globalCurrentlyLoggedInUser.getLessonsRating())
                    {
                        if (checkedRatingList.get(0).equals(pickedCourseName))
                        {
                            for (int i=0; i<pickedCourse.getLessonsList().size(); i++)
                            {
                                if (globalCurrentlyLoggedInUser.getLessonsStatus().get(i).equals(MyConstants.FINISHED_LESSON))
                                {
                                    lessonsRatings.add(checkedRatingList.get(i+1)); //index 0 is reserved for course name
                                }

                            }

                        }
                    }
                }

                else if (position!=0)
                {
                    for (int i =0; i<pickedCourse.getLessonsList().size(); i ++)
                    {
                        lessonsNames.add(pickedCourse.getLessonsList().get(i).getLessonName());
                    }

                    for (ArrayList<String> checkedRatingList: globalCurrentlyLoggedInUser.getLessonsRating()) {
                        if (checkedRatingList.get(0).equals(pickedCourseName))
                        {
                            for (int i=0; i<pickedCourse.getLessonsList().size(); i++)
                            {

                                lessonsRatings.add(checkedRatingList.get(i+1)); //index 0 is reserved for course name


                            }
                        }
                    }
                }




                makeCompletedLessonsRecyclerView(pickedCourseName, lessonsNames, lessonsRatings);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refLesson.addListenerForSingleValueEvent(getLessonName);
    }

    public void makeCompletedLessonsRecyclerView(String pickedCourseName, ArrayList<String> lessonsNames, ArrayList<String> lessonsRatings){

        CompletedLessonsAdapter adapter =
                new CompletedLessonsAdapter(moreDetailsLists.this, pickedCourseName, lessonsNames, lessonsRatings, this::onItemClickCompletedLessons);

        // Set the layout manager for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(moreDetailsLists.this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mainRV.setLayoutManager(linearLayoutManager);

        // Set the adapter for the RecyclerView
        mainRV.setAdapter(adapter);


    }


    @Override
    public void onItemClickCompletedLessons(int position) {

    }


    //Completed Community Lessons:

    public void getCompletedCommunityLessonsList()
    {

        ArrayList<String> creatorIDs = new ArrayList<>();
        finishedCommunityLessons = new ArrayList<>();
        finishedCommunityLessonsCreatorsUsernames = new ArrayList<>();

        refUsers = FBDB.getReference("Users").child(loggedInUser.getUid());
        infoGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User currentlyLoggedInUser = snapshot.getValue(User.class);
                ArrayList<ArrayList<String>> allFinishedLessonsList = currentlyLoggedInUser.getFinishedCommunityLessons();

                refLesson = FBDB.getReference("Community Lessons");
                getLessonName = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (ArrayList<String> currFinishedLessonList : allFinishedLessonsList) {

                                String creatorID = currFinishedLessonList.get(0);
                                creatorIDs.add(creatorID);
                                String lessonNumber = currFinishedLessonList.get(1);
                                String currLessonKey = creatorID + " , " + lessonNumber;
                                for (DataSnapshot childSnapshot : snapshot.getChildren())
                                {
                                    if (currLessonKey.equals(childSnapshot.getKey()))
                                    {
                                        CommunityLesson addedLesson = childSnapshot.getValue(CommunityLesson.class);
                                        finishedCommunityLessons.add(addedLesson);
                                    }
                                }



                        }

                        refCreatorUsernames = FBDB.getReference("Users");
                        getCreatorUsernames = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (String checkedID : creatorIDs) {

                                    for (DataSnapshot childSnapshot : snapshot.getChildren())
                                    {
                                        if (childSnapshot.getKey().equals(checkedID))
                                        {
                                            User addedUser = childSnapshot.getValue(User.class);
                                            finishedCommunityLessonsCreatorsUsernames.add(addedUser.getUsername());
                                        }
                                    }
                                }

                                CompletedCommunityLessonsAdapter adapter = new CompletedCommunityLessonsAdapter(moreDetailsLists.this,
                                        finishedCommunityLessons, finishedCommunityLessonsCreatorsUsernames);

                                // Set the layout manager for the RecyclerView
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(moreDetailsLists.this);
                                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                                mainRV.setLayoutManager(linearLayoutManager);

                                // Set the adapter for the RecyclerView
                                mainRV.setAdapter(adapter);

                                //DividerItemDecoration
                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(moreDetailsLists.this, DividerItemDecoration.VERTICAL);
                                dividerItemDecoration.setDrawable(ContextCompat.getDrawable(mainRV.getContext(),
                                        R.drawable.divider_black));
                                mainRV.addItemDecoration(dividerItemDecoration);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                        refCreatorUsernames.addListenerForSingleValueEvent(getCreatorUsernames);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                refLesson.addListenerForSingleValueEvent(getLessonName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addListenerForSingleValueEvent(infoGetter);
    }




    //Recipes Made by User:
    public void getRecipesMadeByUser(){
        refLesson = FBDB.getReference("Community Lessons By User").child(loggedInUser.getUid());
        getLessonName = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                activeRecipesMadeByUser = new ArrayList<>();

                for (DataSnapshot child: snapshot.getChildren())
                {
                    CommunityLesson checkedLesson = child.getValue(CommunityLesson.class);
                    if (checkedLesson.isActive()) activeRecipesMadeByUser.add(checkedLesson);
                }

                refUsers = FBDB.getReference("Users").child(loggedInUser.getUid());
                infoGetter = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        globalCurrentlyLoggedInUser = snapshot.getValue(User.class);


                        makeRecipesMadeByUserRV();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                refUsers.addListenerForSingleValueEvent(infoGetter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refLesson.addListenerForSingleValueEvent(getLessonName);
    }

    public void makeRecipesMadeByUserRV()
    {
        MadeCommunityLessonsAdapter adapter =
                new MadeCommunityLessonsAdapter(moreDetailsLists.this, activeRecipesMadeByUser, loggedInUser.getUid(), globalCurrentlyLoggedInUser.getUsername());

        // Set the layout manager for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(moreDetailsLists.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mainRV.setLayoutManager(linearLayoutManager);

        // Set the adapter for the RecyclerView
        mainRV.setAdapter(adapter);

        //DividerItemDecoration
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(moreDetailsLists.this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(mainRV.getContext(),
                R.drawable.divider_black));
        mainRV.addItemDecoration(dividerItemDecoration);


    }


}