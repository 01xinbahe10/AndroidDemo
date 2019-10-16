package com.example.testjpeg;

import android.graphics.Bitmap;

/**
 * Created by hxb on 2019-07-08.
 */
public class MineJni {
    static {
        System.loadLibrary("native_libs");
    }

    public static native void compress(Bitmap bitmap, String path);
}
