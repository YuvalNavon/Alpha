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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IngredientsListFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IngredientsListFrag extends Fragment {

    Recipe recipe;
    RecyclerView ingredientsRV;
    ImageView upArrowIV, downArrowIV;
    Context context;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IngredientsListFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IngredientsListFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static IngredientsListFrag newInstance(String param1, String param2) {
        IngredientsListFrag fragment = new IngredientsListFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        recipe = myServices.XMLToRecipe(context, MyConstants.CURRENTLY_LEARNED_RECIPE);
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