package com.cdct.cmdim.model;


import org.apache.mina.core.buffer.IoBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.cdct.cmdim.contract.ChatScreenContract;
import com.cdct.cmdim.presenter.ChatScreenPresenter;

/**
 * Created by hxb on 2018/3/28.
 */

public class ChatScreenModel implements ChatScreenContract.Model {

    @Override
    public void sendTextMsg(final ChatScreenContract.View showView, Object sender, Object receiver, Object msgTyp, Object fileName, final String msg) {
        IoBuffer ioBuffer = ChatScreenPresenter.mEditDataModel.sendData(sender, receiver, msgTyp, fileName, msg.getBytes());
        ChatScreenPresenter.mSendDataPresenter.sendOBDData(ioBuffer);
    }

    @Override
    public void sendPicMsg(ChatScreenContract.View showView, Object sender, Object receiver, Object msgTyp, Object fileName, File msg) {
        try {
            FileInputStream fis = new FileInputStream(msg);
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) msg.length());
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            byte[] data = bos.toByteArray();
            IoBuffer ioBuffer = ChatScreenPresenter.mEditDataModel.sendData(sender, receiver, msgTyp, fileName, data);
            ChatScreenPresenter.mSendDataPresenter.sendOBDData(ioBuffer);
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendVoiceMsg(ChatScreenContract.View showView, Object sender, Object receiver, Object msgTyp, Object fileName, File voiceFile) {
        try {
            FileInputStream fis = new FileInputStream(voiceFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) voiceFile.length());
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            byte[] data = bos.toByteArray();
            IoBuffer ioBuffer = ChatScreenPresenter.mEditDataModel.sendData(sender, receiver, msgTyp, fileName, data);
            ChatScreenPresenter.mSendDataPresenter.sendOBDData(ioBuffer);
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPlanMsg(ChatScreenContract.View showView, Object sender, Object receiver, Object msgTyp, Object fileName, String json) {
        IoBuffer ioBuffer = ChatScreenPresenter.mEditDataModel.sendData(sender, receiver, msgTyp, fileName, json.getBytes());
        ChatScreenPresenter.mSendDataPresenter.sendOBDData(ioBuffer);
    }

}
