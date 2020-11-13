package com.example.aac.test.surfaceview.view.graphics;

/**
 * Created by hxb on  2020/11/10
 */
public abstract class GLStyle {
    private final String TAG = GLStyle.class.getName();

    public abstract void onCreate();

    public abstract void onDraw();

    public abstract void onClear();
}
