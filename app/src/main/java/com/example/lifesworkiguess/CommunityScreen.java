package com.example.lifesworkiguess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CommunityScreen extends AppCompatActivity implements CommunityDishesCustomViewHolder.OnItemClickListener {

    RecyclerView dishesCatagoryRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_screen);

        makeDishCatagoryRV();

        //to make sure everything added from previous custom recipes is deleted:
        deleteEverythingFromLastRecipe();





    }

    @Override
    protected void onResume() {

        super.onResume();

        //to make sure everything added from previous custom recipes is deleted:
        deleteEverythingFromLastRecipe();

    }

    public void deleteEverythingFromLastRecipe(){

        //General - no need, as its the first screen in making recipes so if its closed then the recipe should be gone

        //Image
        File recipeImageFile = new File(getFilesDir(), MyConstants.IMAGE_FILE_NAME);
        if (recipeImageFile.exists()) {
            recipeImageFile.delete();
        }

        //Ingredients, Steps and Extra Info
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        editor.putString(MyConstants.CUSTOM_RECIPE_INGREDIENTS, null);
        editor.putString(MyConstants.CUSTOM_RECIPE_STEPS, null);
        editor.putInt(MyConstants.CUSTOM_RECIPE_HOURS_SPINNER_CURR_POS, MyConstants.CUSTOM_RECIPE_NO_SPINNER_POS_SAVED);
        editor.putInt(MyConstants.CUSTOM_RECIPE_MINUTES_SPINNER_CURR_POS, MyConstants.CUSTOM_RECIPE_NO_SPINNER_POS_SAVED);
        editor.putString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL, null);
        editor.putInt(MyConstants.CUSTOM_RECIPE_SERVE_COUNT, 0);
        editor.putBoolean(MyConstants.CUSTOM_RECIPE_KOSHER, false);
        editor.commit();



    }

    public void makeDishCatagoryRV(){


        dishesCatagoryRV = findViewById(R.id.catagoryDishesRV);

        CommunityDishesAdapter communityDishesAdapter = new CommunityDishesAdapter(CommunityScreen.this,
                MyConstants.dishCatagoryNames, MyConstants.dishCatagoryLogoNames, this::onItemClick);

        // Set the layout manager for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CommunityScreen.this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        dishesCatagoryRV.setLayoutManager(linearLayoutManager);

        // Set the adapter for the RecyclerView
        dishesCatagoryRV.setAdapter(communityDishesAdapter);
    }

    @Override
    public void onItemClick(int position) {

        String dishCatagoryName = MyConstants.dishCatagoryNames[position];
        
    }


    public void toCreateRecipe(View view){
        Intent toCreateRecipeScreen = new Intent(this, TrulyFinalCreateRecipeGeneral.class);
        startActivity(toCreateRecipeScreen);
    }
}