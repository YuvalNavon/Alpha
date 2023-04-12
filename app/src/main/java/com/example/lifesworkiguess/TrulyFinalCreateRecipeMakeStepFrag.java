package com.example.lifesworkiguess;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrulyFinalCreateRecipeMakeStepFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrulyFinalCreateRecipeMakeStepFrag extends Fragment {

    //From General
    String recipeName, recipeDescription;

    //From Image - Nothing

    //From Ingredients
    String jsonOfIngredients;


    //From this
    Button  addStepBtn;
    EditText stepNameET, stepDescriptionET, stepTimeET;
    String  stepName, stepDescription, stepTime;

    ArrayList<Step> stepsList;

    ImageView stepImageIV;

    StepsViewModel stepsViewModel;

    public TrulyFinalCreateRecipeMakeStepFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrulyFinalCreateRecipeMakeStepFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static TrulyFinalCreateRecipeMakeStepFrag newInstance(String param1, String param2) {
        TrulyFinalCreateRecipeMakeStepFrag fragment = new TrulyFinalCreateRecipeMakeStepFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_truly_final_create_recipe_make_step, container, false);

        stepNameET = view.findViewById(R.id.stepNameCR);
        stepDescriptionET = view.findViewById(R.id.stepDescriptionCR);
        stepTimeET = view.findViewById(R.id.stepTimeCR);
        stepImageIV = view.findViewById(R.id.stepImageIVCR);


        addStepBtn = view.findViewById(R.id.addStepBTNCR);


        addStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStep(v);
            }
        });

        stepsViewModel = new ViewModelProvider(requireActivity()).get(StepsViewModel.class);
        stepsList = stepsViewModel.getStepsList().getValue();
        if (stepsList == null) {
            stepsList = new ArrayList<>();

        }

        Activity parentActivity = getActivity();
        Intent gi = parentActivity.getIntent();

        //We check if the user got to this activity from the finish screen or from the activity before this one

        if (gi.getStringExtra("Previous Activity").equals(MyConstants.FROM_FINISH_SCREEN)){
            //Right now I do not allow users to edit by pressing on items from the finish screen, so  this will remain empty for now
        }

        else if (gi.getStringExtra("Previous Activity").equals(MyConstants.NOT_FROM_FINISH_SCREEN)){

            //From General
            recipeName =  gi.getStringExtra(MyConstants.CUSTOM_RECIPE_NAME);
            recipeDescription = gi.getStringExtra(MyConstants.CUSTOM_RECIPE_DESCRIPTION);

            //From Image - Nothing, image is saved in files

            //From Ingredients
            jsonOfIngredients = gi.getStringExtra(MyConstants.CUSTOM_RECIPE_INGREDIENTS);


        }



        return view;

    }










    public void addStep(View view){
        stepName = stepNameET.getText().toString();
        stepDescription = stepDescriptionET.getText().toString();
        stepTime = stepTimeET.getText().toString();

        if (!stepName.isEmpty() && !stepDescription.isEmpty() && !stepTime.isEmpty()){
            Step addedStep = new Step(stepName, stepDescription, stepTime, stepsList.size()); //A STEP's NUMBER is its INDEX in the stepList.
            stepsList.add(addedStep);

            stepsViewModel.setStepsList(stepsList);
            Toast.makeText(getContext(), "ADDED", Toast.LENGTH_SHORT).show();
        }
    }


    public void next(View view){

    }

    public void back(View view){

        //No need to save the string Lists of Steps bc finish() calls onDestroy and we save there
        getActivity().finish();



    }


}