package com.example.lifesworkiguess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

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
}