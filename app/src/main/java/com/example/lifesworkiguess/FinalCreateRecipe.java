package com.example.lifesworkiguess;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.lifesworkiguess.databinding.ActivityFinalCreateRecipeBinding;

public class FinalCreateRecipe extends AppCompatActivity {

    private ActivityFinalCreateRecipeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFinalCreateRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




    }
}