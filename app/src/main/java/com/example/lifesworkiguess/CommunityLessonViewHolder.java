/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class is the ViewHolder that is used to show searched Community Lessons in a Recycler View.
 */

package com.example.lifesworkiguess;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommunityLessonViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{


    public LinearLayout itemLayout;
    public ImageView lessonIV, userIV;
    public TextView lessonNameTV, usernameTV, lessonTimeTV, lessonDifficultyTV, lessonKosherTV, lessonRatingCountTV;
    public RatingBar lessonRB;
    public OnItemClickListener listener;

    public CommunityLessonViewHolder(@NonNull View itemView) {
        super(itemView);

       itemLayout = itemView.findViewById(R.id.CL_ItemLayout);

       lessonIV = itemView.findViewById(R.id.CL_LessonIV);
       userIV = itemView.findViewById(R.id.CL_UserIV);

       lessonNameTV = itemView.findViewById(R.id.CL_LessonNameTV);
       usernameTV = itemView.findViewById(R.id.CL_UsernameTV);
       lessonTimeTV = itemView.findViewById(R.id.CL_TimeTV);
       lessonDifficultyTV = itemView.findViewById(R.id.CL_DifficultyTV);
       lessonKosherTV = itemView.findViewById(R.id.CL_KosherTV);

       lessonRatingCountTV = itemView.findViewById(R.id.CL_RatingCountTV);
       lessonRB = itemView.findViewById(R.id.CL_RatingBar);

       itemLayout.setOnClickListener(this);
    }


    public interface OnItemClickListener {
        void onItemClick2(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @Override
    public void onClick(View v) {
        if (listener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick2(position);
            }
        }
    }
}
