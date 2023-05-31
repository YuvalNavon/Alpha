/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class is the ViewHolder that is used to Reviews, Ratings, and Images of finished Community Recipes in a Recycler View.
 */

package com.example.lifesworkiguess;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomRatingsViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

    public LinearLayout ReviewLL;
    public ScrollView ReviewSV;
    public TextView ReviewUsernameTV, ReviewTV;
    public ImageView ReviewPFP, ReviewUploadedDishIV;
    public RatingBar ReviewRB;
    public OnItemClickListener listener;

    public CustomRatingsViewHolder(@NonNull View itemView) {
        super(itemView);

        ReviewLL = itemView.findViewById(R.id.RatingsItem_LL);
        ReviewSV = itemView.findViewById(R.id.RatingsItem_ReviewSV);
        ReviewTV = itemView.findViewById(R.id.RatingsItem_ReviewTV);
        ReviewUsernameTV = itemView.findViewById(R.id.RatingsItem_UsernameTV);
        ReviewPFP = itemView.findViewById(R.id.RatingsItem_PFPIV);
        ReviewUploadedDishIV = itemView.findViewById(R.id.RatingsItem_UploadedDishIV);
        ReviewRB = itemView.findViewById(R.id.RatingsItem_RB);
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
