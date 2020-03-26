package com.example.aac.test.rvpage.view;

import androidx.recyclerview.widget.RecyclerView;

/**
 * A PageIndicator is responsible to show an visual indicator on the total views
 * number and the current visible view.
 */
public interface PageIndicator extends OnPageChangeListener {
    /**
     * Bind the indicator to a RecyclerView.
     *
     * @param view
     */
    void setRecyclerView(RecyclerView view);

    /**
     * Bind the indicator to a RecyclerView.
     *
     * @param view
     * @param initialPosition
     */
    void setRecyclerView(RecyclerView view, int initialPosition);

    /**
     * <p>Set the current page of both the RecyclerView and indicator.</p>
     * <p>
     * <p>This <strong>must</strong> be used if you need to set the page before
     * the views are drawn on screen (e.g., default start page).</p>
     *
     * @param item
     */
    void setCurrentItem(int item);

    /**
     * Set a page change listener which will receive forwarded events.
     *
     * @param listener
     */
    void setOnPageChangeListener(OnPageChangeListener listener);

    /**
     * Notify the indicator that the fragment list has changed.
     */
    void notifyDataSetChanged();

    /**
     * Set a page column
     *
     * @param column
     */
    void setPageColumn(int column);
}
