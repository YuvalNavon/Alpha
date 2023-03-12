package com.example.lifesworkiguess;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommunityDishesAdapter extends RecyclerView.Adapter<CommunityDishesCustomViewHolder> {

    Context context;
    String[] dishNames;
    String[] dishLogos;
    OnItemClickListener listener;


    public CommunityDishesAdapter(Context context,String[] dishNames, String[] dishLogos, OnItemClickListener listener ) {
        this.context = context;
        this.dishNames = dishNames;
        this.dishLogos = dishLogos;
        this.listener = listener;

    }

    @NonNull
    @Override
    public CommunityDishesCustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.community_general_dishes, parent, false);
        return new CommunityDishesCustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityDishesCustomViewHolder holder, int position) {

        String dishName = dishNames[position];
        holder.dishName.setText(dishName);

        String dishLogoName = dishLogos[position];
        int dishLogoResourceID = context.getResources().getIdentifier(dishLogoName, "drawable", context.getPackageName());
        Drawable d = ContextCompat.getDrawable(context, dishLogoResourceID);
        holder.dishLogo.setImageDrawable(d);


        holder.setOnItemClickListener(new CustomViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishNames.length;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
