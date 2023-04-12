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

    public customAdapterIngredients(Context context, ArrayList<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;

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
        holder.ingredientName.setText(position+1 + ". " + ingredientName);
        holder.ingredientAmountandUnits.setText(ingredientAmount + " " + ingredientUnits);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}
