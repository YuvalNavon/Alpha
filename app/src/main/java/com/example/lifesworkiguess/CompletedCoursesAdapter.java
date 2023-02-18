package com.example.lifesworkiguess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class CompletedCoursesAdapter extends RecyclerView.Adapter<CompletedCoursesViewHolder> {
    Context context;
    User user;
    OnItemClickListener listener;

    public CompletedCoursesAdapter(Context context, User user, OnItemClickListener listener) {
        this.context = context;
        this.user = user;
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
        String completedCourse =user.getCompletedCourses().get(position);
        holder.completedCourseTV.setText(completedCourse);

        holder.setOnItemClickListener(new CompletedCoursesViewHolder.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (listener != null) {
                        listener.onItemClick(position);
                    }
                }
        });



    }

    @Override
    public int getItemCount() {
        return user.getCompletedCourses().size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
