package hxb.xb_testandroidfunction.test_different_display;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_different_display.presentation.VideoPresentation;

/**
 * 测试Android多屏异显示功能
 * */
public class DifferentDisplayActivity extends AppCompatActivity implements View.OnClickListener {

    private Display[] displays;
    private VideoPresentation presentation;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.displays = ((DisplayManager)getSystemService(Context.DISPLAY_SERVICE)).getDisplays();
        setContentView(R.layout.activity_different_display);
        findViewById(R.id.ok).setOnClickListener(this);
        findViewById(R.id.up).setOnClickListener(this);
        findViewById(R.id.down).setOnClickListener(this);
        findViewById(R.id.left).setOnClickListener(this);
        findViewById(R.id.right).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);


        if (displays.length > 1){
            if (this.presentation == null) {
                this.presentation = new VideoPresentation(this, displays[1]);
            }
            if (!this.presentation.isShowing()) {
                this.presentation.show();
            }
        }

    }

    @Override
    public void onClick(View v) {
        Event.ItemEvent itemEvent = VideoPresentation.getItemEvent();
        if (null == itemEvent){
            return;
        }
        switch (v.getId()){
            case R.id.ok:
                itemEvent.onItemClick(Event.ItemEvent.OK);
                break;
            case R.id.up:
                itemEvent.onItemClick(Event.ItemEvent.UP);
                break;
            case R.id.down:
                itemEvent.onItemClick(Event.ItemEvent.DOWN);
                break;
            case R.id.left:
                itemEvent.onItemClick(Event.ItemEvent.LEFT);
                break;
            case R.id.right:
                itemEvent.onItemClick(Event.ItemEvent.RIGHT);
                break;
            case R.id.exit:
                itemEvent.onItemClick(Event.ItemEvent.EXIT);
                break;
        }
    }
}
