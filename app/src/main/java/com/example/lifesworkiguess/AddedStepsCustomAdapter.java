/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class is the Adapter that is used to show added Steps in a Recycler View.
 */

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
    StepsViewModel stepsViewModel;
    OnItemClickListener listener;
    ArrayList<Step> stepsList;

    public AddedStepsCustomAdapter(Context context,  StepsViewModel stepsViewModel, OnItemClickListener listener) {
        this.context = context;
        this.stepsViewModel = stepsViewModel;
        this.listener = listener;
        stepsList = stepsViewModel.getStepsList().getValue();

    }

    public AddedStepsCustomAdapter(Context context,  ArrayList<Step> stepsList, OnItemClickListener listener) {
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


    /**
     * this function sets every Step's number to its index in the stepsList
     * <p>
     *
     * @param
     *
     * @return	None
     */
    public void updateStepNumbers(){
        for (int i = 0; i<stepsList.size(); i++)
        {
            Step currStep = stepsList.get(i);
            currStep.setNumber(i);
            stepsList.set(i, currStep);
        }
        if (stepsViewModel!=null) stepsViewModel.setStepsList(stepsList);

    }

    /**
     * this function swaps 2 Steps positions in the stepsList
     * <p>
     *
     * @param fromPosition - the position of the item that was dragged.
     *        toPosition - the position the Item was dragged to.
     *
     * @return	None
     */
    public void swapItems(int fromPosition, int toPosition) {
        // Update the data set to reflect the new item positions
        Collections.swap(stepsList, fromPosition, toPosition);
        // Notify the adapter that the data set has changed
        notifyItemMoved(fromPosition, toPosition);
        updateStepNumbers();
        if (stepsViewModel!=null) stepsViewModel.setStepsList(stepsList);


    }

    /**
     * this function removes a swiped Step from the stepsList
     * <p>
     *
     * @param position - the position of the item that was swiped.
     *
     *
     * @return	None
     */
    public void removeItem(int position) {
        // Remove the item from the data set
        stepsList.remove(position);
        // Notify the adapter that the item has been removed
        notifyItemRemoved(position);
        updateStepNumbers();
    }




    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
