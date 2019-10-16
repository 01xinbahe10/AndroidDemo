package hxb.xb_testandroidfunction.test_custom_view.adapter1_Index;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_custom_view.ListView1_Index.IndexBaseCursorAdapter;


/**
 * Created by hxb on 2018/12/19
 */
public class IndexAdapter extends IndexBaseCursorAdapter<String, SectionViewHolder, ItemViewHolder>{

    public IndexAdapter(Context context) {
        super(context, null, 0, R.layout.item_row_section, R.layout.item_row_text);
    }

    @Override
    protected String getSectionFromCursor(Cursor cursor) throws IllegalStateException {
        return null;
    }

    @Override
    protected SectionViewHolder createSectionViewHolder(View sectionView, String section) {
        return null;
    }

    @Override
    protected void bindSectionViewHolder(int position, SectionViewHolder sectionViewHolder, ViewGroup parent, String section) {

    }

    @Override
    protected ItemViewHolder createItemViewHolder(Cursor cursor, View itemView) {
        return null;
    }

    @Override
    protected void bindItemViewHolder(ItemViewHolder itemViewHolder, Cursor cursor, ViewGroup parent) {

    }
}
