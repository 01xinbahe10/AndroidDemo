package com.cdct.cmdim.mina_net.handler;

import android.util.Log;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.cdct.cmdim.mina_net.model.MsgCodeModel;

/**
 * Created by hxb on 2017/11/17.
 * 消息接收
 */

public class ClientHandlerMsg extends IoHandlerAdapter{
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        Log.e("Handler测试mina", "exceptionCaught: ");
    }

    //收到消息
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);
        MsgCodeModel model = (MsgCodeModel) message;
        if (msg != null){
            msg.receivedMsg(model);
        }
        Log.d("-------", "messageReceived: ");
    }

    //发送成功回调
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        Log.d("Handler测试mina", "messageSent: "+message);
        if (status != null){
            status.successStatus(message);
        }

    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        Log.d("Handler测试mina", "sessionClosed: " );
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        Log.d("Handler测试mina", "sessionCreated: " );
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
        Log.d("Handler测试mina", "sessionIdle: ");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        Log.d("Handler测试mina", "sessionOpened: " );
    }

   public interface IsSuccess{
       void successStatus(Object message);
   }
   private IsSuccess status;

    public void setDataStatus(IsSuccess status) {
        this.status = status;
    }

    private ReceivedMsg msg;
    public interface ReceivedMsg{
        void receivedMsg(MsgCodeModel message);
    }

    public void setMsg(ReceivedMsg msg) {
        this.msg = msg;
    }
}
