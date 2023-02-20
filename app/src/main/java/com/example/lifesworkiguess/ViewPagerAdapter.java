package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragmentList = new ArrayList<>();
    private final ArrayList<String> fragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList, ArrayList<String> fragmentTitleList) {
        super(fm);
        this.fragmentList.addAll(fragmentList);
        this.fragmentTitleList.addAll(fragmentTitleList);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }
}
