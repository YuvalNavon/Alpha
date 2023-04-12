package com.example.lifesworkiguess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TrulyFinalCreateRecipeIngredients extends AppCompatActivity {


    //From General
    String recipeName, recipeDescription;

    //From Image
    Uri recipeImageUri;

    //From This
    EditText ingredientNameET, ingredientAmountET, ingredientUnitsET;
    String ingredientName, ingredientAmount, ingredientUnits;

    ArrayList<Ingredient> ingredientsList;
    ArrayList<String[]> ingredientsInStringLists;
    RecyclerView ingredientsRV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truly_final_create_recipe_ingredients);

        ingredientNameET = findViewById(R.id.ingredientNameCreateRecipe);
        ingredientAmountET = findViewById(R.id.ingredientAmountCreateRecipe);
        ingredientUnitsET = findViewById(R.id.ingredientUnitsCreateRecipe);
        ingredientsRV = findViewById(R.id.ingredientsRVCreateRecipeIngredients);

        ingredientsList = new ArrayList<>();
        ingredientsInStringLists = new ArrayList<>();

        //getting saved ingredients
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        String jsonOfIngredients = settings.getString(MyConstants.CUSTOM_RECIPE_INGREDIENTS, null);
        if (jsonOfIngredients!=null){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String[]>>(){}.getType();
            ingredientsInStringLists = gson.fromJson(jsonOfIngredients, type);
            if (ingredientsInStringLists !=null){
                makeRecyclerViewForReopen(ingredientsInStringLists);
            }
        }

        Intent gi = getIntent();

        //We check if the user got to this activity from the finish screen or from the activity before this one

        if (gi.getStringExtra("Previous Activity").equals(MyConstants.FROM_FINISH_SCREEN)){
            //Right now I do not allow users to edit by pressing on items from the finish screen, so  this will remain empty for now
        }

        else if (gi.getStringExtra("Previous Activity").equals(MyConstants.NOT_FROM_FINISH_SCREEN)){

            //From General
            recipeName =  gi.getStringExtra(MyConstants.CUSTOM_RECIPE_NAME);
            recipeDescription = gi.getStringExtra(MyConstants.CUSTOM_RECIPE_DESCRIPTION);

            //From Image - Nothing, image is saved in files


        }


    }


    public void onDestroy() {

        super.onDestroy();
        //The added Ingredients ArrayLists of String are deleted when the user finishes the recipe, either by uploading it or by going back to the community screen
        if (!ingredientsInStringLists.isEmpty()){

            saveCurrentlyAddedIngredients();

        }

    }


    public void saveCurrentlyAddedIngredients(){

        //The added Ingredients ArrayLists of String are deleted when the user finishes the recipe, either by uploading it or by going back to the community screen

        Gson gson = new Gson();
        String jsonOfIngredients = gson.toJson(ingredientsInStringLists);

        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        editor.putString(MyConstants.CUSTOM_RECIPE_INGREDIENTS, jsonOfIngredients);
        editor.commit();
    }


    public void addIngredient(View view){

        ingredientName = ingredientNameET.getText().toString();
        ingredientAmount = ingredientAmountET.getText().toString();
        ingredientUnits = ingredientUnitsET.getText().toString();

        if (!ingredientName.isEmpty() && !ingredientAmount.isEmpty() && !ingredientUnits.isEmpty()) {

            Ingredient addedIngredient = new Ingredient(ingredientName, ingredientAmount, ingredientUnits);

            ingredientsList.add(addedIngredient);
            addIngredientToStringLists(addedIngredient);

            makeRecyclerView();

        }
    }

    public void addIngredientToStringLists(Ingredient ingredient){
//        ArrayList<String> addedIngredientList = new ArrayList<>();
//        addedIngredientList.add(ingredient.getName());
//        addedIngredientList.add(ingredient.getAmount());
//        addedIngredientList.add(ingredient.getUnits());

        String[] addedIngredientList = new String[]{ingredient.getName(), ingredient.getAmount(), ingredient.getUnits()};

        ingredientsInStringLists.add(addedIngredientList);
    }



    public void makeRecyclerView(){

        // Create an instance of your adapter
        customAdapterIngredients adapter = new customAdapterIngredients(this, ingredientsList);

        // Set the layout manager for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        ingredientsRV.setLayoutManager(linearLayoutManager);

        // Set the adapter for the RecyclerView
        ingredientsRV.setAdapter(adapter);
    }

    public void makeRecyclerViewForReopen(ArrayList<String[]> ingredientsInLists){

        //Each ArrayList<String> in the ingredientsInLists is of the following format: name, amount, units
        for (String[] ingredientStringList : ingredientsInLists){
            String ingredientName = ingredientStringList[MyConstants.STRING_LIST_INGREDIENT_NAME_INDEX];
            String ingredientAmount = ingredientStringList[MyConstants.STRING_LIST_INGREDIENT_AMOUNT_INDEX];
            String ingredientUnits = ingredientStringList[MyConstants.STRING_LIST_INGREDIENT_UNITS_INDEX];

            //We make an ingredient out of every List in the ingredientsInLists and add it to the ingredientsList
            Ingredient ingredient = new Ingredient(ingredientName, ingredientAmount, ingredientUnits);
            ingredientsList.add(ingredient);

        }

        //We make a recyclerView out of that
        makeRecyclerView();

    }

    public void next(View view){
        Intent toAddRecipeSteps = new Intent(this, TrulyFinalCreateRecipeStepsTabbed.class);
        toAddRecipeSteps.putExtra("Previous Activity", MyConstants.NOT_FROM_FINISH_SCREEN);

        //From General
        toAddRecipeSteps.putExtra(MyConstants.CUSTOM_RECIPE_NAME, recipeName);
        toAddRecipeSteps.putExtra(MyConstants.CUSTOM_RECIPE_DESCRIPTION, recipeDescription);


        //From Image - Nothing

        //From This
        Gson gson = new Gson();
        String jsonOfIngredients = gson.toJson(ingredientsInStringLists);
        toAddRecipeSteps.putExtra(MyConstants.CUSTOM_RECIPE_INGREDIENTS, jsonOfIngredients);

        startActivity(toAddRecipeSteps);
    }

    public void back(View view){

        //No need to save the string lists of ingredients bc finish() calls onDestroy and we save there
        finish();



    }




}