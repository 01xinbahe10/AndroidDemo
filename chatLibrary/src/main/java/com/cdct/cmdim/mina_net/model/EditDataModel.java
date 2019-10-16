package com.cdct.cmdim.mina_net.model;

import org.apache.mina.core.buffer.IoBuffer;
import org.json.JSONObject;

import java.util.HashMap;

import com.cdct.cmdim.mina_net.utils.ByteUtils;

/**
 * Created by hxb on 2017/11/17.
 *
 */

public class EditDataModel {
    public static EditDataModel init(){
        EditDataModel model = new EditDataModel();
        return model;
    }
    public IoBuffer sendData(Object senderFirst){//首次跟后台建立连接
        HashMap<String,Object> json1 = new HashMap<>();
        json1.put("sender",senderFirst);
        json1.put("receiver","");
        json1.put("msgType",-1);
        json1.put("fileName","");
        json1.put("bodyLength",0);
        JSONObject jsonObject1 = new JSONObject(json1);

        byte[] jsonByte1 = jsonObject1.toString().getBytes();

        int jsonByteLength1 = jsonByte1.length;

        byte[] jsonLengthByte1 = ByteUtils.intToByteArray(jsonByteLength1);

        byte[] all = ByteUtils.unitByteArray(jsonLengthByte1,jsonByte1);

        IoBuffer buffer1 = IoBuffer.allocate(all.length).put(all,0,all.length);
        buffer1.flip();
        return buffer1;
    }

    public IoBuffer sendData(Object sender,Object receiver,Object msgTyp,Object fileName,byte[] body){
        int bodyLength = body.length;
        HashMap<String,Object> contentMap = new HashMap<>();
        contentMap.put("sender",sender);
        contentMap.put("receiver",receiver);
        contentMap.put("msgType",msgTyp);
        contentMap.put("fileName",fileName);
        contentMap.put("bodyLength",bodyLength);
        JSONObject jsonObject = new JSONObject(contentMap);

        byte[] jsonByte = jsonObject.toString().getBytes();
        int jsonByteLength = jsonByte.length;
        byte[] jsonLengthToByte = ByteUtils.intToByteArray(jsonByteLength);
        byte[] jsonBody = ByteUtils.unitByteArray(jsonLengthToByte,jsonByte);
        byte[] all = ByteUtils.unitByteArray(jsonBody,body);

        IoBuffer buffer1 = IoBuffer.allocate(all.length).put(all,0,all.length);
        buffer1.flip();
        return buffer1;

    }
}
