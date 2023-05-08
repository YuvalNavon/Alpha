package com.example.lifesworkiguess;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_truly_final_create_recipe_view_steps, container, false);

        stepsRV = view.findViewById(R.id.viewStepsRV);

        if (mode.equals(MyConstants.CUSTOM_RECIPE_VIEW_STEPS_FINISH)){
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



        RVAdapter = new AddedStepsCustomAdapter(getContext(), stepsViewModel.getStepsList().getValue(), this::onItemClick);
        stepsRV.setAdapter(RVAdapter);

//        //really cool
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        stepsRV.addItemDecoration(dividerItemDecoration);

        // (from YT vid "Drag and drop Reorder in Recycler View | Android"
        // set up item touch helper to handle drag and swipe
//        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN |
//                ItemTouchHelper.START | ItemTouchHelper.END, 0) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//
//                int fromPos = viewHolder.getAdapterPosition();
//                int toPos = target.getAdapterPosition();
//
//                Collections.swap(stepsViewModel.getStepsList().getValue(), fromPos, toPos);
//                updateStepNumbers();
//                stepsRV.getAdapter().notifyItemMoved(fromPos, toPos);
//
//                return true;
//
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//            }
//        };

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                ItemTouchHelper.START | ItemTouchHelper.END, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();

                Collections.swap(stepsViewModel.getStepsList().getValue(), fromPos, toPos);
                updateStepNumbers();
                stepsRV.getAdapter().notifyItemMoved(fromPos, toPos);

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                stepsViewModel.getStepsList().getValue().remove(viewHolder.getAdapterPosition());
                updateStepNumbers();
                stepsRV.getAdapter().notifyItemRemoved(viewHolder.getAdapterPosition());

            }
        });

        helper.attachToRecyclerView(stepsRV);


    }

    private void updateStepNumbers() {
        ArrayList<Step> stepsList = stepsViewModel.getStepsList().getValue();
        for (int i = 0; i < stepsList.size(); i++) {
            Step step = stepsList.get(i);
            step.setNumber(i);
        }
    }

    @Override
    public void onItemClick(int position) {

    }



}