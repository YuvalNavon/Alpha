/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class is the ViewHolder that is used to show active CommunityLessons made by the user in a Recycler View (In the HomeScreen Activity).
 */


package com.example.lifesworkiguess;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MadeCommunityLessonsViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView lessonImage, lessonOptionsImage;
    public TextView lessonName;
    public LinearLayout LL;
    public OnItemClickListener listener;

    public MadeCommunityLessonsViewHolder(@NonNull View itemView) {
        super(itemView);

        LL = itemView.findViewById(R.id.madeCommunityLessonLL);
        lessonImage = itemView.findViewById(R.id.communityLessonIV);
        lessonOptionsImage = itemView.findViewById(R.id.communityLessonOptionsIV);
        lessonName =  itemView.findViewById(R.id.communityLessonName);

        lessonOptionsImage.setOnClickListener(this);

    }



    public interface OnItemClickListener {
        void onItemClickUploadedRecipes(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClickUploadedRecipes(position);
            }
        }
    }
}
