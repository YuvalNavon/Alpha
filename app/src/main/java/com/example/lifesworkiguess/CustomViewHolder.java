/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class is the ViewHolder that is used to show PermanentLessons in a Recycler View (In the HomeScreen Activity).
 */


package com.example.lifesworkiguess;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView logo;
    public TextView lessonName;
    public OnItemClickListener listener;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);

        logo = itemView.findViewById(R.id.lessonLogoIV);
        lessonName = itemView.findViewById(R.id.lessonNameTV);

        logo.setOnClickListener(this);
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
