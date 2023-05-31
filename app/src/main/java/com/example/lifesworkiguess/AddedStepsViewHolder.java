/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class is the ViewHolder that is used to show added Steps in a Recycler View.
 */

package com.example.lifesworkiguess;

import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AddedStepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView stepNumber, stepName, stepTime ;
    public LinearLayout linearLayout;
    public OnItemClickListener listener;


    public AddedStepsViewHolder(@NonNull View itemView) {
        super(itemView);

        stepNumber = itemView.findViewById(R.id.ViewStepsCR_stepNumberTV);
        stepName = itemView.findViewById(R.id.ViewStepsCR_stepNameTV);
        stepTime = itemView.findViewById(R.id.ViewStepsCR_stepTimeTV);
        linearLayout = itemView.findViewById(R.id.ViewStepsCR_LL);

        linearLayout.setOnClickListener(this);

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position);
            }
        }
    }
}
