package com.example.lifesworkiguess;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.lifesworkiguess.databinding.ActivityTrulyFinalCreateRecipeStepsTabbedBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CreateRecipeStepsTabbed extends AppCompatActivity {

    private ActivityTrulyFinalCreateRecipeStepsTabbedBinding binding;


    //From this
    StepsViewModel stepsViewModel;
    ArrayList<String[]> stepsInStringLists;

    Button editBTN;
    ImageView nextBTN, backBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        //Tabbed Activity stuff
        binding = ActivityTrulyFinalCreateRecipeStepsTabbedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CreateRecipeMakeStepFrag makeStepFrag = new CreateRecipeMakeStepFrag();
        StepsListFrag viewStepsFrag = new StepsListFrag();

        Bundle stepsBundle = new Bundle();
        stepsBundle.putString(MyConstants.CUSTOM_RECIPE_STEPS_VIEW_MODE, MyConstants.CUSTOM_RECIPE_VIEW_STEPS_DURING_MAKING);
        viewStepsFrag.setArguments(stepsBundle);

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(makeStepFrag);
        fragmentList.add(viewStepsFrag);

        ArrayList<String> fragmentTitleList = new ArrayList<>();
        fragmentTitleList.add("Add Step");
        fragmentTitleList.add("View Steps");


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, fragmentTitleList);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);


        //StepViewModel stuff
        stepsViewModel = new ViewModelProvider(this).get(StepsViewModel.class);
        stepsViewModel.getStepsList().observe(this, new Observer<ArrayList<Step>>() {
            @Override
            public void onChanged(ArrayList<Step> steps) {

               ArrayList<Step> stepsList = stepsViewModel.getStepsList().getValue();
                if (stepsList == null) {
                    stepsList = new ArrayList<>();

                }
               stepListToStringList(stepsList);
            }
        });


        editBTN = findViewById(R.id.CR_Steps_EditBTN);
        nextBTN = findViewById(R.id.CR_Steps_NextBTN);
        backBTN = findViewById(R.id.CR_Steps_BackBTN);

        Intent gi = getIntent();

        //We check if the user got to this activity from the finish screen or from the activity before this one

        if (gi.getStringExtra("Previous Activity")!=null && gi.getStringExtra("Previous Activity").equals(MyConstants.FROM_FINISH_SCREEN)){
            //Right now I do not allow users to edit by pressing on items from the finish screen, so  this will remain empty for now
            //I can do this SO SO SO EASILY but design Wise I think its useless and confusing

        }

        else if (gi.getStringExtra("Previous Activity")!=null &&gi.getStringExtra("Previous Activity").equals(MyConstants.FROM_PROFILE_AKA_EDIT_MODE))
        {   //User is editing uploaded Recipe

            nextBTN.setVisibility(View.GONE);
            backBTN.setVisibility(View.GONE);
            //getting saved steps
            SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
            String jsonofSteps = settings.getString(MyConstants.CUSTOM_RECIPE_STEPS, null);
            if (jsonofSteps !=null){
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<String[]>>(){}.getType();
                stepsInStringLists = gson.fromJson(jsonofSteps, type);
                if (stepsInStringLists !=null){
                    makeRecyclerViewForReopen(stepsInStringLists);  //This method is just StringListsToSteps
                    //Basically we just put the saved steps in the stepsViewModel and that updates it so that in the ViewStepsFrag,
                    // the StepsViewModel Observe method is called and that method makes the RV
                }
            }
        }

        else  //Normal Creating Recipe Process
        {
            editBTN.setVisibility(View.GONE);
            //getting saved steps
            SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
            String jsonofSteps = settings.getString(MyConstants.CUSTOM_RECIPE_STEPS, null);
            if (jsonofSteps !=null){
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<String[]>>(){}.getType();
                stepsInStringLists = gson.fromJson(jsonofSteps, type);
                if (stepsInStringLists !=null){
                    makeRecyclerViewForReopen(stepsInStringLists); //This method is just StringListsToSteps
                    //Basically we just put the saved steps in the stepsViewModel and that updates it so that in the ViewStepsFrag,
                    // the StepsViewModel Observe method is called and that method makes the RV
                }
            }
        }
    }



    public void onDestroy() {

        super.onDestroy();
        //The added Ingredients ArrayLists of String are deleted when the user finishes the recipe, either by uploading it or by going back to the community screen
        if (!stepsViewModel.getStepsList().getValue().isEmpty()){

            saveCurrentlyAddedSteps();

        }

    }



    public void saveCurrentlyAddedSteps(){

        //The added Steps ArrayLists of String are deleted when the user finishes the recipe, either by uploading it or by going back to the community screen

        Gson gson = new Gson();
        String jsonOfSteps = gson.toJson(stepsInStringLists);

        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        editor.putString(MyConstants.CUSTOM_RECIPE_STEPS, jsonOfSteps);
        editor.commit();
    }

    public void makeRecyclerViewForReopen(ArrayList<String[]> stepsInStringLists ){
        //Basically we just put the saved steps in the stepsViewModel and that updates it so that in the ViewStepsFrag,
        // the StepsViewModel Observe method is called and that method makes the RV

        ArrayList<Step> stepsList = new ArrayList<>();
        for (String[] currStepString : stepsInStringLists){

            String stepName = currStepString[MyConstants.STRING_LIST_STEP_NAME_INDEX];
            String stepDescription = currStepString[MyConstants.STRING_LIST_STEP_DESCRIPTION_INDEX];
            String stepTime = currStepString[MyConstants.STRING_LIST_STEP_TIME_INDEX];
            String stepAction = currStepString[MyConstants.STRING_LIST_STEP_ACTION_INDEX];
            // String stepNumber - No need, we get that from index

            Step currStep = new Step(stepName, stepDescription, stepTime, stepAction, stepsInStringLists.indexOf(currStepString));
            stepsList.add(currStep);
        }

        stepsViewModel.setStepsList(stepsList);

    }

    public void stepListToStringList(ArrayList<Step> stepsList){  //See note on method "addStepToStringList"
        stepsInStringLists = new ArrayList<>();
        for (Step currStep : stepsList){
            String stepName = currStep.getName();
            String stepDescription = currStep.getDescription();
            String stepTime = currStep.getTime();
            String stepImageUri = currStep.getAction();

            //No need for Step Number - the index in the list is the number

            String[] currStepInString = new String[]{stepName, stepDescription, stepTime, stepImageUri};

            stepsInStringLists.add(currStepInString);
        }
    }




    public void next(View view){

        ArrayList<Step> stepsList = stepsViewModel.getStepsList().getValue();
        if (stepsList==null || stepsList.isEmpty() || stepsList.size()<2)
        {
            AlertDialog.Builder addStepsDialogBuilder = new AlertDialog.Builder(CreateRecipeStepsTabbed.this);

            addStepsDialogBuilder.setTitle("Not Enough Steps");
            addStepsDialogBuilder.setMessage("Please Add at least 2 Steps for your Recipe!");


            addStepsDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });


            AlertDialog addStepsDialog = addStepsDialogBuilder.create();
            addStepsDialog.show();
        }

        else
        {
            Intent toAddRecipeExtraInfo = new Intent(this, CreateRecipeExtraInfo.class);
            toAddRecipeExtraInfo.putExtra("Previous Activity", MyConstants.NOT_FROM_FINISH_SCREEN);

            //From This
            Gson gson = new Gson();
            String jsonOfSteps = gson.toJson(stepsInStringLists);
            SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
            SharedPreferences.Editor editor=settings.edit();
            editor.putString(MyConstants.CUSTOM_RECIPE_STEPS, jsonOfSteps);
            editor.commit();

            startActivity(toAddRecipeExtraInfo);
        }



    }

    public void saveEdit(View view)
    {
        ArrayList<Step> stepsList = stepsViewModel.getStepsList().getValue();
        if (stepsList==null || stepsList.isEmpty() || stepsList.size()<2)
        {
            AlertDialog.Builder addStepsDialogBuilder = new AlertDialog.Builder(CreateRecipeStepsTabbed.this);

            addStepsDialogBuilder.setTitle("Not Enough Steps");
            addStepsDialogBuilder.setMessage("Please Add at least 2 Steps for your Recipe!");


            addStepsDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });


            AlertDialog addStepsDialog = addStepsDialogBuilder.create();
            addStepsDialog.show();
        }

        else
        {
            Intent backToFinish = new Intent(this, CreateRecipeFinishScreen.class);
            backToFinish.putExtra("Previous Activity", MyConstants.FROM_PROFILE_AKA_EDIT_MODE);

            //From This
            Gson gson = new Gson();
            String jsonOfSteps = gson.toJson(stepsInStringLists);
            SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
            SharedPreferences.Editor editor=settings.edit();
            editor.putString(MyConstants.CUSTOM_RECIPE_STEPS, jsonOfSteps);
            editor.commit();

            startActivity(backToFinish);
        }
    }

    public void back(View view){
        //No need to save the string Lists of Steps bc finish() calls onDestroy and we save there
        finish();
    }
}