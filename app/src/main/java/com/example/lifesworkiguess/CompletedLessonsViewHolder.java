package com.example.lifesworkiguess;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CompletedLessonsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {


    public ImageView dishIV;
    public TextView lessonNameTV;
    public RatingBar dishRatingRB;
    public OnItemClickListener listener;

    public CompletedLessonsViewHolder(@NonNull View itemView) {
        super(itemView);

        dishIV = itemView.findViewById(R.id.completedLessonDishIV);
        lessonNameTV = itemView.findViewById(R.id.completedLessonNameTV);
        dishRatingRB = itemView.findViewById(R.id.completedLessonRatingRB);

        dishIV.setOnClickListener(this);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;

    }


    public interface OnItemClickListener {
        void onItemClick2(int position);
    }



    @Override
    public void onClick(View view) {
        if (listener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick2(position);
            }
        }
    }
}
