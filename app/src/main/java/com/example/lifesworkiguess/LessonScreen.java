package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LessonScreen extends AppCompatActivity {

    String recipeName, lessonName, courseName;
    int currStepNumber;

    TextView stepNumberTV, stepNameTV, stepDescriptionTV;
    ImageView stepImageIV;
    Button nextBtn;

    Recipe recipe;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_screen);


        stepNumberTV = findViewById(R.id.stepNumberTV);
        stepNameTV = findViewById(R.id.stepNameTV);
        stepDescriptionTV = findViewById(R.id.stepDescriptionTV);
        stepImageIV = findViewById(R.id.stepImageIV);
        nextBtn = findViewById(R.id.nextBtn);

        Intent getLessonName = getIntent();
        recipeName = getLessonName.getStringExtra("Recipe Name");
        lessonName = getLessonName.getStringExtra("Lesson Name");
        courseName = getLessonName.getStringExtra("Course Name");

        myServices.downloadXML(this, recipeName, "Courses/" + courseName + "/" + lessonName);
        recipe = myServices.XMLToRecipe(LessonScreen.this, MyConstants.CURRENTLY_LEARNED_RECIPE);

        currStepNumber = 0;
        setStepDetails(currStepNumber);





    }



    public void setStepDetails(int stepNumber){
        Step currStep = recipe.getSteps().get(stepNumber);
        if (stepIsLast(stepNumber)){
            stepNumberTV.setText("Last Step");
            nextBtn.setText("Finish!");
        }
        else{
            stepNumberTV.setText("Step " + Integer.toString(stepNumber+1));

        }
        stepNameTV.setText(currStep.getName());
        stepDescriptionTV.setText(currStep.getDescription());
        //Implement stepAction WITHOUT DELAY
        Glide.with(this)
                .load(currStep.getAction())
                .into(stepImageIV);

    }

    public boolean stepIsLast(int stepNumber){
        if (stepNumber==recipe.getSteps().size()-1) return true;
        else return false;
    }

    public String determineGIF(int stepNumber){
        if (stepNumber>MyConstants.GIFS_LINKS_LIST.size()){
            return MyConstants.NO_GIF_ERROR;
        }
        else{
            String stepName = recipe.getSteps().get(stepNumber).getName();
            String gif = MyConstants.NO_GIF_ERROR;
            for (int i = 0; i<MyConstants.GIFS_LINKS_LIST.size(); i++){
                String actionName = formatStepName(stepName);
            }
            return gif;
        }

    }

    public String formatStepName(String stepName){

        //Find Connector Words to Remove them
        String beforeConnector, afterConnector;
        int spaceCount = 0;
        String tempName = stepName;
        for (int i = 0; i<stepName.length();i++){
            if (stepName.charAt(i) == ' ') spaceCount = spaceCount + 1;
        }
        for (int i = 0; i<spaceCount; i++){
            String currWord = tempName.substring(0, tempName.indexOf(' '));
            for (int j = 0;j<MyConstants.CONNECTOR_WORDS.length;j++){
                if (currWord.equals(MyConstants.CONNECTOR_WORDS[j])){
                   stepName = removeFirstWord(stepName, currWord);
                }
            }
            tempName = tempName.substring(tempName.indexOf(' ') + 1);
        }

        return stepName;
    }

    public static String removeFirstWord(String input, String word) {
        int index = input.indexOf(word);
        if (index != -1) {
            String before = input.substring(0, index).trim();
            String after = input.substring(index + word.length()).trim();
            return before + " " + after;
        }
        return input;
    }

    public void nextStep(View view){
        if (stepIsLast(currStepNumber)){
            Toast.makeText(this, "FINISHED", Toast.LENGTH_LONG).show();
        }
        else{
            currStepNumber+=1;
            setStepDetails(currStepNumber);

        }
        formatStepName(recipe.getSteps().get(currStepNumber).getName());

    }
}