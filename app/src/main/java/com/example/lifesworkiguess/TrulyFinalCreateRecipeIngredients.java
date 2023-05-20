package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
    ImageView upArrowIV, downArrowIV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truly_final_create_recipe_ingredients);

        ingredientNameET = findViewById(R.id.ingredientNameCreateRecipe);
        ingredientAmountET = findViewById(R.id.ingredientAmountCreateRecipe);
        ingredientUnitsET = findViewById(R.id.ingredientUnitsCreateRecipe);
        ingredientsRV = findViewById(R.id.ingredientsRVCreateRecipeIngredients);

        upArrowIV = findViewById(R.id.CR_ING_UpArrowIV);
        downArrowIV = findViewById(R.id.CR_ING_DownArrowIV);
        upArrowIV.setVisibility(View.INVISIBLE);
        downArrowIV.setVisibility(View.INVISIBLE);

        ingredientsList = new ArrayList<>();
        ingredientsInStringLists = new ArrayList<>();

        //getting saved ingredients
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        String jsonOfIngredients = settings.getString(MyConstants.CUSTOM_RECIPE_INGREDIENTS, null);
        if (jsonOfIngredients!=null){
            upArrowIV.setVisibility(View.VISIBLE);
            downArrowIV.setVisibility(View.VISIBLE);
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
        ingredientsListToStringLists(ingredientsList); //Updating ingredientsInStringLists if the user swiped items
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

            ingredientNameET.setText("");
            ingredientAmountET.setText("");
            ingredientUnitsET.setText("");

        }

        else
        {
            Toast.makeText(this, "Please fill ALL fields!", Toast.LENGTH_SHORT).show();
        }

        //Removing Keyboard
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.SHOW_FORCED);
            focusedView.clearFocus();
        }


    }

    public void addIngredientToStringLists(Ingredient ingredient){

        String[] addedIngredientList = new String[]{ingredient.getName(), ingredient.getAmount(), ingredient.getUnits()};

        ingredientsInStringLists.add(addedIngredientList);
    }

    public void ingredientsListToStringLists(ArrayList<Ingredient> ingredients){

        ingredientsInStringLists = new ArrayList<>();

        for (Ingredient ingredient: ingredients)
        {
            String[] addedIngredientList = new String[]{ingredient.getName(), ingredient.getAmount(), ingredient.getUnits()};
            ingredientsInStringLists.add(addedIngredientList);

        }
    }





    public void makeRecyclerView(){

        upArrowIV.setVisibility(View.VISIBLE);
        downArrowIV.setVisibility(View.VISIBLE);

        // Create an instance of your adapter
        customAdapterIngredients adapter = new customAdapterIngredients(this, ingredientsList);

        // Set the layout manager for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        ingredientsRV.setLayoutManager(linearLayoutManager);

        // Set the adapter for the RecyclerView
        ingredientsRV.setAdapter(adapter);
        ingredientsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1)) {
                    // Cannot scroll up
                    upArrowIV.setVisibility(View.INVISIBLE);
                } else {
                    // Can scroll up
                    upArrowIV.setVisibility(View.VISIBLE);
                }
                if (!recyclerView.canScrollVertically(1)) {
                    // Cannot scroll down
                    downArrowIV.setVisibility(View.INVISIBLE);
                } else {
                    // Can scroll down
                    downArrowIV.setVisibility(View.VISIBLE);
                }
            }
        });

        ItemTouchHelper.Callback callback = new AddedIngredientsSwipeCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(ingredientsRV);
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

        if (!ingredientsList.isEmpty())
        {
            ingredientName = ingredientNameET.getText().toString();
            ingredientAmount = ingredientAmountET.getText().toString();
            ingredientUnits = ingredientUnitsET.getText().toString();

            if (!ingredientName.isEmpty() || !ingredientAmount.isEmpty() || !ingredientUnits.isEmpty())
            {
                AlertDialog.Builder midIngredientAddingDialogBuilder = new AlertDialog.Builder(TrulyFinalCreateRecipeIngredients.this);

                midIngredientAddingDialogBuilder.setTitle("Ingredient Not Added");
                midIngredientAddingDialogBuilder.setMessage("Would You like to Continue without Adding The Ingredient You're currently Writing?");


                midIngredientAddingDialogBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                midIngredientAddingDialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent toAddRecipeSteps = new Intent(TrulyFinalCreateRecipeIngredients.this, TrulyFinalCreateRecipeStepsTabbed.class);
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
                });


                AlertDialog midIngredientAddingDialog = midIngredientAddingDialogBuilder.create();
                midIngredientAddingDialog.show();
            }

            else
            {
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

        }

        else //No Ingredients added
        {
            AlertDialog.Builder noIngredientsDialogBuilder = new AlertDialog.Builder(TrulyFinalCreateRecipeIngredients.this);

            noIngredientsDialogBuilder.setTitle("No Ingredients");
            noIngredientsDialogBuilder.setMessage("Please Add at least 1 Ingredient to Your Recipe!");


            noIngredientsDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });


            AlertDialog noIngredientsDialog = noIngredientsDialogBuilder.create();
            noIngredientsDialog.show();
        }

    }

    public void back(View view){

        //No need to save the string lists of ingredients bc finish() calls onDestroy and we save there
        finish();



    }




}