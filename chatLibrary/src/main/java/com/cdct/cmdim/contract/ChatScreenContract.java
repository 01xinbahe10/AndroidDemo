package com.cdct.cmdim.contract;

import java.io.File;

import com.cdct.cmdim.beans.ChatDialogueBean;

/**
 * Created by hxb on 2018/3/28.
 */

public interface ChatScreenContract {
    interface View{
//        Object[] serverAddressConfig();
//        Object senderLogin();
        void msgSuccessStatus(Object message);
        void receivedMsg(ChatDialogueBean bean);
    }
    interface Model{
       void sendTextMsg(View showView, Object sender, Object receiver, Object msgTyp, Object fileName, String msg);
       void sendPicMsg(View showView, Object sender, Object receiver, Object msgTyp, Object fileName, File msg);
       void sendVoiceMsg(View showView, Object sender, Object receiver, Object msgTyp, Object fileName, File msg);
       void sendPlanMsg(View showView, Object sender, Object receiver, Object msgTyp, Object fileName, String msg);
    }
}
