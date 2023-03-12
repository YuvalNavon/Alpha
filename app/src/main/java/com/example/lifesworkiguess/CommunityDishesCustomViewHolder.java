package com.example.lifesworkiguess;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommunityDishesCustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView dishLogo;
    public TextView dishName;
    public CustomViewHolder.OnItemClickListener listener;

    public CommunityDishesCustomViewHolder(@NonNull View itemView) {
        super(itemView);

        dishLogo = itemView.findViewById(R.id.communityDishIV);
        dishName = itemView.findViewById(R.id.communityDishTV);

        dishLogo.setOnClickListener(this);
        dishName.setOnClickListener(this);


    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(CustomViewHolder.OnItemClickListener listener) {
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
