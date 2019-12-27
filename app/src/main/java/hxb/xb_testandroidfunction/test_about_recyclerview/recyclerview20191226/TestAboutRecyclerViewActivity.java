package hxb.xb_testandroidfunction.test_about_recyclerview.recyclerview20191226;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hxb.xb_testandroidfunction.R;

/**
 * Created by hxb on  2019/12/26
 */
public class TestAboutRecyclerViewActivity extends FragmentActivity implements View.OnFocusChangeListener {

    private Button mBtn1,mBtn2,mBtn3,mBtn4;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_recyclerview2);
        mBtn1 = findViewById(R.id.btn1);
        mBtn2 = findViewById(R.id.btn2);
        mBtn3 = findViewById(R.id.btn3);
        mBtn4 = findViewById(R.id.btn4);
        mRecyclerView = findViewById(R.id.recyclerView);

        CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(this,2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mAdapter = new RecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

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
}
