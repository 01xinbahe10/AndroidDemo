package hxb.xb_testandroidfunction.test_custom_view.ListView1_Index;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import androidx.cursoradapter.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by caiying on 03/02/2016.
 *
 * CursorAdapter 适合和列表视图展示数据库 来配合使用 --- hxb
 */
public abstract class IndexBaseCursorAdapter<T, S extends IndexBaseCursorAdapter.ViewHolder, H extends IndexBaseCursorAdapter.ViewHolder> extends CursorAdapter
    implements SectionIndexer, IndexListView.PinnedSectionListAdapter {

    public static abstract class ViewHolder {
        public final View rootView;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
        }

        /**
         * @return Auto-Magicly infers your return type. No casting necessary.
         */
        protected final <X extends View> X findWidgetById(int resId) {
            return (X) rootView.findViewById(resId);
        }
    }

//    public class IllegalCursorMovementException extends RuntimeException {
//
//        public IllegalCursorMovementException(String detailMessage) {
//            super(detailMessage);
//        }
//    }

    private static final String ERROR_ILLEGAL_STATE = "IllegalStateException during build sections. "
        + "More then likely your cursor has been disconnected from the database, so your cursor will be set to null. "
        + "In most cases your content observer has already been notified of a database change and IndexBaseCursorAdapter should get a new cursor shortly.";

    public static final int NO_CURSOR_POSITION = -99; // used when mapping section list position to cursor position

    protected static final int VIEW_TYPE_SECTION = 0;
    protected static final int VIEW_TYPE_ITEM = 1;

    private int mSectionLayoutResId;
    private int mItemLayoutResId;

    protected SortedMap<Integer, T> mSectionMap = new TreeMap<Integer, T>(); // should not be null
    ArrayList<Integer> mSectionList = new ArrayList<Integer>();
    private Object[] mFastScrollObjects;

    private LayoutInflater mLayoutInflater;

    public IndexBaseCursorAdapter(Context context, Cursor cursor, int flags, int sectionLayoutResId, int itemLayoutResId) {
        super(context, cursor, flags);
        init(context, null, sectionLayoutResId, itemLayoutResId);
    }

    protected IndexBaseCursorAdapter(Context context, Cursor c, boolean autoRequery, int sectionLayoutResId, int itemLayoutResId, SortedMap<Integer, T> sections) {
        super(context, c, autoRequery);
        init(context, sections, sectionLayoutResId, itemLayoutResId);
    }

    @Deprecated
    public IndexBaseCursorAdapter(Context context, Cursor cursor, int sectionLayoutResId, int itemLayoutResId) {
        super(context, cursor);
        init(context, null, sectionLayoutResId, itemLayoutResId);
    }

    private void init(Context context, SortedMap<Integer, T> sections, int sectionLayoutResId, int itemLayoutResId) {
        this.mSectionLayoutResId = sectionLayoutResId;
        this.mItemLayoutResId = itemLayoutResId;
        mLayoutInflater = LayoutInflater.from(context);
        if (sections != null) {
            mSectionMap = sections;
        } else {
            buildSections();
        }
    }

    /**
     * @return an inflater to inflate your view with.
     */
    protected LayoutInflater getInflater() {
        return mLayoutInflater;
    }

    /**
     * If the adapter's cursor is not null then this method will call buildSections(Cursor cursor).
     */
    private void buildSections() {
        if (hasOpenCursor()) {
            Cursor cursor = getCursor();
            cursor.moveToPosition(-1);
            try {
                mSectionMap = buildSections(cursor);
            } catch (IllegalStateException e) {
                Log.w(IndexBaseCursorAdapter.class.getName(), ERROR_ILLEGAL_STATE, e);
                swapCursor(null);
                mSectionMap = new TreeMap<Integer, T>();
                return;
            }
            if (mSectionMap == null) {
                mSectionMap = new TreeMap<Integer, T>();
            }
        }
    }

    /**
     * @param cursor a non-null cursor at position -1.
     * @return A map whose keys are the position at which a section is and values are an object
     * which will be passed to newSectionView and bindSectionView
     */
    protected SortedMap<Integer, T> buildSections(Cursor cursor) throws IllegalStateException {
        TreeMap<Integer, T> sections = new TreeMap<Integer, T>();
        int cursorPosition = 0;
        while (hasOpenCursor() && cursor.moveToNext()) {
            T section = getSectionFromCursor(cursor);
            if (cursor.getPosition() != cursorPosition) {
                try {
                    throw new Throwable("Do no move the cursor's position in getSectionFromCursor.");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
            if (!sections.containsValue(section))
                sections.put(cursorPosition + sections.size(), section);
            cursorPosition++;
        }
        return sections;
    }

    /**
     * The object which is return will determine what section this cursor position will be in.
     *
     * @param cursor database cursor
     * @return the section from the cursor at its current position.
     * This object will be passed to newSectionView and bindSectionView.
     */
    protected abstract T getSectionFromCursor(Cursor cursor) throws IllegalStateException;

    ////////////////////////////////////////
    // Implemented Abstract/Overrode methods
    ////////////////////////////////////////

    /**
     * @return How many items are in the data set represented by this Adapter.
     */
    @Override
    public int getCount() {
        return super.getCount() + mSectionMap.size();
    }

    /**
     * @return Returns the number of types of Views that will be created by getView(int, View, ViewGroup).
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * @param listPosition the position of the current item in the list with mSectionMap included
     * @return If the position is a section it will return the value for the position from the section map.
     * Otherwise it will convert to the cursor position and return super.
     */
    @Override
    public Object getItem(int listPosition) {
        if (isSection(listPosition))
            return mSectionMap.get(listPosition);
        else
            return super.getItem(getCursorPositionWithoutSections(listPosition));
    }

    /**
     * @param listPosition the position of the current item in the list with mSectionMap included
     * @return If the position is a section it will return the value for the position from the section map.
     * Otherwise it will return the _id column value.
     */
    @Override
    public long getItemId(int listPosition) {
        if (isSection(listPosition))
            return listPosition;
        else {
            int cursorPosition = getCursorPositionWithoutSections(listPosition);
            Cursor cursor = getCursor();
            if (hasOpenCursor() && cursor.moveToPosition(cursorPosition)) {
                return cursor.getLong(cursor.getColumnIndex("_id"));
            }
            return NO_CURSOR_POSITION;
        }
    }

    /////////////////
    // Managing Data
    /////////////////

    /**
     * Clears out all section data before rebuilding it.
     */
    @Override
    public void notifyDataSetChanged() {
        if (hasOpenCursor()) {
            buildSections();
            mFastScrollObjects = null;
            mSectionList.clear();
        }
        super.notifyDataSetChanged();
    }

    /**
     * Clears out all section data before rebuilding it.
     */
    @Override
    public void notifyDataSetInvalidated() {
        if (hasOpenCursor()) {
            buildSections();
            mFastScrollObjects = null;
            mSectionList.clear();
        }
        super.notifyDataSetInvalidated();
    }

    /**
     * @return True if cursor is not null and open.
     * If the cursor is closed a null cursor will be swapped out.
     */
    protected boolean hasOpenCursor() {
        Cursor cursor = getCursor();
        if (cursor != null && cursor.isClosed()) {
            swapCursor(null);
            return false;
        }
        return cursor != null;
    }

    /////////////////
    // Positions
    /////////////////

    /**
     * @return all of the positions that sections are at.
     */
    public Set<Integer> getSectionListPositions() {
        return mSectionMap.keySet();
    }

    /**
     * @return the section for the given sectionPosition.
     * Null will be returned if the section doesn't exist.
     */
    public T getSection(int sectionPosition) {
        if (mSectionList.contains(sectionPosition)) {
            return mSectionMap.get(mSectionList.get(sectionPosition));
        }
        return null;
    }

    /**
     * @param listPosition the position of the current item in the list with mSectionMap included
     * @return Whether or not the listPosition points to a section.
     */
    public boolean isSection(int listPosition) {
        return mSectionMap.containsKey(listPosition);
    }

    /**
     * This will map a position in the list adapter (which includes mSectionMap) to a position in
     * the cursor (which does not contain mSectionMap).
     *
     * @param listPosition the position of the current item in the list with mSectionMap included
     * @return the correct position to use with the cursor
     */
    public int getCursorPositionWithoutSections(int listPosition) {
        if (mSectionMap.size() == 0) {
            return listPosition;
        } else if (!isSection(listPosition)) {
            int sectionIndex = getSectionPosition(listPosition);
            if (isListPositionBeforeFirstSection(listPosition, sectionIndex)) {
                return listPosition;
            } else {
                return listPosition - (sectionIndex + 1);
            }
        } else {
            return NO_CURSOR_POSITION;
        }
    }

    /**
     * Finds the section index for a given list position.
     *
     * @param listPosition the position of the current item in the list with mSectionMap included
     * @return an index in an ordered list of section names
     */
    public int getSectionPosition(int listPosition) {
        boolean isSection = false;
        int numPrecedingSections = 0;
        for (Integer sectionPosition : mSectionMap.keySet()) {
            if (listPosition > sectionPosition)
                numPrecedingSections++;
            else if (listPosition == sectionPosition)
                isSection = true;
            else
                break;
        }
        return isSection ? numPrecedingSections : Math.max(numPrecedingSections - 1, 0);
    }

    private boolean isListPositionBeforeFirstSection(int listPosition, int sectionIndex) {
        boolean hasSections = mSectionMap != null && mSectionMap.size() > 0;
        return sectionIndex == 0 && hasSections && listPosition < mSectionMap.firstKey();
    }

    /////////////////
    // Views
    /////////////////

    /**
     * @param listPosition position in ListView
     * @return Get the type of View that will be created by getView(int, View, ViewGroup) for the specified item.
     */
    @Override
    public int getItemViewType(int listPosition) {
        return isSection(listPosition) ? VIEW_TYPE_SECTION : VIEW_TYPE_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean isSection = isSection(position);
        Cursor cursor = getCursor();
        View view;

        if (!isSection) {
            int newPosition = getCursorPositionWithoutSections(position);
            if (!hasOpenCursor()) {
                // This only happens when the scroll is super fast and someone backs out.
                return new View(parent.getContext());
            } else if (!cursor.moveToPosition(newPosition)) {
                throw new IllegalStateException("couldn't move cursor to position " + newPosition);
            }
        }

        if (convertView == null) {
            view = isSection ? newSectionView(parent, (T) getItem(position))
                : newItemView(cursor, parent);
        } else {
            view = convertView;
        }

        if (isSection) {
            T section = mSectionMap.get(position);
            bindSectionViewHolder(position, (S) view.getTag(), parent, section);
        } else {
            bindItemViewHolder((H) view.getTag(), cursor, parent);
        }

        return view;
    }

    @Override
    @Deprecated
    /**
     * This method is from the CursorAdapter and will never be called.
     */
    public final View newView(Context context, Cursor cursor, ViewGroup parent) {
        throw new IllegalStateException("This method is not used by " + IndexBaseCursorAdapter.class.getSimpleName());
    }

    @Override
    @Deprecated
    /**
     * This method is from the CursorAdapter and will never be called.
     */
    public final void bindView(View view, Context context, Cursor cursor) {
        throw new IllegalStateException("This method is not used by " + IndexBaseCursorAdapter.class.getSimpleName());
    }

    /**
     * Override to manually create your views. MAKE SURE YOU TAG A ViewHolder TO THIS VIEW!
     * If you do not tag a ViewHolder, the bind methods will give you a null ViewHolder.
     */
    protected View newSectionView(ViewGroup parent, T section) {
        View view = getInflater().inflate(mSectionLayoutResId, parent, false);
        view.setTag(createSectionViewHolder(view, section));

        return view;
    }

    /**
     * @param sectionView the view which was created for this ViewHolder. There is no need to setTag.
     * @param section     is the item stored in the sorted map for the section header.
     * @return The newly created section ViewHolder.
     */
    protected abstract S createSectionViewHolder(View sectionView, T section);

    /**
     * @param position position in ListView
     * @param section           is the item stored in the sorted map for the section header.
     * @param sectionViewHolder the ViewHolder which should have data bound to. This maybe reused and have old data in it.
     * @param parent            the parent view. Typically a ListView.
     */
    protected abstract void bindSectionViewHolder(int position, S sectionViewHolder, ViewGroup parent, T section);

    /**
     * Override to manually create your views. MAKE SURE YOU TAG A ViewHolder TO THIS VIEW!
     * If you do not tag a ViewHolder, the bind methods will give you a null ViewHolder.
     */
    protected View newItemView(Cursor cursor, ViewGroup parent) {
        View view = getInflater().inflate(mItemLayoutResId, parent, false);
        view.setTag(createItemViewHolder(cursor, view));

        return view;
    }

    /**
     * @param cursor   at the correct position for the item.
     * @param itemView the view which was created for this ViewHolder. There is no need to setTag.
     * @return the new created item view.
     */
    protected abstract H createItemViewHolder(Cursor cursor, View itemView);

    /**
     * @param itemViewHolder the ViewHolder which should have data bound to. This maybe reused and have old data in it.
     * @param parent         the parent view. Typically a ListView.
     */
    protected abstract void bindItemViewHolder(H itemViewHolder, Cursor cursor, ViewGroup parent);

    ////////////////////////////////////
    // Methods for the SectionIndexer
    ////////////////////////////////////

    /**
     * Given the index of a section within the array of section objects, returns
     * the starting position of that section within the adapter.
     * 
     * If the section's starting position is outside of the adapter bounds, the
     * position must be clipped to fall within the size of the adapter.
     *
     * @param sectionIndex the index of the section within the array of section
     *                     objects
     * @return the starting position of that section within the adapter,
     * constrained to fall within the adapter bounds
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        if (mSectionList.size() == 0) {
            for (Integer key : mSectionMap.keySet()) {
                mSectionList.add(key);
            }
        }
        return sectionIndex < mSectionList.size() ? mSectionList.get(sectionIndex) : getCount();
    }

    /**
     * Given a position within the adapter, returns the index of the
     * corresponding section within the array of section objects.
     * 
     * If the section index is outside of the section array bounds, the index
     * must be clipped to fall within the size of the section array.
     * 
     * For example, consider an indexer where the section at array index 0
     * starts at adapter position 100. Calling this method with position 10,
     * which is before the first section, must return index 0.
     *
     * @param position the position within the adapter for which to return the
     *                 corresponding section index
     * @return the index of the corresponding section within the array of
     * section objects, constrained to fall within the array bounds
     */
    @Override
    public int getSectionForPosition(int position) {
        Object[] objects = getSections(); // the fast scroll section objects
        int sectionIndex = getSectionPosition(position);

        return sectionIndex < objects.length ? sectionIndex : 0;
    }

    /**
     * Returns an array of objects representing mSectionMap of the list. The
     * returned array and its contents should be non-null.
     * 
     * The list view will call toString() on the objects to get the preview text
     * to display while scrolling. For example, an adapter may return an array
     * of Strings representing letters of the alphabet. Or, it may return an
     * array of objects whose toString() methods return their section titles.
     *
     * @return the array of section objects
     */
    @Override
    public Object[] getSections() {
        if (mFastScrollObjects == null) {
            mFastScrollObjects = getFastScrollDialogLabels();
        }
        return mFastScrollObjects;
    }

    /**
     * This only affects SDK less than 19.
     * Override this to control the amount of characters the fast scroll dialog can display.
     */
    protected int getMaxIndexerLength() {
        return 3;
    }

    /**
     * @return The values which for the sections which will be shown in the fast scroll dialog.
     * As the only a max of three letters can fit in this dialog before KitKat,
     * the string value will be trimmed according to to length specified in getMaxIndexerLength().
     */
    private Object[] getFastScrollDialogLabels() {
        if (mSectionMap == null) return new Object[]{};

        int sectionCount = mSectionMap.size();
        String[] titles = new String[sectionCount];

        int max = Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ? getMaxIndexerLength() : Integer.MAX_VALUE;
        int i = 0;
        for (Object object : mSectionMap.values()) {
            if (object == null) {
                titles[i] = "";
            } else if (object.toString().length() >= max) {
                titles[i] = object.toString().substring(0, max);
            } else {
                titles[i] = object.toString();
            }
            i++;
        }
        return titles;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == VIEW_TYPE_SECTION;
    }
}
