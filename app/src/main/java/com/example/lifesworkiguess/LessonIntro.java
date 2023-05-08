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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LessonIntro extends AppCompatActivity {

    Intent getLessonData;

    //For PermanentLessonSetup
    String courseName;

    //For CommunityLessonSetup
    String creatorUsername, creatorID;

    //For Both
    String lessonName;

    int lessonPosition;
    boolean lessonFinal;
    PermanentLesson selectedPermanentLessonGlobal;
    CommunityLesson selectedCommunityLessonGlobal;
    Recipe recipe;

    TextView lessonPositionTV, lessonNameTV, expectedTimeTV, difficultyTV, kosherTV, serveCountTV;
    ImageView kosherIV, upArrowIV, downArrowIV ;
    RecyclerView ingredientsRV;

    FirebaseDatabase FBDB;
    DatabaseReference refLessons;
    ValueEventListener lessonGetter;

    LinearLayout screen;
    Button startBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_intro);

        //To make loading smooth 1
        screen = findViewById(R.id.lessonIntroScreen);
        startBTN = findViewById(R.id.startLessonBTN);
        screen.setVisibility(View.INVISIBLE);
        startBTN.setEnabled(false);

        lessonPositionTV = findViewById(R.id.lessonTitleTV);
        lessonNameTV = findViewById(R.id.lessonNameIntroScreenTV);
        expectedTimeTV = findViewById(R.id.expectedTimeTV);
        difficultyTV = findViewById(R.id.difficultyTV);
        kosherTV = findViewById(R.id.kosherTV);
        kosherIV = findViewById(R.id.kosherIV);
        serveCountTV = findViewById(R.id.lessonIntroScreenServeCountTV);


        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");

        getLessonData = getIntent();
        int fromHomeOrCommunity = getLessonData.getIntExtra(MyConstants.LESSON_INTRO_MODE_KEY, MyConstants.LESSON_INTRO_MODE_ERROR);

        if (fromHomeOrCommunity==MyConstants.PERMENANT_LESSON_INTRO){
            permanentLessonSetup();
        }

        else if (fromHomeOrCommunity==MyConstants.COMMUNITY_LESSON_INTRO)
        {
            communityLessonSetup();
        }


    }

    @Override
    protected void onPause() {

        super.onPause();
        if (refLessons!=null && lessonGetter!=null) refLessons.removeEventListener(lessonGetter);

    }


    @Override
    protected void onResume() {

        super.onResume();
        if (refLessons!=null && lessonGetter!=null) refLessons.addValueEventListener(lessonGetter);
    }

    public void onDestroy() {

        super.onDestroy();
        if (refLessons!=null && lessonGetter!=null) refLessons.removeEventListener(lessonGetter);

    }

//    public void getRecipe(int mode){
//        if (mode==MyConstants.PERMENANT_LESSON_INTRO)
//            myServices.downloadXML(this, selectedPermanentLessonGlobal.getLessonRecipeName(), "Courses/" + courseName + "/" + lessonName);
//
//        else if (mode==MyConstants.COMMUNITY_LESSON_INTRO)
//            myServices.downloadXML(this, MyConstants.UPLOADED_RECIPE_STORAGE_NAME,
//                    "Community Recipes/" + creatorID + "/" + selectedCommunityLessonGlobal.getLessonRecipeName() + "/" );
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                // Actions to do after 2 second (Has to be same delay as calling the makeRecyclerView method)
//                recipe = myServices.XMLToRecipe(LessonIntro.this, MyConstants.CURRENTLY_LEARNED_RECIPE);
//                lessonNameTV.setText("Make Some " + recipe.getTitle() + "!");
//                expectedTimeTV.setText(selectedPermanentLessonGlobal.getTime());
//                difficultyTV.setText(selectedPermanentLessonGlobal.getDifficulty());
//                if (selectedPermanentLessonGlobal.isKosher()) {
//                    kosherTV.setText("KOSHER");
//                    kosherIV.setImageResource(com.firebase.ui.auth.R.drawable.fui_ic_check_circle_black_128dp);
//                }
//                else {
//                    kosherTV.setText("NOT\nKOSHER");
//                    kosherIV.setImageResource(android.R.drawable.ic_delete);
//                }
//                serveCountTV.setText(Integer.toString(selectedPermanentLessonGlobal.getServeCount()));
//
//
//            }
//        }, 2000);
//    }


//    public void makeRecyclerView(){
//        ingredientsRV = findViewById(R.id.ingredientsRecyclerView);
//
//        // Create an instance of your adapter
//        customAdapterIngredients adapter = new customAdapterIngredients(LessonIntro.this, recipe.getIngredients());
//
//
//        // Set the layout manager for the RecyclerView
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LessonIntro.this);
//        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
//        ingredientsRV.setLayoutManager(linearLayoutManager);
//
//        // Set the adapter for the RecyclerView
//        ingredientsRV.setAdapter(adapter);
//
//        //To make loading smooth 2
//        screen.setVisibility(View.VISIBLE);
//        startBTN.setEnabled(true);
//
//        ingredientsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (!recyclerView.canScrollVertically(-1)) {
//                    // Cannot scroll up
//                    upArrowIV.setVisibility(View.INVISIBLE);
//                } else {
//                    // Can scroll up
//                    upArrowIV.setVisibility(View.VISIBLE);
//                }
//                if (!recyclerView.canScrollVertically(1)) {
//                    // Cannot scroll down
//                    downArrowIV.setVisibility(View.INVISIBLE);
//                } else {
//                    // Can scroll down
//                    downArrowIV.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//
//    }

    public void permanentLessonSetup(){
        lessonName = getLessonData.getStringExtra("Lesson Name");
        courseName = getLessonData.getStringExtra("Course Name");
        lessonPosition = getLessonData.getIntExtra("Lesson Position - 1", MyConstants.NO_LESSON_POSITION);
        lessonFinal = getLessonData.getBooleanExtra("Is Lesson Final", false);


        refLessons = FBDB.getReference("Courses").child(courseName);
        lessonGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    PermanentLesson checkedPermanentLesson = data.getValue(PermanentLesson.class);

                    if (lessonName.equals(checkedPermanentLesson.getLessonName())){
                        PermanentLesson selectedPermanentLesson = checkedPermanentLesson;
                        setGlobalLesson(selectedPermanentLesson);

                        myServices.downloadXML(LessonIntro.this, selectedPermanentLessonGlobal.getLessonRecipeName(), "Courses/" + courseName + "/" + lessonName);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                // Actions to do after 2 second (Has to be same delay as calling the makeRecyclerView method)
                                recipe = myServices.XMLToRecipe(LessonIntro.this, MyConstants.CURRENTLY_LEARNED_RECIPE);
                                lessonNameTV.setText("Make Some " + recipe.getTitle() + "!");
                                expectedTimeTV.setText(selectedPermanentLessonGlobal.getTime());
                                difficultyTV.setText(selectedPermanentLessonGlobal.getDifficulty());
                                if (selectedPermanentLessonGlobal.isKosher()) {
                                    kosherTV.setText("KOSHER");
                                    kosherIV.setImageResource(com.firebase.ui.auth.R.drawable.fui_ic_check_circle_black_128dp);
                                }
                                else {
                                    kosherTV.setText("NOT\nKOSHER");
                                    kosherIV.setImageResource(android.R.drawable.ic_delete);
                                }
                                serveCountTV.setText(Integer.toString(selectedPermanentLessonGlobal.getServeCount()));

//                                makeRecyclerView();


                            }
                        }, 2000);



                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refLessons.addValueEventListener(lessonGetter);

    }

    public void communityLessonSetup(){
        lessonName = getLessonData.getStringExtra(MyConstants.LESSON_NAME_KEY);
        creatorUsername = getLessonData.getStringExtra(MyConstants.LESSON_CREATOR_USERNAME_KEY);
        creatorID = getLessonData.getStringExtra(MyConstants.LESSON_CREATOR_ID_KEY);


        refLessons = FBDB.getReference("Community Lessons").child(lessonName + " , " + creatorID);
        lessonGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selectedCommunityLessonGlobal = snapshot.getValue(CommunityLesson.class);

                myServices.downloadXML(LessonIntro.this, MyConstants.RECIPE_STORAGE_NAME,
                        "Community Recipes/" + creatorID + "/" + selectedCommunityLessonGlobal.getLessonRecipeName());
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 2 second (Has to be same delay as calling the makeRecyclerView method)
                        recipe = myServices.XMLToRecipe(LessonIntro.this, MyConstants.CURRENTLY_LEARNED_RECIPE);
                        lessonPositionTV.setText(creatorUsername + "'s Recipe" );
                        lessonNameTV.setText(recipe.getTitle());
                        expectedTimeTV.setText(selectedCommunityLessonGlobal.getTime());
                        difficultyTV.setText(selectedCommunityLessonGlobal.getDifficulty());
                        if (selectedCommunityLessonGlobal.isKosher()) {
                            kosherTV.setText("KOSHER");
                            kosherIV.setImageResource(com.firebase.ui.auth.R.drawable.fui_ic_check_circle_black_128dp);
                        }
                        else {
                            kosherTV.setText("NOT\nKOSHER");
                            kosherIV.setImageResource(android.R.drawable.ic_delete);
                        }
                        serveCountTV.setText(Integer.toString(selectedCommunityLessonGlobal.getServeCount()));

//                        makeRecyclerView();


                    }
                }, 2000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refLessons.addListenerForSingleValueEvent(lessonGetter);
    }



    public void setGlobalLesson(PermanentLesson permanentLesson){

        selectedPermanentLessonGlobal = permanentLesson;
        setPositionTV(lessonPosition);

    }


    public void setPositionTV(int lessonPosition){

        if (lessonFinal) lessonPositionTV.setText("Final Lesson");
        else lessonPositionTV.setText(MyConstants.LESSON_POSITIONS[lessonPosition] + " Lesson");
    }

    public void startLesson(View view){

          Intent toLessonScreen = new Intent(LessonIntro.this, newLessonScreen.class);

          toLessonScreen.putExtra("Lesson Name", selectedPermanentLessonGlobal.getLessonName());
          toLessonScreen.putExtra("Lesson Position in List", lessonPosition);

          startActivity(toLessonScreen);
          finish();



    }

}