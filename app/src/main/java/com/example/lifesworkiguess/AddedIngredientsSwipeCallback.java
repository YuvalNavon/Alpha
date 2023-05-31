/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class is used to add swipe Functionality to a Recycler View.
 */

package com.example.lifesworkiguess;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class AddedIngredientsSwipeCallback extends ItemTouchHelper.SimpleCallback {
    private final customAdapterIngredients mAdapter;

    public AddedIngredientsSwipeCallback(customAdapterIngredients adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            mAdapter = adapter;
        }

    /**
     * this function does nothing, but it has to be implemented.
     * <p>
     *
     * @param	recyclerView - The recyclerView used.
     *          viewHolder - the item dragged in the recyclerView.
     *          target - the item dragged to in the recyclerView.
     *
     * @return	false
     */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                RecyclerView.ViewHolder target) {
            // Do nothing - we're only interested in swipe gestures
            return false;
        }
    /**
     * this function removes a swiped item from the data set of the adapter that this Callback Class was attached to.
     * <p>
     *
     * @param   viewHolder - the item swiped in the recyclerView.
     *          direction - the direction of the swipe.
     *
     * @return	None
     */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.removeItem(viewHolder.getAdapterPosition());
        }
    }

