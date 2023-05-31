/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Activity is where the user can view the full details of a picked Step in the CommunityLesson
 * they are writing/editing/about to publish/about to start.
 * if the Activity is started when the user is writing/editing a CommunityLesson, than the user can
 * also edit the picked Step's details.
 */


package com.example.lifesworkiguess;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SingleStepScreen extends AppCompatActivity {

    TextView stepNumberTV;
    EditText  stepNameET, stepDescriptionET, stepTimeET;
    ImageView stepImageIV;
    Button saveBTN, resetBTN;

    int stepNumber;
    String originalStepName, originalStepDescription, originalStepTime;

    String mode; //Can only Contain "From Finish"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_step_screen);

        stepNumberTV = findViewById(R.id.SingleStep_stepNumberTV);
        stepNameET = findViewById(R.id.SingleStep_StepNameET);
        stepDescriptionET = findViewById(R.id.SingleStep_stepDescriptionET);
        stepTimeET = findViewById(R.id.SingleStep_stepTimeET);
        stepImageIV = findViewById(R.id.SingleStep_stepImageIV);

        saveBTN = findViewById(R.id.SingleStep_SaveChangesBTN);


        resetBTN = findViewById(R.id.SingleStep_ResetChangesBTN);
        resetBTN.setVisibility(View.GONE);

        Intent fromCreatingRecipe = getIntent();

        stepNumber = fromCreatingRecipe.getIntExtra("Step Number", 0);
        originalStepName = fromCreatingRecipe.getStringExtra("Step Name");
        originalStepDescription = fromCreatingRecipe.getStringExtra("Step Description");
        originalStepTime = fromCreatingRecipe.getStringExtra("Step Time");

        //Currently No Implementation of Images/GIFS for Custom Recipes Steps

        stepNumberTV.setText("Step Number: " + (stepNumber + 1));
        stepNameET.setText(originalStepName);
        stepDescriptionET.setText(originalStepDescription);
        stepTimeET.setText(originalStepTime);


        stepNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                resetBTN.setVisibility(View.VISIBLE);

            }
        });

        stepDescriptionET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                resetBTN.setVisibility(View.VISIBLE);

            }
        });

        stepTimeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                resetBTN.setVisibility(View.VISIBLE);

            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button press event here
                //Setting up Alert Dialogs

                String changedStepName = stepNameET.getText().toString();
                String changedStepDescription = stepDescriptionET.getText().toString();
                String changedStepTime = stepTimeET.getText().toString();

                if (changedStepName.equals(originalStepName) && changedStepDescription.equals(originalStepDescription) &&changedStepTime.equals(originalStepTime))
                {
                    finish();
                }

                else
                {

                    AlertDialog.Builder saveStepChangesDialogBuilder = new AlertDialog.Builder(SingleStepScreen.this);

                    saveStepChangesDialogBuilder.setTitle("Before Going Back...");
                    saveStepChangesDialogBuilder.setMessage("Would you like to Save your Changes?");

                    saveStepChangesDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Handle click here
                            finish();
                        }
                    });

                    saveStepChangesDialogBuilder.setNeutralButton("Continue Editing", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    saveStepChangesDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            saveChanges();
                        }
                    });

                    // Create and show the AlertDialog
                    AlertDialog saveStepChangesDialog = saveStepChangesDialogBuilder.create();
                    saveStepChangesDialog.show();
                }

            }
        });

        mode = fromCreatingRecipe.getStringExtra("Origin");
        if (mode!=null && mode.equals("From Finish"))
        {
            stepNameET.setEnabled(false);
            stepDescriptionET.setEnabled(false);
            stepTimeET.setEnabled(false);
            saveBTN.setText("Back");
        }
    }



    /**
     * if the User edited the picked Step, the function makes sure they're valid,and if they are,
     * the function saves the changed Step's details in Shared Preferences, and closes the activity.
     * otherwise, the function alerts the user that their input is invalid.
     * if the user didn't edit the picked step, the function closes the activity.
     *
     *
     * @param
     *
     *
     * @return
     */
    public void saveChanges(){

        String changedStepName = stepNameET.getText().toString();
        String changedStepDescription = stepDescriptionET.getText().toString();
        String changedStepTime = stepTimeET.getText().toString();

        if (changedStepName.equals(originalStepName) && changedStepDescription.equals(originalStepDescription) &&changedStepTime.equals(originalStepTime))
        {
            finish();
        }

        else
        {
            if (!changedStepName.isEmpty() && !changedStepDescription.isEmpty() && !changedStepTime.isEmpty())
            {

                AlertDialog.Builder saveStepChangesDialogBuilder = new AlertDialog.Builder(SingleStepScreen.this);

                saveStepChangesDialogBuilder.setTitle("Are You Sure?");
                saveStepChangesDialogBuilder.setMessage("Would you like to Save your Changes?");

                saveStepChangesDialogBuilder.setNegativeButton("Not Yet", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Handle click here
                    }
                });


                saveStepChangesDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                        SharedPreferences.Editor editor=settings.edit();
                        editor.putInt(MyConstants.STEP_NUMBER_KEY, stepNumber );
                        editor.putString(MyConstants.STEP_NAME_KEY, changedStepName);
                        editor.putString(MyConstants.STEP_DESCRIPTION_KEY, changedStepDescription);
                        editor.putString(MyConstants.STEP_TIME_KEY, changedStepTime);
                        editor.commit();
                        finish();
                    }
                });

                // Create and show the AlertDialog
                AlertDialog exitLessonDialog = saveStepChangesDialogBuilder.create();
                exitLessonDialog.show();


            }

            else
            {
                Toast.makeText(this, "Please fill all Fields!", Toast.LENGTH_SHORT).show();
            }

        }



    }


    /**
     * this function either closes the activity or calls the saveChanges() method, depending on the
     * context that the user is viewing this screen from.
     * @param view - the button pressed.
     *
     *
     * @return
     */
    public void clickedSave(View view){
        if (mode!=null &&  mode.equals("From Finish"))
        {
            finish();
        }
        else
        {
            saveChanges();

        }
    }

    /**
     * this function resets the EditText Fields of the Step's details to their original values.
     * @param view - the button pressed.
     *
     *
     * @return
     */
    public void resetChanges(View view){

        AlertDialog.Builder resetStepChangesDialogBuilder = new AlertDialog.Builder(SingleStepScreen.this);

        resetStepChangesDialogBuilder.setTitle("Resetting Changes");
        resetStepChangesDialogBuilder.setMessage("Would you like to Reset your Changes?");

        resetStepChangesDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Handle click here
            }
        });


        resetStepChangesDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                stepNameET.setText(originalStepName);
                stepDescriptionET.setText(originalStepDescription);
                stepTimeET.setText(originalStepTime);


                SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                SharedPreferences.Editor editor=settings.edit();
                editor.putInt(MyConstants.STEP_NUMBER_KEY, -999 );
                editor.putString(MyConstants.STEP_NAME_KEY, null);
                editor.putString(MyConstants.STEP_DESCRIPTION_KEY, null);
                editor.putString(MyConstants.STEP_TIME_KEY, null);
                editor.commit();

            }
        });

        // Create and show the AlertDialog
        AlertDialog resetStepChangesDialog = resetStepChangesDialogBuilder.create();
        resetStepChangesDialog.show();


    }
}