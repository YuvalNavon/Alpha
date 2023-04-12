package com.example.lifesworkiguess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;


public class AddedStepsCustomAdapter extends RecyclerView.Adapter<AddedStepsViewHolder> {

    Context context;
    ArrayList<Step> stepsList;
    OnItemClickListener listener;

    public AddedStepsCustomAdapter(Context context, ArrayList<Step> stepsList, OnItemClickListener listener) {
        this.context = context;
        this.stepsList = stepsList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public AddedStepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.steps_view, parent, false);
        return new AddedStepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddedStepsViewHolder holder, int position) {
       Step currStep = stepsList.get(position);
       String stepNumber = Integer.toString(currStep.getNumber() + 1) ; //Same as just writing "stepNumber = position"
       String stepName = currStep.getName();
       String stepTime = currStep.getTime();


       holder.stepNumber.setText(stepNumber + ".");
       holder.stepName.setText(stepName);
       holder.stepTime.setText(stepTime + " min.");

        holder.setOnItemClickListener(new AddedStepsViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });    }

    @Override
    public int getItemCount() {
        return stepsList.size();
    }










    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
