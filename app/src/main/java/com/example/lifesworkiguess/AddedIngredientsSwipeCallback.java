package com.example.lifesworkiguess;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class AddedIngredientsSwipeCallback extends ItemTouchHelper.SimpleCallback {
    private final customAdapterIngredients mAdapter;

    public AddedIngredientsSwipeCallback(customAdapterIngredients adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            mAdapter = adapter;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                RecyclerView.ViewHolder target) {
            // Do nothing - we're only interested in swipe gestures
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.removeItem(viewHolder.getAdapterPosition());
        }
    }

