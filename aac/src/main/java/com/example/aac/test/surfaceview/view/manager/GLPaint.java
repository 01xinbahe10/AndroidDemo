package com.example.aac.test.surfaceview.view.manager;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;

/**
 * Created by hxb on  2020/11/25
 * 画图样式配置类
 */
public final class GLPaint {
    private static volatile GLPaint glPaint = null;

    private Graphic paintStyle = Graphic.NO_STYLE;//绘画样式
    private float[] color = {0, 0, 0, 1};//默认色
    private float size = 1f;//大小

    private GLPaint() {
    }

    public static GLPaint init() {
        if (null == glPaint) {
            synchronized (GLPaint.class) {
                if (null == glPaint) {
                    glPaint = new GLPaint();
                }
            }
        }
        return glPaint;
    }


    public static void setPaintStyle(Graphic graphic) {
        if (null == glPaint){
            return;
        }
        glPaint.paintStyle = graphic;
    }

    public static Graphic getPaintStyle(){
        if (null == glPaint){
            return Graphic.NO_STYLE;
        }
        return glPaint.paintStyle;
    }


    public static void setColor(@IntRange(from = 0, to = 255) int r, @IntRange(from = 0, to = 255) int g, @IntRange(from = 0, to = 255) int b, @FloatRange(from = 0f, to = 1f) float a) {
        if (null == glPaint){
            return;
        }
        glPaint.color[0] = r;
        glPaint.color[1] = g;
        glPaint.color[2] = b;
        glPaint.color[3] = a;
    }

    public static float[] getColor(){
        if (null == glPaint){
            return new float[]{0f,0f,0f,1f};
        }

        return glPaint.color;
    }

    public static void setSize(float size){
        if (null == glPaint){
            return;
        }
        if (size <= 0){
            size = 0.1f;
        }
        glPaint.size = size;
    }

    public static float getSize(){
        if (null == glPaint){
            return 0.1f;
        }
        return glPaint.size;
    }

    /**
     * 图形样式状态
     */
    public enum Graphic {
        NO_STYLE(-1)
        , POINT(0)//点
        , LINE(1)//线
        , TRIANGLE(2)//三角形
        , QUADRILATERAL(3)//四边形
        , TEXTURE(4)//纹理
        , ERASER(5)//橡皮擦
        ;

        int v;

        Graphic(int i) {
            v = i;
        }
    }


}
