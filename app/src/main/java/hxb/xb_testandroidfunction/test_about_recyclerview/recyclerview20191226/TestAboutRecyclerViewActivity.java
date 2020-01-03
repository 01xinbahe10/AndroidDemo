package hxb.xb_testandroidfunction.test_about_recyclerview.recyclerview20191226;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_about_recyclerview.recyclerview20191226.tvlistview.CustomizeGridRecyclerView;
import hxb.xb_testandroidfunction.test_about_recyclerview.recyclerview20191226.tvlistview.OnChildSelectedListener;

/**
 * Created by hxb on  2019/12/26
 */
public class TestAboutRecyclerViewActivity extends FragmentActivity implements View.OnFocusChangeListener {
    private static final String TAG = "TestAboutRecyclerView";

    private Button mBtn1,mBtn2,mBtn3,mBtn4;
    private ListView mListView;
    private CustomizeGridRecyclerView mRecyclerView;
    private ListViewAdapter mListAdapter;
    private RecyclerAdapter mAdapter;
    private View mViewMainVideo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_recyclerview2);
        mBtn1 = findViewById(R.id.btn1);
        mBtn2 = findViewById(R.id.btn2);
        mBtn3 = findViewById(R.id.btn3);
        mBtn4 = findViewById(R.id.btn4);
        mListView = findViewById(R.id.listView);
        mListView.setItemsCanFocus(true);
        mRecyclerView = findViewById(R.id.recyclerView);
        mViewMainVideo = findViewById(R.id.viewMainVideo);

        mListAdapter = new ListViewAdapter();
        mListView.setAdapter(mListAdapter);

        mRecyclerView.managerConfig().setOrientation(RecyclerView.VERTICAL).setSpanCount(2).done();
        mAdapter = new RecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setCanFocusOutVertical(false);
        mRecyclerView.setCanFocusOutHorizontal(true);

        mBtn1.setOnFocusChangeListener(this);
        mBtn2.setOnFocusChangeListener(this);
        mBtn3.setOnFocusChangeListener(this);
        mBtn4.setOnFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.btn1:
                mBtn1.setBackgroundColor(hasFocus?Color.RED:Color.WHITE);
                break;
            case R.id.btn2:
                mBtn2.setBackgroundColor(hasFocus?Color.RED:Color.WHITE);
                break;
            case R.id.btn3:
                mBtn3.setBackgroundColor(hasFocus?Color.RED:Color.WHITE);
                break;
            case R.id.btn4:
                mBtn4.setBackgroundColor(hasFocus?Color.RED:Color.WHITE);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerView.setOnChildSelectedListener(new OnChildSelectedListener() {
            @Override
            public void onChildSelected(ViewGroup parent, View view, int position, long id) {
                Log.e(TAG, "onChildSelected: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  " +position);
            }
        });

        mRecyclerView.setGainFocusListener(new CustomizeGridRecyclerView.FocusGainListener() {
            @Override
            public void onFocusGain(View child, View focued) {
                Log.e(TAG, "onFocusGain: >>>>>>>>>  焦点进入    " );
            }
        });

        mRecyclerView.setFocusLostListener(new CustomizeGridRecyclerView.FocusLostListener() {
            @Override
            public void onFocusLost(View lastFocusChild, int direction) {
                Log.e(TAG, "onFocusLost: >>>>>>>>>>  焦点移出   " );
            }
        });

        mRecyclerView.setOnScrolledListener(new CustomizeGridRecyclerView.OnScrolledListener() {
            @Override
            public void onScrolled(int scrolled) {
                String text = "";
                switch (scrolled){
                    case SCROLLED_START:
                        text = "滚动到顶";
                        if (mViewMainVideo.getVisibility() == View.INVISIBLE) {
                            mViewMainVideo.setVisibility(View.VISIBLE);
                        }
                        break;
                    case SCROLLED_END:
                        text = "滚动到底";
                        break;
                    case CAN_SCROLLED:
                        text = "可以滚动";
                        if (mViewMainVideo.getVisibility() == View.VISIBLE) {
                            mViewMainVideo.setVisibility(View.INVISIBLE);
                        }
                        break;
                }

                Log.e(TAG, "onScrolled: >>>>>>>>>>>>> 滚动的状态 = "+text );
            }
        });

    }
}
