/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Activity is where the user can put the Extra Info for the Recipe/CommunityLesson they're writing/editing.
 * The Extra Info is the Time it takes to make the Recipe, How hard the Recipe is, How many people the Recipe is for, and whether the Recipe is Kosher or not.
 */

package com.example.lifesworkiguess;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateRecipeExtraInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {




    //This
    //We gotta implement scroll bars for the spinners!!
    Spinner recipeTimeHoursSpinner, recipeTimeMinutesSpinner;
    String  finalRecipeTime;
    int recipeTimeHours, recipeTimeMinutes;
    int currHoursPos, currMinutesPos; //for saving current input when closing

    Button easyBTN, standardBTN, hardBTN;
    String recipeDifficultyLevel;

    EditText serveCountET;
    int recipeServeCount;

    CheckBox recipeKosherCB;
    boolean recipeKosher;

    Button editBTN;
    ImageView nextBTN, backBTN;
    TextView finishMessageTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truly_final_create_recipe_extra_info);

        editBTN = findViewById(R.id.CR_ExtraInfo_EditBTN);
        nextBTN = findViewById(R.id.CR_ExtraInfo_NextBTN);
        backBTN = findViewById(R.id.CR_ExtraInfo_BackBTN);
        finishMessageTV = findViewById(R.id.CR_ExtraInfo_FinishTV);

        currHoursPos = 0;
        currMinutesPos = 0;
        recipeTimeHours = 0;
        recipeTimeMinutes = 0;
        finalRecipeTime = MyConstants.CUSTOM_RECIPE_NO_TIME_INPUTTED; //for Input Checking, if the user hasn't picked a time

        //Adapters, GestureDetectors and Custom Item Layout for Spinner is from ChatGPT

        recipeTimeHoursSpinner = findViewById(R.id.CR_TimeSpinnerHours);
        recipeTimeMinutesSpinner = findViewById(R.id.CR_TimeSpinnerMinutes);
        makeHoursSpinner();
        makeMinutesSpinner();

        easyBTN = findViewById(R.id.CR_Easy);
        standardBTN = findViewById(R.id.CR_Standard);
        hardBTN = findViewById(R.id.CR_Hard);

        serveCountET = findViewById(R.id.CR_ServeCountET); //Recipe Serve Count is saved whenever the user exits the activity, or presses next


        recipeKosher = false; //not needed bc def value for getting this out of shared pref is false anyway but still its a nice failsafe
        recipeKosherCB = findViewById(R.id.CR_KosherCheckBox);
        recipeKosherCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recipeKosher = true;
                } else {
                    recipeKosher = false;

                }
            }
        });





        Intent gi = getIntent();

        //We check if the user got to this activity from the finish screen or from the activity before this one

        if (gi.getStringExtra("Previous Activity")!=null && gi.getStringExtra("Previous Activity").equals(MyConstants.FROM_FINISH_SCREEN)){
            //Right now I do not allow users to edit by pressing on items from the finish screen BEFORE UPLOADING THE RECIPE ITSELF, so  this will remain empty for now
            //I can do this SO SO SO EASILY but design Wise I think its useless and confusing
        }

        else if (gi.getStringExtra("Previous Activity")!=null && gi.getStringExtra("Previous Activity").equals(MyConstants.FROM_PROFILE_AKA_EDIT_MODE))
        { //User is editing uploaded Recipe

            nextBTN.setVisibility(View.GONE);
            backBTN.setVisibility(View.GONE);
            finishMessageTV.setVisibility(View.GONE);

            SharedPreferences settings=this.getSharedPreferences("PREFS_NAME",MODE_PRIVATE);

            recipeKosher = settings.getBoolean(MyConstants.CUSTOM_RECIPE_KOSHER, false);
            recipeKosherCB.setChecked(recipeKosher);

            recipeDifficultyLevel = settings.getString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL, null);
            if (recipeDifficultyLevel !=null){
                if (recipeDifficultyLevel.equals(MyConstants.CUSTOM_RECIPE_DIFFICULTY_EASY)){

                    difficultyPicked(easyBTN);

                }

                else if (recipeDifficultyLevel.equals(MyConstants.CUSTOM_RECIPE_DIFFICULTY_STANDARD)){

                    difficultyPicked(standardBTN);

                }

                else if (recipeDifficultyLevel.equals(MyConstants.CUSTOM_RECIPE_DIFFICULTY_HARD)){

                    difficultyPicked(hardBTN);

                }
            }
            else //default value for difficulty is standard, if user didnt pick something else
            {
                difficultyPicked(standardBTN);

            }

            recipeServeCount = settings.getInt(MyConstants.CUSTOM_RECIPE_SERVE_COUNT, MyConstants.NO_SERVE_COUNT_ERROR);
            if (recipeServeCount!=0) serveCountET.setText(Integer.toString(recipeServeCount));
        }

        else //Normal Creating Recipe Process
        {
            editBTN.setVisibility(View.GONE);
            //getting saved extra info
            SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);

            currHoursPos = settings.getInt(MyConstants.CUSTOM_RECIPE_HOURS_SPINNER_CURR_POS, MyConstants.CUSTOM_RECIPE_NO_SPINNER_POS_SAVED);
            if (currHoursPos!=MyConstants.CUSTOM_RECIPE_NO_SPINNER_POS_SAVED){
                recipeTimeHoursSpinner.setSelection(currHoursPos);
                onItemSelected(recipeTimeHoursSpinner, recipeTimeHoursSpinner.getSelectedView(), currHoursPos, 0); //id is irrelevant

            }

            currMinutesPos = settings.getInt(MyConstants.CUSTOM_RECIPE_MINUTES_SPINNER_CURR_POS, MyConstants.CUSTOM_RECIPE_NO_SPINNER_POS_SAVED);
            if (currMinutesPos!=MyConstants.CUSTOM_RECIPE_NO_SPINNER_POS_SAVED){
                recipeTimeMinutesSpinner.setSelection(currMinutesPos);
                onItemSelected(recipeTimeMinutesSpinner, recipeTimeMinutesSpinner.getSelectedView(), currMinutesPos, 0); //id is irrelevant

            }

            recipeDifficultyLevel = settings.getString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL, null);
            if (recipeDifficultyLevel !=null){
                if (recipeDifficultyLevel.equals(MyConstants.CUSTOM_RECIPE_DIFFICULTY_EASY)){

                    difficultyPicked(easyBTN);

                }

                else if (recipeDifficultyLevel.equals(MyConstants.CUSTOM_RECIPE_DIFFICULTY_STANDARD)){

                    difficultyPicked(standardBTN);

                }

                else if (recipeDifficultyLevel.equals(MyConstants.CUSTOM_RECIPE_DIFFICULTY_HARD)){

                    difficultyPicked(hardBTN);

                }
            }
            else //default value for difficulty is standard, if user didnt pick something else
            {
                difficultyPicked(standardBTN);

            }

            recipeServeCount = settings.getInt(MyConstants.CUSTOM_RECIPE_SERVE_COUNT, 0);
            if (recipeServeCount!=0) serveCountET.setText(Integer.toString(recipeServeCount));

            recipeKosher = settings.getBoolean(MyConstants.CUSTOM_RECIPE_KOSHER, false);
            recipeKosherCB.setChecked(recipeKosher);
        }



    }

    public void onDestroy() {

        super.onDestroy();

        Intent gi = getIntent();
        if (gi.getStringExtra("Previous Activity")==null ||
                (gi.getStringExtra("Previous Activity")!=null &&  !gi.getStringExtra("Previous Activity").equals(MyConstants.FROM_PROFILE_AKA_EDIT_MODE)))
        {//WE ONLY SAVE WHEN CLOSED WHEN WRITING A NEW RECIPE, IF YOU EDIT AN UPLOADED ONE THEN THE ONLY WAY TO SAVE IS VIA SAVEEDIT
            //The extra info is deleted when the user finishes the recipe, either by uploading it or by going back to the community screen
            saveCurrentExtraInfo();

        }

    }

    /**
     * this function saves all the inputted Extra Info into SharedPreferences
     * <p>
     *
     * @param
     *
     *
     * @return	None
     */
    public void saveCurrentExtraInfo(){
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        editor.putInt(MyConstants.CUSTOM_RECIPE_HOURS_SPINNER_CURR_POS, currHoursPos);
        editor.putInt(MyConstants.CUSTOM_RECIPE_MINUTES_SPINNER_CURR_POS, currMinutesPos);
        editor.putString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL, recipeDifficultyLevel);
        if (!serveCountET.getText().toString().isEmpty()) recipeServeCount = Integer.parseInt(serveCountET.getText().toString());
        editor.putInt(MyConstants.CUSTOM_RECIPE_SERVE_COUNT, recipeServeCount);
        editor.putBoolean(MyConstants.CUSTOM_RECIPE_KOSHER, recipeKosher);
        editor.commit();
    }


    /**
     * this function sets up the Spinner for the Hours Options for the Recipe.
     * <p>
     *
     * @param
     *
     *
     * @return	None
     */
    public void makeHoursSpinner(){

        ArrayAdapter<String> adpTimeHours = new ArrayAdapter<String>(this, R.layout.extra_info_spinner_items,R.id.extra_info_spinner_curr, MyConstants.CUSTOM_RECIPE_TIME_HOUR_OPTIONS_FOR_SPINNER) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);



                View selectedViewHours = recipeTimeHoursSpinner.getSelectedView();
                TextView selectedItemTextView = view.findViewById(R.id.extra_info_spinner_curr);
                TextView previousItemTextView = view.findViewById(R.id.extra_info_spinner_prev);
                TextView nextItemTextView = view.findViewById(R.id.extra_info_spinner_next);

                previousItemTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedItemPosition = recipeTimeHoursSpinner.getSelectedItemPosition();
                        recipeTimeHoursSpinner.setSelection(selectedItemPosition - 1);
                        onItemSelected(recipeTimeHoursSpinner, selectedViewHours, selectedItemPosition - 1,0); //id is irrelevant

                    }
                });

                nextItemTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedItemPosition = recipeTimeHoursSpinner.getSelectedItemPosition();
                        recipeTimeHoursSpinner.setSelection(recipeTimeHoursSpinner.getSelectedItemPosition() + 1);
                        onItemSelected(recipeTimeHoursSpinner, selectedViewHours, selectedItemPosition + 1, 0); //id is irrelevant
                    }
                });




                selectedItemTextView.setText(getItem(position));

                if (position > 0) {
                    previousItemTextView.setVisibility(View.VISIBLE);
                    previousItemTextView.setText(getItem(position - 1));
                } else {
                    previousItemTextView.setVisibility(View.INVISIBLE);
                    previousItemTextView.setText("");
                }

                if (position < getCount() - 1) {
                    nextItemTextView.setVisibility(View.VISIBLE);
                    nextItemTextView.setText(getItem(position + 1));
                } else {
                    nextItemTextView.setVisibility(View.INVISIBLE);
                    nextItemTextView.setText("");
                }

                return view;
            }
        };

        recipeTimeHoursSpinner.setAdapter(adpTimeHours);

        GestureDetector gestureDetectorHours = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 10;
            private static final int SWIPE_VELOCITY_THRESHOLD = 10;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {


                View selectedView = recipeTimeHoursSpinner.getSelectedView();
                TextView selectedItemTextView = selectedView.findViewById(R.id.extra_info_spinner_curr);
                TextView previousItemTextView = selectedView.findViewById(R.id.extra_info_spinner_prev);
                TextView nextItemTextView = selectedView.findViewById(R.id.extra_info_spinner_next);

                float deltaY = e2.getY() - e1.getY();
                if (Math.abs(deltaY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (deltaY < 0) {
                        // Swipe up (next item)
                        int selectedItemPosition = recipeTimeHoursSpinner.getSelectedItemPosition();
                        if (selectedItemPosition < recipeTimeHoursSpinner.getCount() - 1) {
                            animateTextView(previousItemTextView, 0.0f, -10.0f);
                            animateTextView(selectedItemTextView, 0.0f, 0.0f);
                            animateTextView(nextItemTextView, -200.0f, 10.0f);
                            recipeTimeHoursSpinner.setSelection(selectedItemPosition + 1);
                            onItemSelected(recipeTimeHoursSpinner, selectedView, selectedItemPosition + 1, adpTimeHours.getItemId(selectedItemPosition + 1));
                        }
                    } else {
                        // Swipe down (previous item)


                        int selectedItemPosition = recipeTimeHoursSpinner.getSelectedItemPosition();
                        if (selectedItemPosition > 0) {
                            animateTextView(previousItemTextView, -200.0f, -10.0f);
                            animateTextView(selectedItemTextView, 0.0f, 0.0f);
                            animateTextView(nextItemTextView, 0.0f, 10.0f);
                            recipeTimeHoursSpinner.setSelection(selectedItemPosition - 1);
                            onItemSelected(recipeTimeHoursSpinner, selectedView, selectedItemPosition - 1, adpTimeHours.getItemId(selectedItemPosition - 1));
                        }
                    }


                    return true;
                }
                return false;
            }

            private void animateTextView(TextView textView, float translationDistance, float rotationAngle) {
                ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(textView, "translationY", translationDistance);
                ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(textView, "rotation", rotationAngle);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(translationAnimator, rotationAnimator);
                animatorSet.setDuration(200);
                animatorSet.start();
            }
        });


        // Set the touch listener for the Spinner view
        recipeTimeHoursSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetectorHours.onTouchEvent(motionEvent);
            }
        });
    }

    /**
     * this function sets up the Spinner for the Minutes Options for the Recipe.
     * <p>
     *
     * @param
     *
     *
     * @return	None
     */
    public void makeMinutesSpinner(){

        ArrayAdapter<String> adpTimeMinutes = new ArrayAdapter<String>(this, R.layout.extra_info_spinner_items,R.id.extra_info_spinner_curr, MyConstants.CUSTOM_RECIPE_TIME_MINUTE_OPTIONS_FOR_SPINNER) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                View selectedViewHours = recipeTimeMinutesSpinner.getSelectedView();
                TextView selectedItemTextView = view.findViewById(R.id.extra_info_spinner_curr);
                TextView previousItemTextView = view.findViewById(R.id.extra_info_spinner_prev);
                TextView nextItemTextView = view.findViewById(R.id.extra_info_spinner_next);

                previousItemTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedItemPosition = recipeTimeMinutesSpinner.getSelectedItemPosition();
                        recipeTimeMinutesSpinner.setSelection(selectedItemPosition - 1);
                        onItemSelected(recipeTimeMinutesSpinner, selectedViewHours, selectedItemPosition - 1,0); //id is irrelevant

                    }
                });

                nextItemTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedItemPosition = recipeTimeMinutesSpinner.getSelectedItemPosition();
                        recipeTimeMinutesSpinner.setSelection(recipeTimeMinutesSpinner.getSelectedItemPosition() + 1);
                        onItemSelected(recipeTimeMinutesSpinner, selectedViewHours, selectedItemPosition + 1, 0); //id is irrelevant
                    }
                });



                selectedItemTextView.setText(getItem(position));

                if (position > 0) {
                    previousItemTextView.setVisibility(View.VISIBLE);
                    previousItemTextView.setText(getItem(position - 1));
                } else {
                    previousItemTextView.setVisibility(View.INVISIBLE);
                    previousItemTextView.setText("");
                }

                if (position < getCount() - 1) {
                    nextItemTextView.setVisibility(View.VISIBLE);
                    nextItemTextView.setText(getItem(position + 1));
                } else {
                    nextItemTextView.setVisibility(View.INVISIBLE);
                    nextItemTextView.setText("");
                }

                return view;
            }
        };

        recipeTimeMinutesSpinner.setAdapter(adpTimeMinutes);

        GestureDetector gestureDetectorMinutes = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 10;
            private static final int SWIPE_VELOCITY_THRESHOLD = 10;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                View selectedView = recipeTimeMinutesSpinner.getSelectedView();
                TextView selectedItemTextView = selectedView.findViewById(R.id.extra_info_spinner_curr);
                TextView previousItemTextView = selectedView.findViewById(R.id.extra_info_spinner_prev);
                TextView nextItemTextView = selectedView.findViewById(R.id.extra_info_spinner_next);


                float deltaY = e2.getY() - e1.getY();
                if (Math.abs(deltaY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (deltaY < 0) {
                        // Swipe up (next item)
                        int selectedItemPosition = recipeTimeMinutesSpinner.getSelectedItemPosition();
                        if (selectedItemPosition < recipeTimeMinutesSpinner.getCount() - 1) {
                            animateTextView(previousItemTextView, 0.0f, -10.0f);
                            animateTextView(selectedItemTextView, 0.0f, 0.0f);
                            animateTextView(nextItemTextView, -200.0f, 10.0f);
                            recipeTimeMinutesSpinner.setSelection(selectedItemPosition + 1);
                            onItemSelected(recipeTimeMinutesSpinner, selectedView, selectedItemPosition + 1, adpTimeMinutes.getItemId(selectedItemPosition + 1));
                        }
                    } else {
                        // Swipe down (previous item)


                        int selectedItemPosition = recipeTimeMinutesSpinner.getSelectedItemPosition();
                        if (selectedItemPosition > 0) {
                            animateTextView(previousItemTextView, -200.0f, -10.0f);
                            animateTextView(selectedItemTextView, 0.0f, 0.0f);
                            animateTextView(nextItemTextView, 0.0f, 10.0f);
                            recipeTimeMinutesSpinner.setSelection(selectedItemPosition - 1);
                            onItemSelected(recipeTimeMinutesSpinner, selectedView, selectedItemPosition - 1, adpTimeMinutes.getItemId(selectedItemPosition - 1));

                        }
                    }


                    return true;
                }
                return false;
            }

            private void animateTextView(TextView textView, float translationDistance, float rotationAngle) {
                ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(textView, "translationY", translationDistance);
                ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(textView, "rotation", rotationAngle);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(translationAnimator, rotationAnimator);
                animatorSet.setDuration(200);
                animatorSet.start();
            }
        });


        // Set the touch listener for the Spinner view
        recipeTimeMinutesSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetectorMinutes.onTouchEvent(motionEvent);
            }
        });
    }


    /**
     * this function sets The String that contains the Time it takes to make the Recipe in finalRecipeTime
     * <p>
     *
     * @param hours - the hours inputted.
     *        minutes - the minutes inputted.
     *
     *
     * @return	None
     */
    public void makeFinalRecipeTime(int hours, int minutes){
        if (hours != 0 && minutes != 0){
            if (hours==1){
                finalRecipeTime = Integer.toString(hours) + " Hour " + Integer.toString(minutes) + " min.";

            }
            else if (hours!=1){
                finalRecipeTime = Integer.toString(hours) + " Hours " + Integer.toString(minutes) + " min.";

            }

        }

        else if (hours != 0 && minutes == 0){
            if (hours==1){
                finalRecipeTime = Integer.toString(hours) + " Hour " ;

            }
            else if (hours!=1){
                finalRecipeTime = Integer.toString(hours) + " Hours ";

            }
        }

        else if (hours == 0 && minutes !=0){
            finalRecipeTime =  Integer.toString(minutes) + " min.";
        }

        else if (hours == 0 && minutes ==0){
            finalRecipeTime = MyConstants.CUSTOM_RECIPE_NO_TIME_INPUTTED; //for Input Checking, if the user hasn't picked a time.
            //Never mind, this method isnt called if the users doesnt pick a time, so for this condition to be met, the user has to change the time
        }

    }



    /**
     * this function sets The Difficulty Level for the Recipe, bases on the button pressed.
     * <p>
     *
     * @param view - the button pressed.
     *
     *
     *
     * @return	None
     */
    public void difficultyPicked(View view){


        int colorOrange = getResources().getColor(R.color.orange);
        int colorLightBlue = getResources().getColor(R.color.lightBlue);


        if (view.getId()==easyBTN.getId()){

            easyBTN.setBackgroundColor(colorOrange);
            standardBTN.setBackgroundColor(colorLightBlue);
            hardBTN.setBackgroundColor(colorLightBlue);
            recipeDifficultyLevel = MyConstants.CUSTOM_RECIPE_DIFFICULTY_EASY;

        }
        else if (view.getId()==standardBTN.getId()){

            easyBTN.setBackgroundColor(colorLightBlue);
            standardBTN.setBackgroundColor(colorOrange);
            hardBTN.setBackgroundColor(colorLightBlue);
            recipeDifficultyLevel = MyConstants.CUSTOM_RECIPE_DIFFICULTY_STANDARD;

        }

        else  if (view.getId()==hardBTN.getId()){

            easyBTN.setBackgroundColor(colorLightBlue);
            standardBTN.setBackgroundColor(colorLightBlue);
            hardBTN.setBackgroundColor(colorOrange);
            recipeDifficultyLevel = MyConstants.CUSTOM_RECIPE_DIFFICULTY_HARD;

        }
    }



    /**
     * this function checks that all the inputted info is valid.
     * if so, the function saves the info in Shared Preferences and starts the CreateRecipeFinishScreen Activity.
     * if not, the function alerts the user bases on the info that isn't valid.
     * <p>
     *
     * @param view - the button pressed.
     *
     *
     *
     * @return	None
     */
    public void next(View view){

        //Checking For Valid Input
        if (!serveCountET.getText().toString().isEmpty()) recipeServeCount = Integer.parseInt(serveCountET.getText().toString());

        if (recipeServeCount!= 0 && (currHoursPos>0 || currMinutesPos>0)){ //Intentionally wrote ">0" and not "!=0" bc pos Values can be -999 (from MyConstants)
            Intent toAddRecipeFinish = new Intent(this, CreateRecipeFinishScreen.class);
            toAddRecipeFinish.putExtra("Previous Activity", MyConstants.NOT_FROM_FINISH_SCREEN);

            SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
            SharedPreferences.Editor editor=settings.edit();
            makeFinalRecipeTime(recipeTimeHours, recipeTimeMinutes);
            editor.putString(MyConstants.CUSTOM_RECIPE_TIME, finalRecipeTime);
            editor.putString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL, recipeDifficultyLevel);
            editor.putInt(MyConstants.CUSTOM_RECIPE_SERVE_COUNT, recipeServeCount);
            editor.putBoolean(MyConstants.CUSTOM_RECIPE_KOSHER, recipeKosher);
            editor.commit();



            startActivity(toAddRecipeFinish);
        }

        else{

            if (recipeServeCount==0) {
                Toast.makeText(this, "Please Enter the Recipe's Serve Count!", Toast.LENGTH_SHORT).show();
            }
            if (currHoursPos<=0 && currMinutesPos<=0){ //Intentionally wrote "<=0" and not "==0" bc pos Values can be -999 (from MyConstants)
                Toast.makeText(this, "Please Enter a Valid Time for the Recipe!", Toast.LENGTH_SHORT).show();

            }
        }


    }

    public void back(View view){
        //No need to save the string Lists of Steps bc finish() calls onDestroy and we save there
        finish();
    }

    /**
     * this function checks that all the inputted info is valid.
     * if so, the function saves the info in Shared Preferences and starts the CreateRecipeFinishScreen Activity.
     * if not, the function alerts the user bases on the info that isn't valid.
     * <p>
     *
     * @param view - the button pressed.
     *
     *
     *
     * @return	None
     */
    public void saveEdit(View view)
    {
        //Checking For Valid Input
        if (!serveCountET.getText().toString().isEmpty()) recipeServeCount = Integer.parseInt(serveCountET.getText().toString());

        if (recipeServeCount!= 0 && (currHoursPos>0 || currMinutesPos>0))
        { //Intentionally wrote ">0" and not "!=0" bc pos Values can be -999 (from MyConstants)

            Intent backToFinish = new Intent(CreateRecipeExtraInfo.this, CreateRecipeFinishScreen.class);
            backToFinish.putExtra("Previous Activity", MyConstants.FROM_PROFILE_AKA_EDIT_MODE);

            //From This
            SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
            SharedPreferences.Editor editor=settings.edit();
            makeFinalRecipeTime(recipeTimeHours, recipeTimeMinutes);
            editor.putString(MyConstants.CUSTOM_RECIPE_TIME, finalRecipeTime);
            editor.putString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL, recipeDifficultyLevel);
            editor.putInt(MyConstants.CUSTOM_RECIPE_SERVE_COUNT, recipeServeCount);
            editor.putBoolean(MyConstants.CUSTOM_RECIPE_KOSHER, recipeKosher);
            editor.commit();

            backToFinish.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backToFinish);
        }

        else{

            if (recipeServeCount==0) {
                Toast.makeText(this, "Please Enter the Recipe's Serve Count!", Toast.LENGTH_SHORT).show();
            }
            if (currHoursPos<=0 && currMinutesPos<=0){ //Intentionally wrote "<=0" and not "==0" bc pos Values can be -999 (from MyConstants)
                Toast.makeText(this, "Please Enter a Valid Time for the Recipe!", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView hoursTV = findViewById(R.id.CR_HoursTV);
            if (parent.getId()==R.id.CR_TimeSpinnerHours){
                if (position==1){
                    hoursTV.setText("Hour");
                    recipeTimeHours = 1; // 1 is the value at position chosen,
                    // could have written Integer.parseInt(MyConstants.CUSTOM_RECIPE_TIME_HOUR_OPTIONS_FOR_SPINNER[position] instead of 1
                }
                else{
                    hoursTV.setText("Hours");
                    recipeTimeHours = Integer.parseInt(MyConstants.CUSTOM_RECIPE_TIME_HOUR_OPTIONS_FOR_SPINNER[position]);
                }
                currHoursPos = position;
            }

            else if (parent.getId()==R.id.CR_TimeSpinnerMinutes){
                recipeTimeMinutes = Integer.parseInt(MyConstants.CUSTOM_RECIPE_TIME_MINUTE_OPTIONS_FOR_SPINNER[position]);
                currMinutesPos = position;
            }


            //finalRecipeTime is calculated in next(); it isnt needed to be saved bc we save the pos of the spinners, not the final string of time
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
    }
}