package com.example.lifesworkiguess;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lifesworkiguess.databinding.ActivityFinalCreateRecipeBinding;

import java.util.ArrayList;

public class FinalCreateRecipe extends AppCompatActivity {

    private ActivityFinalCreateRecipeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFinalCreateRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        ArrayList<Fragment> fragmentList = new ArrayList<>();


        ArrayList<String> fragmentTitleList = new ArrayList<>();
        fragmentTitleList.add("Details");
        fragmentTitleList.add("Ingredients");

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, fragmentTitleList);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

    }
}