package com.example.lifesworkiguess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CompletedCoursesAdapter extends RecyclerView.Adapter<CompletedCoursesViewHolder> {
    Context context;
    ArrayList<String> coursesNames;
    OnItemClickListener listener;

    public CompletedCoursesAdapter(Context context, ArrayList<String> coursesNames, OnItemClickListener listener) {
        this.context = context;
        this.coursesNames = coursesNames;
        this.listener = listener;
    }


    @NonNull
    @Override
    public CompletedCoursesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.completed_courses_items, parent, false);
        return new CompletedCoursesViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull CompletedCoursesViewHolder holder, int position) {

        //As of BETA 2: No inclusion of Current Course Progress bc shits wack.

        //User has completed a course
        String completedCourse = coursesNames.get(position);
        holder.completedCourseTV.setText(completedCourse);

        holder.setOnItemClickListener(new CompletedCoursesViewHolder.OnItemClickListener() {
                @Override
                public void onItemClickCompletedCourses(int position) {
                    if (listener != null) {
                        listener.onItemClickCompletedCourses(position);
                    }
                }
        });



    }

    @Override
    public int getItemCount() {
        return coursesNames.size();
    }

    public interface OnItemClickListener {
        void onItemClickCompletedCourses(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
