package com.example.lifesworkiguess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class customAdapterIngredients  extends RecyclerView.Adapter<CustomViewHolderIngredients>{

    Context context;
    ArrayList<Ingredient> ingredients;
    OnItemClickListener listener;
    int viewMode;


    public customAdapterIngredients(Context context, ArrayList<Ingredient> ingredients, OnItemClickListener listener, int viewMode ) {
        this.context = context;
        this.ingredients = ingredients;
        this.listener = listener;
        this.viewMode = viewMode;

    }




    @NonNull
    @Override
    public CustomViewHolderIngredients onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ingredient_items, parent, false);
        return new CustomViewHolderIngredients(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolderIngredients holder, int position) {
        String ingredientName = ingredients.get(position).getName();
        String ingredientAmount = ingredients.get(position).getAmount();
        String ingredientUnits = ingredients.get(position).getUnits();
        holder.ingredientName.setText(ingredientName);
        holder.ingredientAmountandUnits.setText(ingredientAmount + " " + ingredientUnits);

        if (viewMode == MyConstants.EDITING_RECIPE)
        {
            holder.setOnItemClickListener(new CustomViewHolderIngredients.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
        }





    }

    public void removeItem(int position) {
        ingredients.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
