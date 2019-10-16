package hxb.xb_testandroidfunction.test_custom_view.ListView1_Index;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.SectionIndexer;

/**
 * Created by caiying on 03/02/2016.
 */
public class IndexScroller {

    private int indexBarContainerBgColor = Color.BLACK;

    /// end additional properties

    private float mIndexBarWidth;
    private float mIndexBarMargin;
    private float mPreviewPadding;
    private float mDensity;
    private float mScaledDensity;
    private int mState = STATE_HIDDEN;
    private int mListViewWidth;
    private int mListViewHeight;
    private int mCurrentSection = -1;
    private boolean isIndexing = false;
    private ListView mListView = null;
    private RectF mIndexBarRect;

    private static final int STATE_HIDDEN = 0;
    private static final int STATE_SHOWN = 1;

    public IndexScroller(Context context) {
        mDensity = context.getResources().getDisplayMetrics().density;
        mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        mIndexBarWidth = 20 * mDensity;
        mIndexBarMargin = 10 * mDensity;
        mPreviewPadding = 5 * mDensity;
    }

    public void bind(IndexListView listView) {
        mListView = listView;
    }

    // draw the outer rounded container
    private void drawIndexSideBar(Canvas canvas) {
        Paint indexSideBarPaint = new Paint();
        indexSideBarPaint.setAntiAlias(true);
        indexSideBarPaint.setColor(indexBarContainerBgColor);
        indexSideBarPaint.setAlpha(isIndexing ? 64 * mState : 0); // opacity
        canvas.drawRoundRect(mIndexBarRect, 0, 0, indexSideBarPaint);
    }

    private void drawSections(Canvas canvas) {
        Paint indexIndicatorPaint = new Paint();
        indexIndicatorPaint.setAntiAlias(true);
        indexIndicatorPaint.setTextSize(12 * mScaledDensity);
        indexIndicatorPaint.setColor(Color.parseColor("#565656"));
        indexIndicatorPaint.setAlpha(255 * mState);

        float sectionHeight = (mIndexBarRect.height() - 2 * mIndexBarMargin) / getSections().length;
        float paddingTop = (sectionHeight - (indexIndicatorPaint.descent() - indexIndicatorPaint.ascent())) / 2;

        // draw the section letters
        for (int i = 0; i < getSections().length; i++) {
            float paddingLeft = (mIndexBarWidth - indexIndicatorPaint.measureText(getSections()[i])) / 2;
            canvas.drawText(getSections()[i], mIndexBarRect.left + paddingLeft
                , mIndexBarRect.top + mIndexBarMargin + sectionHeight * i + paddingTop - indexIndicatorPaint.ascent(), indexIndicatorPaint);
        }
    }

    private void drawIndexIndicator(Canvas canvas) {
        if (mCurrentSection >= 0) {
            // Preview is shown when mCurrentSection is setPhoto
            // mCurrentSection is the letter that is being pressed
            // this will draw the big preview text on top of the listview
            Paint previewPaint = new Paint();
            previewPaint.setColor(Color.BLACK);
            previewPaint.setAlpha(96);
            previewPaint.setAntiAlias(true);
            previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));

            Paint previewTextPaint = new Paint();
            previewTextPaint.setColor(Color.WHITE);
            previewTextPaint.setAntiAlias(true);
            previewTextPaint.setTextSize(50 * mScaledDensity);

            float previewTextWidth = previewTextPaint.measureText(getSections()[mCurrentSection]);
            float previewSize = 2 * mPreviewPadding + previewTextPaint.descent() - previewTextPaint.ascent();
            RectF previewRect = new RectF((mListViewWidth - previewSize) / 2
                , (mListViewHeight - previewSize) / 2
                , (mListViewWidth - previewSize) / 2 + previewSize
                , (mListViewHeight - previewSize) / 2 + previewSize);

            canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity, previewPaint);
            canvas.drawText(getSections()[mCurrentSection], previewRect.left + (previewSize - previewTextWidth) / 2 - 1
                , previewRect.top + mPreviewPadding - previewTextPaint.ascent() + 1, previewTextPaint);
        }
    }


    public void draw(Canvas canvas) {
        drawIndexSideBar(canvas);

        if (getSections() != null && getSections().length > 0) {
            drawIndexIndicator(canvas);
            drawSections(canvas);
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // If down event occurs inside index bar region, start indexing
                if (mState != STATE_HIDDEN && contains(ev.getX(), ev.getY())) {
                    setState(STATE_SHOWN);

                    // It demonstrates that the motion event started from index bar
                    isIndexing = true;
                    // Determine which section the point is in, and move the list to that section
                    mCurrentSection = getSectionByPoint(ev.getY());
                    mListView.setSelection(((SectionIndexer) mListView.getAdapter()).getPositionForSection(mCurrentSection));
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isIndexing) {
                    // If this event moves inside index bar
                    if (contains(ev.getX(), ev.getY())) {
                        // Determine which section the point is in, and move the list to that section
                        mCurrentSection = getSectionByPoint(ev.getY());
                        mListView.setSelection(((SectionIndexer) mListView.getAdapter()).getPositionForSection(mCurrentSection));
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isIndexing) {
                    isIndexing = false;
                    mCurrentSection = -1;
                }
                break;
        }
        return false;
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        mListViewWidth = w;
        mListViewHeight = h;
        mIndexBarRect = new RectF(w - mIndexBarWidth, 0, w, h);
    }

    public void show() {
        if (mState == STATE_HIDDEN) setState(STATE_SHOWN);
    }

    public void hide() {
        if (mState == STATE_SHOWN) setState(STATE_HIDDEN);
    }

    public boolean isShowing() {
        return mState == STATE_SHOWN;
    }

    public String[] getSections() {
        return (String[]) ((SectionIndexer) mListView.getAdapter()).getSections();
    }

    private void setState(int state) {
        if (state != STATE_HIDDEN && state != STATE_SHOWN)
            return;

        mState = state;
        mListView.invalidate();
    }

    private boolean contains(float x, float y) {
        // Determine if the point is in index bar region, which includes the right margin of the bar
        return (x >= mIndexBarRect.left && y >= mIndexBarRect.top && y <= mIndexBarRect.top + mIndexBarRect.height());
    }

    private int getSectionByPoint(float y) {
        if (getSections() == null || getSections().length == 0)
            return 0;
        if (y < mIndexBarRect.top + mIndexBarMargin)
            return 0;
        if (y >= mIndexBarRect.top + mIndexBarRect.height() - mIndexBarMargin)
            return getSections().length - 1;
        return (int) ((y - mIndexBarRect.top - mIndexBarMargin) / ((mIndexBarRect.height() - 2 * mIndexBarMargin) / getSections().length));
    }
}
