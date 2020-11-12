package com.example.aac.test.surfaceview.view.draw_style;

/**
 * Created by hxb on  2020/11/10
 */
public abstract class GLStyle {
    private final String TAG = GLStyle.class.getName();

    protected GLStyle(){
        GLStyleManager.init();//父类初始化样式管理类
    }
}
