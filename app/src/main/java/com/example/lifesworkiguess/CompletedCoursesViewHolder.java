package com.example.lifesworkiguess;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CompletedCoursesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView completedCourseTV;
    public OnItemClickListener listener;

    public CompletedCoursesViewHolder(@NonNull View itemView) {
        super(itemView);

        completedCourseTV = itemView.findViewById(R.id.CompletedCoursesTV);
        completedCourseTV.setOnClickListener(this);
    }

    public interface OnItemClickListener {
        void onItemClickCompletedCourses(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;

    }




    @Override
    public void onClick(View view) {
        if (listener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClickCompletedCourses(position);
            }
        }
    }
}
