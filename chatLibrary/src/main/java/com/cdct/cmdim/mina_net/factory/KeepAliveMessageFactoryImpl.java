package com.cdct.cmdim.mina_net.factory;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

/**
 * Created by hxb on 2017/11/17.
 */

public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory{
    @Override
    public boolean isRequest(IoSession ioSession, Object o) {
//        Log.e("请求心跳包信息", "isRequest: "+o);

        return false;
    }

    @Override
    public boolean isResponse(IoSession ioSession, Object o) {
//        Log.e("响应心跳包信息", "isResponse: "+o);
        return false;
    }

    @Override
    public Object getRequest(IoSession ioSession) {
//        Log.e("请求预设信息", "getRequest: "+10);
        return 10;
    }

    @Override
    public Object getResponse(IoSession ioSession, Object o) {
//        Log.e("响应预设信息", "getResponse: "+10 );
        return 10;
    }
}
