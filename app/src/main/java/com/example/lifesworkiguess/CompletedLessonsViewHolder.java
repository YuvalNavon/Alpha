/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class is the ViewHolder that is used to show Lessons that the user completed in a selected Course, in a Recycler View.
 */

package com.example.lifesworkiguess;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CompletedLessonsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {


    public ImageView dishIV;
    public TextView lessonNameTV;
    public RatingBar dishRatingRB;
    public LinearLayout LL;
    public OnItemClickListener listener;

    public CompletedLessonsViewHolder(@NonNull View itemView) {
        super(itemView);

        dishIV = itemView.findViewById(R.id.completedLessonDishIV);
        lessonNameTV = itemView.findViewById(R.id.completedLessonNameTV);
        dishRatingRB = itemView.findViewById(R.id.completedLessonRatingRB);
        LL = itemView.findViewById(R.id.completedLessonsLL);

        LL.setOnClickListener(this);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;

    }


    public interface OnItemClickListener {
        void onItemClickCompletedLessons(int position);
    }



    @Override
    public void onClick(View view) {
        if (listener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClickCompletedLessons(position);
            }
        }
    }
}
