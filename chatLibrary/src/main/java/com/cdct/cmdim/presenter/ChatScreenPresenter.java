package com.cdct.cmdim.presenter;

import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import com.cdct.cmdim.beans.ChatDialogueBean;
import com.cdct.cmdim.contract.ChatScreenContract;
import com.cdct.cmdim.mina_net.ClientMina;
import com.cdct.cmdim.mina_net.model.EditDataModel;
import com.cdct.cmdim.mina_net.model.MsgCodeModel;
import com.cdct.cmdim.model.ChatScreenModel;
import com.cdct.cmdim.utils.RoleUtils;

/**
 * Created by hxb on 2018/3/28.
 */

public class ChatScreenPresenter {
    public static ChatScreenPresenter presenter;
    public final static EditDataModel mEditDataModel;//数据编辑
    public final static SendDataPresenter mSendDataPresenter;//发送数据操类

    private ChatScreenContract.View mView;
    private ChatScreenContract.Model mModel;
    private ChatScreenPresenter(){

    }

    public static ChatScreenPresenter getInstans(ChatScreenContract.View view){
        if (null==presenter){
            presenter = new ChatScreenPresenter();
        }
        //初始化
        presenter.mView = view;
        presenter.mModel = new ChatScreenModel();

        mSendDataPresenter.getmClientMina().setLoginConfig(new ClientMina.LoginConfig() {
//            @Override
//            public Object[] serverAddressConfig() {
//                return presenter.mView.serverAddressConfig();
//            }

            @Override
            public Object senderLogin(String msg) {
//                presenter.mSender = presenter.mView.senderLogin();
                return presenter.mSender;
            }
        });
        presenter.initMsgSendReceipt();
        return presenter;
    }
    static {
        mSendDataPresenter = SendDataPresenter.init();
        mEditDataModel = mSendDataPresenter.getmClientMina().getEditDataModel();
    }



    private Object mSender;//发送者的ID

//    public ChatScreenPresenter(ChatScreenContract.View view) {
//        mView = view;
//        mModel = new ChatScreenModel();
//
//        mSendDataPresenter.getmClientMina().setLoginConfig(new ClientMina.LoginConfig() {
//            @Override
//            public Object[] serverAddressConfig() {
//                return mView.serverAddressConfig();
//            }
//
//            @Override
//            public Object senderLogin(String msg) {
//                mSender = mView.senderLogin();
//                return mSender;
//            }
//        });
//        initMsgSendReceipt();
//    }


    private void initMsgSendReceipt() {
        //发送成功接口回调
        mSendDataPresenter.getmClientMina().setDataStatus(new ClientMina.IsSuccess() {
            @Override
            public void successStatus(Object message) {
                mView.msgSuccessStatus(message);
            }
        });
        //接受消息接口回调
        mSendDataPresenter.getmClientMina().setMsg(new ClientMina.ReceivedMsg() {
            @Override
            public void receivedMsg(MsgCodeModel message) {
                String msgType = null;
                try {
                    JSONObject contentJson = new JSONObject(message.getHeader());
                    msgType = contentJson.getString("msgType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(msgType)) {
                    Log.e("TAG", "ClientMina: " + message.getBody().length);
                    switch (msgType) {
                        case "11":
                            ChatDialogueBean model = new ChatDialogueBean();
                            model.itemType = RoleUtils.TEXT_LEFT;//代表对方
                            model.textContent = new String(message.getBody());
                            mView.receivedMsg(model);
                            break;
                        case "21":
                            ChatDialogueBean file = new ChatDialogueBean();
                            file.itemType = RoleUtils.IMG_LEFT;//代表对方
                            file.file = message.getBody();
                            file.bitmap = BitmapFactory.decodeByteArray(message.getBody(), 0, message.getBody().length);
                            mView.receivedMsg(file);
                            break;
                        case "22":
                            ChatDialogueBean voice = new ChatDialogueBean();
                            voice.itemType = RoleUtils.VOICE_LEFT;//代表对方
                            voice.file = message.getBody();
                            mView.receivedMsg(voice);
                            break;
                        case "31":
                            try {
                                JSONObject jsonObject = new JSONObject(new String(message.getBody()));
                                ChatDialogueBean plan = new ChatDialogueBean();
                                plan.itemType = RoleUtils.PLAN_LEFT;//代表对方
                                plan.setCJHJJ(jsonObject.optString("CJHJJ"));
                                plan.setCJKFABT(jsonObject.optString("CJKFABT"));
                                plan.setCYSMC(jsonObject.optString("CYSMC"));
                                plan.setDCREATTIME(jsonObject.optString("DCREATTIME"));
                                mView.receivedMsg(plan);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        case "43":
                            Log.e("messageSent", "receivedMsg: "+message.getBody() );
                            break;

                    }
                }
            }
        });
    }

    public void sendTextMsg(Object receiver, Object msgTyp, Object fileName, String msg) {
        mModel.sendTextMsg(mView, mSender, receiver, msgTyp, fileName, msg);
    }
    public void sendPicMsg(Object receiver, Object msgTyp, Object fileName, File msg) {
        mModel.sendPicMsg(mView, mSender, receiver, msgTyp, fileName, msg);
    }
    public void sendVoiceMsg(Object receiver, Object msgTyp, Object fileName, File voiceFile) {
        mModel.sendVoiceMsg(mView, mSender, receiver, msgTyp, fileName, voiceFile);
    }

    public void sendPlanMsg(Object receiver, Object msgTyp, Object fileName, String json){
        mModel.sendPlanMsg(mView, mSender, receiver, msgTyp, fileName, json);
    }

    public void closeClient() {
        if (mSendDataPresenter != null) {
            mSendDataPresenter.closeMinaThread();
        }
    }

}
