package hxb.xb_testandroidfunction.test_different_display.tv_control.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import hxb.xb_testandroidfunction.R.styleable;


/**
 * 注册关联焦点的view
 */

public class RegLinkFocusView extends View {
    private final String TAG = "RegLinkFocusView";
    private Context mContext;
    private String[] mReferenceIds;
    private SparseArray<LinkFocus> mSparseArray;
    private final String SEPARATOR = ",";
    private LifeCycle mLifeCycle;

    public RegLinkFocusView(Context context) {
        super(context);
        this.mContext = context;
        init(null);
    }

    public RegLinkFocusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);

    }

    public RegLinkFocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE){
//            Log.e(TAG, "onVisibilityChanged: ---------------------/////1111");
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
//            Log.e(TAG, "onVisibilityChanged: ---------------------/////2222");
            setIds(this.mReferenceIds);
        }

        if (null != mLifeCycle){
            mLifeCycle.onCreateFinish(visibility);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        Log.e(TAG, "onFinishInflate:  ---------------------/////3333");

    }

    private void init(AttributeSet attrs) {
        String resIds = null;
        if (attrs != null) {
            this.mSparseArray = new SparseArray<>();
            TypedArray a = this.getContext().obtainStyledAttributes(attrs, styleable.RegLinkFocusView);
            resIds = a.getString(styleable.RegLinkFocusView_regIds);
            a.recycle();
        }

        if (null != resIds) {
            mReferenceIds = resIds.split(SEPARATOR);
        }

    }


    private void setIds(String[] linkIds) {
        if (null == linkIds || linkIds.length == 0) {
            return;
        }
        for (String strId : linkIds) {
            int id = this.mContext.getResources().getIdentifier(strId, "id", this.mContext.getPackageName());
            View view = this.getRootView().findViewById(id);
            LinkFocus linkFocus = new LinkFocus();
            linkFocus.setSelfId(id);
            linkFocus.setUpId(view.getNextFocusUpId());
            linkFocus.setDownId(view.getNextFocusDownId());
            linkFocus.setLeftId(view.getNextFocusLeftId());
            linkFocus.setRightId(view.getNextFocusRightId());
            linkFocus.setSelfList((view instanceof ListView || view instanceof RecyclerView));
//            Log.e(TAG, "setIds: iiiiiiiiiiiiiiii    " + linkFocus.toString());
            mSparseArray.put(id, linkFocus);
        }
    }

    public void setLifeCycle(LifeCycle lifeCycle){
        mLifeCycle = lifeCycle;
    }

    public SparseArray<LinkFocus> getLinkIds() {
        return mSparseArray;
    }



    public final static class LinkFocus implements Serializable {
        private int selfId = View.NO_ID;
        private int upId = View.NO_ID;
        private int downId = View.NO_ID;
        private int leftId = View.NO_ID;
        private int rightId = View.NO_ID;
        private boolean isSelfList = false;

        private LinkFocus() {
        }

        public int getSelfId() {
            return selfId;
        }

        public void setSelfId(int selfId) {
            this.selfId = selfId;
        }

        public int getUpId() {
            return upId;
        }

        public void setUpId(int upId) {
            this.upId = upId;
        }

        public int getDownId() {
            return downId;
        }

        public void setDownId(int downId) {
            this.downId = downId;
        }

        public int getLeftId() {
            return leftId;
        }

        public void setLeftId(int leftId) {
            this.leftId = leftId;
        }

        public int getRightId() {
            return rightId;
        }

        public void setRightId(int rightId) {
            this.rightId = rightId;
        }

        public boolean isSelfList() {
            return isSelfList;
        }

        public void setSelfList(boolean isSelfList) {
            this.isSelfList = isSelfList;
        }

        @Override
        public String toString() {
            return "LinkFocus{" +
                    "selfId=" + selfId +
                    ", upId=" + upId +
                    ", downId=" + downId +
                    ", leftId=" + leftId +
                    ", rightId=" + rightId +
                    ", isSelftList=" + isSelfList +
                    '}';
        }
    }

    public interface LifeCycle{
        void onCreateFinish(int visibility);
    }

}
