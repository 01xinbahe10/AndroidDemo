package com.cdct.cmdim.fragment_interface;

import java.io.File;

/**
 * Created by hxb on 2018/3/30.
 * 处理fragment中产生的数据或是动作
 */

public interface FragmentInterface {
    interface PhoneticsInterface{//录音面板接口
        void phonetics_recording(String file);
    }
    interface FunctionPanelInterface{//功能面板接口
        void functionPanel_image(File file);
        void functionPanel_plan();
        void functionPanel_phrase();
    }
    interface PhraseInterface{//常用语接口
        void phrase_language(String str);
    }
}
