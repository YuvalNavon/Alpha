package com.example.lifesworkiguess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrulyFinalCreateRecipeOverviewFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrulyFinalCreateRecipeOverviewFrag extends Fragment {

    String recipeName, recipeDescription;
    String recipeTime, recipeDifficultyLevel;
    int recipeServeCount;
    boolean recipeKosher;
    ArrayList<Ingredient> ingredientsList;
    ArrayList<Step> stepsList;
    String jsonofIngredients, jsonofSteps;
    ArrayList<String[]> ingredientsInStringLists, stepsInStringLists;

    TextView recipeNameTV, recipeDescriptionTV, recipeTimeTV, recipeDifficultyLevelTV, recipeServeCountTV, recipeKosherTV;
    ImageView recipeImage, kosherImage;
    Button finish;

    FirebaseDatabase FBDB;
    DatabaseReference refUsers, refCommunityLessons;
    ValueEventListener userRecipeAdder, communityLessonAdder;
    FirebaseUser loggedInUser;
    FirebaseAuth fAuth;


    public TrulyFinalCreateRecipeOverviewFrag() {
        // Required empty public constructor
    }


    public static TrulyFinalCreateRecipeOverviewFrag newInstance(String param1, String param2) {
        TrulyFinalCreateRecipeOverviewFrag fragment = new TrulyFinalCreateRecipeOverviewFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeName = getArguments().getString(MyConstants.CUSTOM_RECIPE_NAME);
            recipeDescription = getArguments().getString(MyConstants.CUSTOM_RECIPE_DESCRIPTION);
            recipeTime = getArguments().getString(MyConstants.CUSTOM_RECIPE_TIME);
            recipeDifficultyLevel = getArguments().getString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL);
            recipeServeCount = getArguments().getInt(MyConstants.CUSTOM_RECIPE_SERVE_COUNT);
            recipeKosher = getArguments().getBoolean(MyConstants.CUSTOM_RECIPE_KOSHER);
            jsonofIngredients = getArguments().getString(MyConstants.CUSTOM_RECIPE_INGREDIENTS);
            jsonofSteps = getArguments().getString(MyConstants.CUSTOM_RECIPE_STEPS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_truly_final_create_recipe_overview, container, false);

        recipeImage = view.findViewById(R.id.CR_Finish_ImageIV);
        recipeNameTV = view.findViewById(R.id.CR_Finish_NameTV);
        recipeDescriptionTV = view.findViewById(R.id.CR_Finish_DescriptionTV);
        recipeTimeTV = view.findViewById(R.id.CR_Finish_TimeTV);
        recipeDifficultyLevelTV = view.findViewById(R.id.CR_Finish_DifficultyTV);
        recipeKosherTV = view.findViewById(R.id.CR_Finish_KosherTV);
        kosherImage = view.findViewById(R.id.CR_Finish_KosherIV);
        recipeServeCountTV = view.findViewById(R.id.CR_Finish_ServeCountTV);

        File SelectedImageFile = new File(getContext().getFilesDir(), MyConstants.IMAGE_FILE_NAME);
        if (SelectedImageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(SelectedImageFile.getAbsolutePath());
            recipeImage.setImageBitmap(bitmap);
        }

        recipeNameTV.setText(recipeName);
        recipeDescriptionTV.setText(recipeDescription);
        recipeTimeTV.setText(recipeTime);
        recipeDifficultyLevelTV.setText(recipeDifficultyLevel);
        if (recipeKosher) {
            recipeKosherTV.setText("KOSHER");
            kosherImage.setImageResource(com.firebase.ui.auth.R.drawable.fui_ic_check_circle_black_128dp);
        }
        else{
            recipeKosherTV.setText("NOT\nKOSHER");
            kosherImage.setImageResource(android.R.drawable.ic_delete);
        }
        recipeServeCountTV.setText(Integer.toString(recipeServeCount));

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String[]>>(){}.getType();

        ingredientsInStringLists = gson.fromJson(jsonofIngredients, type);
        if(ingredientsInStringLists!=null)  StringListsToIngredients(); //can't be null cause we have input checks in ingredients screen but still


        stepsInStringLists = gson.fromJson(jsonofSteps, type);
        if (stepsInStringLists!=null) StringListsToSteps(); //can't be null cause we have input checks in steps screen but still

        finish = view.findViewById(R.id.CR_finishBTN);


        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadRecipe(v);
            }
        });


        return view;
    }

    public void StringListsToIngredients(){  //I use this method in a bunch of different activities but it feels like it should be like this for possible changes
        //depending on each activity, instead of putting it in myServices

        ingredientsList = new ArrayList<>();
        //Each ArrayList<String> in the ingredientsInLists is of the following format: name, amount, units
        for (String[] ingredientStringList : ingredientsInStringLists){
            String ingredientName = ingredientStringList[MyConstants.STRING_LIST_INGREDIENT_NAME_INDEX];
            String ingredientAmount = ingredientStringList[MyConstants.STRING_LIST_INGREDIENT_AMOUNT_INDEX];
            String ingredientUnits = ingredientStringList[MyConstants.STRING_LIST_INGREDIENT_UNITS_INDEX];

            //We make an ingredient out of every List in the ingredientsInLists and add it to the ingredientsList
            Ingredient ingredient = new Ingredient(ingredientName, ingredientAmount, ingredientUnits);
            ingredientsList.add(ingredient);

        }

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

    public void uploadRecipe(View view){

        //Adding the Recipe to the LoggedInUser List of made recipes

        fAuth = FirebaseAuth.getInstance();
        loggedInUser = fAuth.getCurrentUser();
        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        refUsers=FBDB.getReference("Users").child(loggedInUser.getUid());
        userRecipeAdder = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);
                currentUser.addCustomRecipe(recipeName);
                refUsers.setValue(currentUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addListenerForSingleValueEvent(userRecipeAdder);



        CommunityLesson addedLesson = new CommunityLesson(recipeName, recipeName, recipeServeCount, recipeTime,
                recipeDifficultyLevel, recipeKosher, loggedInUser.getUid(), recipeDescription );

        refCommunityLessons =FBDB.getReference("Community Lessons");
        refCommunityLessons.child(recipeName + " , " + loggedInUser.getUid() ).setValue(addedLesson);

    }
}