package com.example.lifesworkiguess;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomViewHolderIngredients extends RecyclerView.ViewHolder {

    public TextView ingredientName, ingredientAmountandUnits;

    public CustomViewHolderIngredients(@NonNull View itemView) {
        super(itemView);

        ingredientName = itemView.findViewById(R.id.ingredientNameTV);
        ingredientAmountandUnits = itemView.findViewById(R.id.ingredientAmountAndUnitsTV);

    }
}
