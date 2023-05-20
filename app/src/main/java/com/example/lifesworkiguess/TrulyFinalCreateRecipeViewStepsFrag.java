package com.example.lifesworkiguess;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrulyFinalCreateRecipeViewStepsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrulyFinalCreateRecipeViewStepsFrag extends Fragment implements AddedStepsViewHolder.OnItemClickListener {


    String mode; //For deciding if this is opened in the make steps screen of create recipe, or the finish screen
    boolean isIntro; //For deciding if this frag is opened in the intro screen;

    //for making steps screen
    StepsViewModel stepsViewModel;
    AddedStepsCustomAdapter RVAdapter;

    //for finish screen
    String jsonofSteps;
    ArrayList<String[]> stepsInStringLists;
    ArrayList<Step> stepsList;

    //for both
    RecyclerView stepsRV;
    TextView editMessage;


    public TrulyFinalCreateRecipeViewStepsFrag() {
        // Required empty public constructor
    }


    public static TrulyFinalCreateRecipeViewStepsFrag newInstance() {
        TrulyFinalCreateRecipeViewStepsFrag fragment = new TrulyFinalCreateRecipeViewStepsFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mode = getArguments().getString(MyConstants.CUSTOM_RECIPE_STEPS_VIEW_MODE, null);
            jsonofSteps = getArguments().getString(MyConstants.CUSTOM_RECIPE_STEPS, null);
        }

    }

    @Override
    public void onResume(){
        super.onResume();




        SharedPreferences settings= getContext().getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        int changedStepNumber = settings.getInt(MyConstants.STEP_NUMBER_KEY, -999);
        String changedStepName = settings.getString(MyConstants.STEP_NAME_KEY, null);
        String changedStepDescription = settings.getString(MyConstants.STEP_DESCRIPTION_KEY, null);
        String changedStepTime = settings.getString(MyConstants.STEP_TIME_KEY, null);

        if (changedStepNumber!=-999 && changedStepName!=null && changedStepDescription!=null && changedStepTime!=null
            && !changedStepName.isEmpty() && !changedStepDescription.isEmpty() && !changedStepTime.isEmpty() )
        {
            Step changedStep = new Step(changedStepName, changedStepDescription, changedStepTime, changedStepNumber);
            ArrayList<Step> changedStepsList = stepsViewModel.getStepsList().getValue();
            changedStepsList.set(changedStepNumber, changedStep);
            stepsViewModel.setStepsList(changedStepsList);
        }

        SharedPreferences.Editor editor=settings.edit();
        editor.putInt(MyConstants.STEP_NUMBER_KEY, -999 );
        editor.putString(MyConstants.STEP_NAME_KEY, null);
        editor.putString(MyConstants.STEP_DESCRIPTION_KEY, null);
        editor.putString(MyConstants.STEP_TIME_KEY, null);
        editor.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_truly_final_create_recipe_view_steps, container, false);

        stepsRV = view.findViewById(R.id.viewStepsRV);
        editMessage = view.findViewById(R.id.ViewStepsFrag_MessageTV);

        if (mode.equals(MyConstants.CUSTOM_RECIPE_VIEW_STEPS_FINISH)){
            editMessage.setVisibility(View.GONE);
            if (jsonofSteps!=null){
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<String[]>>(){}.getType();
                stepsInStringLists = gson.fromJson(jsonofSteps, type);
                if (stepsInStringLists !=null){

                    StringListsToSteps();

                    //Making RV


                    // Create an instance of your adapter
                    AddedStepsCustomAdapter adapter = new AddedStepsCustomAdapter(getContext(), stepsList, this::onItemClick);

                    // Set the layout manager for the RecyclerView
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    stepsRV.setLayoutManager(linearLayoutManager);

                    // Set the adapter for the RecyclerView
                    stepsRV.setAdapter(adapter);
                }
            }

        }

        else if (mode.equals(MyConstants.CUSTOM_RECIPE_VIEW_STEPS_DURING_MAKING)){
            stepsViewModel = new ViewModelProvider(requireActivity()).get(StepsViewModel.class);
            stepsRV.setLayoutManager(new LinearLayoutManager(getContext()));




            stepsViewModel.getStepsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Step>>() {
                @Override
                public void onChanged(ArrayList<Step> steps) {
                    makeRecyclerView();
                }
            });
        }






        return view;
    }

    public void StringListsToSteps(){ //I use this method in a bunch of different activities but it feels like it should be like this for possible changes
        //depending on each activity, instead of putting it in myServices
        stepsList = new ArrayList<>();
        for (String[] currStepString : stepsInStringLists){

            String stepName = currStepString[MyConstants.STRING_LIST_STEP_NAME_INDEX];
            String stepDescription = currStepString[MyConstants.STRING_LIST_STEP_DESCRIPTION_INDEX];
            String stepTime = currStepString[MyConstants.STRING_LIST_STEP_TIME_INDEX];
            String stepAction = currStepString[MyConstants.STRING_LIST_STEP_ACTION_INDEX];
            // String stepNumber - No need, we get that from index

            Step currStep = new Step(stepName, stepDescription, stepTime, stepAction, stepsInStringLists.indexOf(currStepString));
            stepsList.add(currStep);
        }
    }

    public void makeRecyclerView(){



        RVAdapter = new AddedStepsCustomAdapter(getContext(), stepsViewModel, this::onItemClick);
        stepsRV.setAdapter(RVAdapter);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(RVAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(stepsRV);


    }



    @Override
    public void onItemClick(int position) {

        if (mode.equals(MyConstants.CUSTOM_RECIPE_VIEW_STEPS_DURING_MAKING))
        {
            Step pickedStep = stepsViewModel.getStepsList().getValue().get(position);
            String stepName = pickedStep.getName();
            String stepDescription = pickedStep.getDescription();
            String stepTime = pickedStep.getTime();
            String stepAction = pickedStep.getAction();
            int stepNumber = pickedStep.getNumber();

            Intent toSingleStepScreen = new Intent(getContext(), SingleStepScreen.class);

            toSingleStepScreen.putExtra("Step Number", stepNumber);
            toSingleStepScreen.putExtra("Step Name", stepName);
            toSingleStepScreen.putExtra("Step Description", stepDescription);
            toSingleStepScreen.putExtra("Step Time", stepTime);
            toSingleStepScreen.putExtra("Step Action", stepAction);

            getContext().startActivity(toSingleStepScreen);
        }
    }



}