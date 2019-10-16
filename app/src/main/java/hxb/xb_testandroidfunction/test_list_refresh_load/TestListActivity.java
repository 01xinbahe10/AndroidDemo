package hxb.xb_testandroidfunction.test_list_refresh_load;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_list_refresh_load.adapter.TestListAdapter;
import hxb.xb_testandroidfunction.test_list_refresh_load.pull_away_pulltorefreshlistview.PullToRefreshBase;
import hxb.xb_testandroidfunction.test_list_refresh_load.pull_away_pulltorefreshlistview.PullToRefreshListView;
import hxb.xb_testandroidfunction.test_list_refresh_load.utils.ChangeAvatarActivity;

/**
 * Created by hxb on 2018/5/29.
 * 测试 ListView的下拉刷新上拉加载
 */

public class TestListActivity extends FragmentActivity implements PullToRefreshListView.OnRefreshListener{
    private PullToRefreshListView mListView;
    private TestListAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.setTheme(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);
        mListView = findViewById(R.id.listView);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(this);
        mAdapter = new TestListAdapter();
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        Log.e("TAG", "onRefresh:当前的模式： "+refreshView.getCurrentMode() );
        stop();
    }

    public void ClickBtn1(View view){
        startActivity(new Intent(this, ChangeAvatarActivity.class));
//        ToastCommom.ToastShow(this, "内容哈哈哈哈哈哈哈哈",3000);
    }

    private void stop(){
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.onRefreshComplete();//结束刷新或是加载
            }
        },2000);
    }
}
