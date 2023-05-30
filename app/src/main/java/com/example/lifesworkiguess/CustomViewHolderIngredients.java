package com.example.lifesworkiguess;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomViewHolderIngredients extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView ingredientName, ingredientAmountandUnits;
    public OnItemClickListener listener;


    public CustomViewHolderIngredients(@NonNull View itemView) {
        super(itemView);

        ingredientName = itemView.findViewById(R.id.ingredientNameTV);
        ingredientAmountandUnits = itemView.findViewById(R.id.ingredientAmountAndUnitsTV);

        ingredientName.setOnClickListener(this);
        ingredientAmountandUnits.setOnClickListener(this);

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position);
            }
        }
    }

}
