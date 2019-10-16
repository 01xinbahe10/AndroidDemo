package com.example.aac.base_frame.sample_adapter;

/**
 * Callback for setting up a {@link ItemBinding} for an item in the collection.
 *
 * @param <T> the item type
 */
public interface OnItemBind<VM> {
    /**
     * Called on each item in the collection, allowing you to modify the given {@link ItemBinding}.
     * Note that you should not do complex processing in this method as it's called many times.
     */
    void onItemBind(ItemBinding itemBinding, int position, VM item);
}