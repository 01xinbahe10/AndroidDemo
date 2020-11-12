package com.example.aac.test.surfaceview.view.draw;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hxb on  2020/11/11
 */
public class BaseDrawView extends GLSurfaceView {
    private final String TAG = BaseDrawView.class.getName();

    public BaseDrawView(Context context) {
        this(context,null);
    }

    public BaseDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        DrawManager.init();
    }
}
