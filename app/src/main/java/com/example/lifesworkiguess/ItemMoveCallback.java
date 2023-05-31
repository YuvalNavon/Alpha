/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class is used to add swipe and drag & drop Functionalities to a Recycler View.
 */


package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMoveCallback extends ItemTouchHelper.Callback {

    private final AddedStepsCustomAdapter mAdapter; //Completely copied from ChatGPT

    public ItemMoveCallback(AddedStepsCustomAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }


    /**
     * this function swaps a dragged item in a RecyclerView with the item it was dragged to.
     * <p>
     *
     * @param	recyclerView - The recyclerView used.
     *          viewHolder - the item dragged in the recyclerView.
     *          target - the item dragged to in the recyclerView.
     *
     * @return	true
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.swapItems(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
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
