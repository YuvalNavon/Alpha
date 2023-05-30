package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class CreateRecipeGeneral extends AppCompatActivity {

    //From This
    EditText recipeNameET, recipeDescriptionET;
    ImageView nextBTN;
    Button editBTN;
    String recipeName, recipeDescription;

    FirebaseDatabase FBDB;
    DatabaseReference refUser;
    ValueEventListener madeCommunityLessonsGetter;

    int originalLessonNumber; //For beating the madeRecipeBefore Allegations in saveEdit


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truly_final_create_recipe_general);

        recipeNameET = findViewById(R.id.CR_recipeNameET);
        recipeDescriptionET = findViewById(R.id.CR_recipeDescriptionET);
        nextBTN = findViewById(R.id.CR_General_NextBTN);
        editBTN = findViewById(R.id.CR_General_EditBTN);

        Intent gi = getIntent();
        if (gi.getStringExtra("Previous Activity")!=null && gi.getStringExtra("Previous Activity").equals(MyConstants.FROM_PROFILE_AKA_EDIT_MODE))
        {
                nextBTN.setVisibility(View.GONE);

                SharedPreferences settings=this.getSharedPreferences("PREFS_NAME",MODE_PRIVATE);

                recipeName = settings.getString(MyConstants.CUSTOM_RECIPE_NAME, null);
                recipeDescription = settings.getString(MyConstants.CUSTOM_RECIPE_DESCRIPTION, null);
                originalLessonNumber = settings.getInt(MyConstants.ORIGINAL_CUSTOM_RECIPE_NUMBER, MyConstants.NO_COMMUNITY_LESSON_NUMBER_ERROR);

                recipeNameET.setText(recipeName);
                recipeDescriptionET.setText(recipeDescription);
        }

        else //Normal Creating Recipe Process
        {
            editBTN.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        if (refUser!=null && madeCommunityLessonsGetter !=null) refUser.removeEventListener(madeCommunityLessonsGetter);


    }



    public void onDestroy() {

        super.onDestroy();
        if (refUser!=null && madeCommunityLessonsGetter !=null) refUser.removeEventListener(madeCommunityLessonsGetter);


    }




    public boolean recipeNameHasComma(String recipeName) {
        return recipeName.contains(",");
    }

    public String removeLeadingTrailingSpaces(String input) {
        if (input == null) {
            return null;
        }
        int start = 0;
        int end = input.length() - 1;

        // find the first non-space character from the start
        while (start < input.length() && input.charAt(start) == ' ') {
            start++;
        }

        // find the last non-space character from the end
        while (end >= 0 && input.charAt(end) == ' ') {
            end--;
        }

        // return the substring without leading or trailing spaces
        return input.substring(start, end + 1);
    }

    public void next(View view){

        recipeName = recipeNameET.getText().toString();
        recipeName = removeLeadingTrailingSpaces(recipeName);
        recipeDescription = recipeDescriptionET.getText().toString();

        if (!recipeName.isEmpty() && !recipeDescription.isEmpty() && !recipeNameHasComma(recipeName))
        {

            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            FirebaseUser currentlyLoggedInUser = fAuth.getCurrentUser();

            FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
            refUser=FBDB.getReference("Users").child(currentlyLoggedInUser.getUid());
            madeCommunityLessonsGetter = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User currentUser = snapshot.getValue(User.class);


                    ArrayList<String> madeCommunityLessons = currentUser.getUploadedRecipeNames();
                    ArrayList<Integer> madeCommunityLessonsStatuses = currentUser.getUploadedRecipeStatuses();
                    boolean madeRecipeBefore = false;
                    for (int i = 0; i<madeCommunityLessons.size();i++)
                    {
                        if (madeCommunityLessons.get(i).toLowerCase(Locale.ROOT).equals(recipeName.toLowerCase(Locale.ROOT))
                        && madeCommunityLessonsStatuses.get(i)==1)
                        {
                            madeRecipeBefore = true;

                        }
                    }


                    if (madeRecipeBefore)
                    {
                        AlertDialog.Builder alreadyMadeRecipeBuilder = new AlertDialog.Builder(CreateRecipeGeneral.this);

                        alreadyMadeRecipeBuilder.setTitle("You Made a Recipe like this Before");
                        alreadyMadeRecipeBuilder.setMessage("Would you like to Edit your Previous Recipe with this Name?\nYou can also Choose a Different Name for This Recipe.");


                        alreadyMadeRecipeBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });


                        alreadyMadeRecipeBuilder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                //to UploadedCommunityLessonsList Activity
                                Intent toUploadedCommunityLessonsList = new Intent(CreateRecipeGeneral.this, moreDetailsLists.class);
                                toUploadedCommunityLessonsList.putExtra("View Mode", "Your Recipes");
                                startActivity(toUploadedCommunityLessonsList);
                                finish();

                            }
                        });

                        AlertDialog alreadyMadeRecipeDialog = alreadyMadeRecipeBuilder.create();
                        alreadyMadeRecipeDialog.show();
                    }

                    else
                    {
                        Intent toAddRecipeImage = new Intent(CreateRecipeGeneral.this, CreateRecipeImage.class);
                        toAddRecipeImage.putExtra("Previous Activity", MyConstants.NOT_FROM_FINISH_SCREEN);

                        //From This
                        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                        SharedPreferences.Editor editor=settings.edit();
                        editor.putString(MyConstants.CUSTOM_RECIPE_NAME, recipeName);
                        editor.putString(MyConstants.CUSTOM_RECIPE_DESCRIPTION, recipeDescription);
                        editor.commit();


                        startActivity(toAddRecipeImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            refUser.addListenerForSingleValueEvent(madeCommunityLessonsGetter);


        }

        else{
            if (recipeName.isEmpty())
            {
                Toast.makeText(this, "Please Enter a Name for this Recipe!", Toast.LENGTH_SHORT).show();
            }

            else if (recipeDescription.isEmpty())
            {
                Toast.makeText(this, "Please Enter a Description for this Recipe!", Toast.LENGTH_SHORT).show();
            }


            else if (recipeNameHasComma(recipeName))
            {
                Toast.makeText(this, "The Recipe's Name can't have ',' in it.", Toast.LENGTH_SHORT).show();
                //better to use Alert Dialog for this one
            }
        }



    }

    public void saveEdit(View view)
    {

        recipeName = recipeNameET.getText().toString();
        recipeName = removeLeadingTrailingSpaces(recipeName);
        recipeDescription = recipeDescriptionET.getText().toString();

        if (!recipeName.isEmpty() && !recipeDescription.isEmpty() && !recipeNameHasComma(recipeName))
        {

            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            FirebaseUser currentlyLoggedInUser = fAuth.getCurrentUser();

            FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
            refUser=FBDB.getReference("Users").child(currentlyLoggedInUser.getUid());
            madeCommunityLessonsGetter = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User currentUser = snapshot.getValue(User.class);

                    ArrayList<String> madeCommunityLessons = currentUser.getUploadedRecipeNames();
                    ArrayList<Integer> madeCommunityLessonsStatuses = currentUser.getUploadedRecipeStatuses();
                    boolean madeRecipeBefore = false;
                    for (int i = 0; i<madeCommunityLessons.size();i++)
                    {
                        if (madeCommunityLessons.get(i).toLowerCase(Locale.ROOT).equals(recipeName.toLowerCase(Locale.ROOT))
                                && madeCommunityLessonsStatuses.get(i)==1 && originalLessonNumber!=i) //LAST AND IS THE GAME CHANGER
                        {
                            madeRecipeBefore = true;

                        }
                    }


                    if (madeRecipeBefore)
                    {
                        AlertDialog.Builder alreadyMadeRecipeBuilder = new AlertDialog.Builder(CreateRecipeGeneral.this);

                        alreadyMadeRecipeBuilder.setTitle("You Made a Recipe like this Before");
                        alreadyMadeRecipeBuilder.setMessage("Would you like to Edit your Previous Recipe with this Name?\nYou can also Choose a Different Name for This Recipe.");


                        alreadyMadeRecipeBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });


                        alreadyMadeRecipeBuilder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                //to UploadedCommunityLessonsList Activity
                                Intent toUploadedCommunityLessonsList = new Intent(CreateRecipeGeneral.this, moreDetailsLists.class);
                                toUploadedCommunityLessonsList.putExtra("View Mode", "Your Recipes");
                                startActivity(toUploadedCommunityLessonsList);
                                finish();

                            }
                        });

                        AlertDialog alreadyMadeRecipeDialog = alreadyMadeRecipeBuilder.create();
                        alreadyMadeRecipeDialog.show();
                    }

                    else
                    {
                        Intent backToFinish = new Intent(CreateRecipeGeneral.this, CreateRecipeFinishScreen.class);
                        backToFinish.putExtra("Previous Activity", MyConstants.FROM_PROFILE_AKA_EDIT_MODE);

                        //From This
                        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                        SharedPreferences.Editor editor=settings.edit();
                        editor.putString(MyConstants.CUSTOM_RECIPE_NAME, recipeName);
                        editor.putString(MyConstants.CUSTOM_RECIPE_DESCRIPTION, recipeDescription);
                        editor.commit();

                        backToFinish.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(backToFinish);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            refUser.addListenerForSingleValueEvent(madeCommunityLessonsGetter);


        }

        else{
            if (recipeName.isEmpty())
            {
                Toast.makeText(this, "Please Enter a Name for this Recipe!", Toast.LENGTH_SHORT).show();
            }

            else if (recipeDescription.isEmpty())
            {
                Toast.makeText(this, "Please Enter a Description for this Recipe!", Toast.LENGTH_SHORT).show();
            }


            else if (recipeNameHasComma(recipeName))
            {
                Toast.makeText(this, "The Recipe's Name can't have ',' in it.", Toast.LENGTH_SHORT).show();
                //better to use Alert Dialog for this one
            }
        }


    }
}