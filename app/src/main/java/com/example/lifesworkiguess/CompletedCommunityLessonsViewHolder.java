package com.example.lifesworkiguess;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CompletedCommunityLessonsViewHolder  extends RecyclerView.ViewHolder  implements  View.OnClickListener{

    public ImageView lessonImage, creatorPFP;
    public TextView lessonName, creatorUsername;
    public OnItemClickListener listener;

    public CompletedCommunityLessonsViewHolder(@NonNull View itemView) {
        super(itemView);

        lessonName = itemView.findViewById(R.id.CompletedCommunityLesson_Name);
        lessonImage = itemView.findViewById(R.id.CompletedCommunityLesson_Image);
        creatorUsername = itemView.findViewById(R.id.CompletedCommunityLesson_Username);
        creatorPFP = itemView.findViewById(R.id.CompletedCommunityLesson_PFP);

        lessonName.setOnClickListener(this);
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
