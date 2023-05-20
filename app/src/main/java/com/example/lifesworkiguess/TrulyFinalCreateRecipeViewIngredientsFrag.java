package com.example.lifesworkiguess;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class TrulyFinalCreateRecipeViewIngredientsFrag extends Fragment {

    String jsonofIngredients;
    ArrayList<String[]> ingredientsInStringlists;
    ArrayList<Ingredient> ingredientsList;
    RecyclerView ingredientsRV;

    public TrulyFinalCreateRecipeViewIngredientsFrag() {
        // Required empty public constructor
    }


    public static TrulyFinalCreateRecipeViewIngredientsFrag newInstance() {
        TrulyFinalCreateRecipeViewIngredientsFrag fragment = new TrulyFinalCreateRecipeViewIngredientsFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           jsonofIngredients = getArguments().getString(MyConstants.CUSTOM_RECIPE_INGREDIENTS, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_truly_final_create_recipe_view_ingredients, container, false);

        ingredientsRV = view.findViewById(R.id.CR_Finish_ingredientsRV);

        if (jsonofIngredients!=null){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String[]>>(){}.getType();
            ingredientsInStringlists = gson.fromJson(jsonofIngredients, type);
            if (ingredientsInStringlists !=null){
                StringListsToIngredients();
                // Create an instance of your adapter
                customAdapterIngredients adapter = new customAdapterIngredients(getContext(), ingredientsList);

                // Set the layout manager for the RecyclerView
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                ingredientsRV.setLayoutManager(linearLayoutManager);

                // Set the adapter for the RecyclerView
                ingredientsRV.setAdapter(adapter);
            }
        }



        return view;
    }

    public void StringListsToIngredients(){  //I use this method in a bunch of different activities but it feels like it should be like this for possible changes
        //depending on each activity, instead of putting it in myServices

        ingredientsList = new ArrayList<>();
        //Each ArrayList<String> in the ingredientsInLists is of the following format: name, amount, units
        for (String[] ingredientStringList : ingredientsInStringlists){
            String ingredientName = ingredientStringList[MyConstants.STRING_LIST_INGREDIENT_NAME_INDEX];
            String ingredientAmount = ingredientStringList[MyConstants.STRING_LIST_INGREDIENT_AMOUNT_INDEX];
            String ingredientUnits = ingredientStringList[MyConstants.STRING_LIST_INGREDIENT_UNITS_INDEX];

            //We make an ingredient out of every List in the ingredientsInLists and add it to the ingredientsList
            Ingredient ingredient = new Ingredient(ingredientName, ingredientAmount, ingredientUnits);
            ingredientsList.add(ingredient);

        }

    }
}