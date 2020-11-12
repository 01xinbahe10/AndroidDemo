package com.example.aac.test.surfaceview.view.draw;

import android.opengl.GLES20;

/**
 * Created by hxb on  2020/11/12
 */
public final class DrawManager {
    private static final String TAG = DrawManager.class.getName();
    private volatile static DrawManager manager = null;

    private int width, height;
    private float width1_2, height1_2;//表示二分之一的宽高
    private float[] scaledCoords = {0.0f, 0.0f, 0.0f};//比例坐标x y z 容器（正中）

    public static void init() {
        if (null == manager) {
            manager = new DrawManager();
        }
    }

    private DrawManager() {
    }


    public static void setWidthAndHeight(int width, int height) {
        if (null == manager) {
            return;
        }
        manager.width = width;
        manager.height = height;
        manager.width1_2 = manager.width / 2f;
        manager.height1_2 = manager.height / 2f;
    }

    /**
     * 将触摸的坐标转换为比例坐标X Y Z
     */
    public static float[] convertToScaledCoords(float x, float y, float z) {
        if (null == manager) {
            return null;
        }
        /*
         * x/width :表示用百分比表示点在屏幕x轴上的位置
         * 2f :由于GLES20的坐标采用比例，正中是{0,0,0}到屏幕四角的位置分别是1，所以边长为2
         * x>manager.width1_2?1f:-1f :表示取正负，以正中为基点左边为负右边为正(X)、上边为正下边为负(Y)、
         * 前边为正后边为负(Y)
         * */

        boolean isX = x > manager.width1_2;
        boolean isY = y > manager.height1_2;
        float percentX = x/manager.width * 2f;
        float percentY = y/manager.height * 2f;
        float scaleX,scaleY,scaleZ = 0f;
        if (isX && !isY){//第一象限
            scaleX = 1f  - (percentX - 1f);
            scaleY = 1f - percentY;
        }else if (!isX && !isY){//第二象限
            scaleX = -(1f - percentX);
            scaleY = 1f - percentY;
        }else if (!isX && isY){//第三象限
            scaleX = -(1f -percentX);
            scaleY = -(percentY  -1f);
        }else {//第四象限
            scaleX =  1f - (percentX - 1f);
            scaleY = -(percentY - 1f);
        }

        manager.scaledCoords[0] = scaleX;
        manager.scaledCoords[1] = scaleY;
        manager.scaledCoords[2] = scaleZ;//目前只是2D图形，如以后需要3D再计算
        return manager.scaledCoords;
    }

}
