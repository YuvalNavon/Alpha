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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LessonIntro extends AppCompatActivity {

    String lessonName, courseName;
    int lessonPosition;
    boolean lessonFinal;
    Lesson selectedLessonGlobal;
    Recipe recipe;

    TextView lessonPositionTV, lessonNameTV, expectedTimeTV, difficultyTV, kosherTV, serveCountTV;
    ImageView kosherIV, upArrowIV, downArrowIV ;
    RecyclerView ingredientsRV;

    FirebaseDatabase FBDB;
    DatabaseReference refLessons;

    LinearLayout screen;
    Button startBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_intro);

        Intent getLessonData = getIntent();
        lessonName = getLessonData.getStringExtra("Lesson Name");
        courseName = getLessonData.getStringExtra("Course Name");
        lessonPosition = getLessonData.getIntExtra("Lesson Position - 1", MyConstants.NO_LESSON_POSITION);
        lessonFinal = getLessonData.getBooleanExtra("Is Lesson Final", false);


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

        upArrowIV = findViewById(R.id.upArrowIV);
        downArrowIV = findViewById(R.id.downArrowIV);


        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        refLessons = FBDB.getReference("Courses").child(courseName);
        ValueEventListener lessonGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                   Lesson checkedLesson = data.getValue(Lesson.class);

                            if (lessonName.equals(checkedLesson.getLessonName())){
                                Lesson selectedLesson = checkedLesson;
                                setGlobalLesson(selectedLesson);
                                getRecipe();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        // Actions to do after 2 seconds  (Has to be same delay as the delay in the getRecipe method)
                                        makeRecyclerView();

                                        //To make loading smooth 2
                                        screen.setVisibility(View.VISIBLE);
                                        startBTN.setEnabled(true);

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

    public void getRecipe(){
        myServices.downloadXML(this, selectedLessonGlobal.getLessonRecipeName(), "Courses/" + courseName + "/" + lessonName);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 2 second (Has to be same delay as calling the makeRecyclerView method)
                recipe = myServices.XMLToRecipe(LessonIntro.this, MyConstants.CURRENTLY_LEARNED_RECIPE);
                lessonNameTV.setText("Make Some " + recipe.getTitle() + "!");
                expectedTimeTV.setText(recipe.getTime());
                difficultyTV.setText(recipe.getDifficulty());
                if (recipe.isKosher()) {
                    kosherTV.setText("KOSHER");
                    kosherIV.setImageResource(com.firebase.ui.auth.R.drawable.fui_ic_check_circle_black_128dp);
                }
                else {
                    kosherTV.setText("NOT\nKOSHER");
                    kosherIV.setImageResource(android.R.drawable.ic_delete);
                }
                serveCountTV.setText(Integer.toString(recipe.getServeCount()));


            }
        }, 2000);
    }


    public void makeRecyclerView(){
        ingredientsRV = findViewById(R.id.ingredientsRecyclerView);

        // Create an instance of your adapter
        customAdapterIngredients adapter = new customAdapterIngredients(LessonIntro.this, recipe.getIngredients());


        // Set the layout manager for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LessonIntro.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        ingredientsRV.setLayoutManager(linearLayoutManager);

        // Set the adapter for the RecyclerView
        ingredientsRV.setAdapter(adapter);
        ingredientsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1)) {
                    // Cannot scroll up
                    upArrowIV.setVisibility(View.INVISIBLE);
                } else {
                    // Can scroll up
                    upArrowIV.setVisibility(View.VISIBLE);
                }
                if (!recyclerView.canScrollVertically(1)) {
                    // Cannot scroll down
                    downArrowIV.setVisibility(View.INVISIBLE);
                } else {
                    // Can scroll down
                    downArrowIV.setVisibility(View.VISIBLE);
                }
            }
        });

    }



    public void setGlobalLesson(Lesson lesson){
        selectedLessonGlobal = lesson;
        setPositionTV(lessonPosition);




    }


    public void setPositionTV(int lessonPosition){

        if (lessonFinal) lessonPositionTV.setText("Final Lesson");
        else lessonPositionTV.setText(MyConstants.LESSON_POSITIONS[lessonPosition] + " Lesson");
    }

    public void startLesson(View view){

          Intent toLessonScreen = new Intent(LessonIntro.this, LessonScreen.class);
          toLessonScreen.putExtra("Recipe Name", selectedLessonGlobal.getLessonRecipeName()); //RecipeName != Recipe.getTitle() ->
            // RecipeName is used to access the recipe from FB Storage while (Recipe for ___.xml) while Recipe.getTitle() is just ____
          toLessonScreen.putExtra("Lesson Name", selectedLessonGlobal.getLessonName());
          toLessonScreen.putExtra("Lesson Position in List", lessonPosition);
          toLessonScreen.putExtra("Course Name", courseName);
          startActivity(toLessonScreen);



    }

}