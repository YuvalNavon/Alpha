package com.example.lifesworkiguess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateRecipeMakeStepFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRecipeMakeStepFrag extends Fragment {




    //From this
    Button  addStepBtn;
    EditText stepNameET, stepDescriptionET, stepTimeET;
    String  stepName, stepDescription, stepTime;

    ArrayList<Step> stepsList;

    ImageView stepImageIV;

    StepsViewModel stepsViewModel;

    public CreateRecipeMakeStepFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateRecipeMakeStepFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateRecipeMakeStepFrag newInstance(String param1, String param2) {
        CreateRecipeMakeStepFrag fragment = new CreateRecipeMakeStepFrag();
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
            Toast.makeText(getContext(), "Step Added!", Toast.LENGTH_SHORT).show();

            stepNameET.setText("");
            stepDescriptionET.setText("");
            stepTimeET.setText("");
        }

        //Removing Keyboard
        View focusedView = getActivity().getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.SHOW_FORCED);
            focusedView.clearFocus();
        }
    }



}