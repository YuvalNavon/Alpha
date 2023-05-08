package com.example.lifesworkiguess;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class IngredientsListFrag extends Fragment {

    Recipe recipe;
    RecyclerView ingredientsRV;
    ImageView upArrowIV, downArrowIV;
    Context context;



    public IngredientsListFrag() {
        // Required empty public constructor
    }


    public static IngredientsListFrag newInstance() {
        IngredientsListFrag fragment = new IngredientsListFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_ingredients_list, container, false);
        ingredientsRV = view.findViewById(R.id.fragIngredientsRV);
        upArrowIV = view.findViewById(R.id.fragUpArrowIV);
        downArrowIV = view.findViewById(R.id.fragDownArrowIV);

        context = getContext();
        recipe = myServices.XMLToRecipe(context, MyConstants.DOWNLOADED_RECIPE_NAME);
        makeRecyclerView();

        return view;
    }

    public void makeRecyclerView(){

        // Create an instance of your adapter
        customAdapterIngredients adapter = new customAdapterIngredients(context, recipe.getIngredients());


        // Set the layout manager for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
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

    }
}