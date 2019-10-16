package com.cdct.cmdim.mina_net;


import android.util.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

import com.cdct.cmdim.mina_net.factory.CodingProtocol;
import com.cdct.cmdim.mina_net.factory.KeepAliveMessageFactoryImpl;
import com.cdct.cmdim.mina_net.handler.ClientHandlerMsg;
import com.cdct.cmdim.mina_net.model.EditDataModel;
import com.cdct.cmdim.mina_net.model.MsgCodeModel;
import com.cdct.cmdim.utils.RoleUtils;

/**
 * Created by hxb on 2017/11/17.
 */

public class ClientMina extends Thread {
    public static ClientMina clientMina;
    private static String TAG = "[CDCT]";
    //30秒后超时
    private static final int IDELTIMEOUT = 10;
    //15秒发送一次心跳包
    private static final int HEARTBEATRATE = 10;

    private ClientHandlerMsg clientHandlerMsg;
    private ConnectFuture connectFuture;
    private IoSession session;
    private IoConnector connector;

    private EditDataModel mEditDataModel;

    private ClientMina() {
    }

    public static ClientMina getIntrans() {
        if (null == clientMina) {
            clientMina = new ClientMina();
        }
        clientMina.mEditDataModel = EditDataModel.init();

        return clientMina;
    }

//    public static ClientMina init() {
//        ClientMina clientMina = new ClientMina();
//        clientMina.mEditDataModel = EditDataModel.init();
//        return clientMina;
//    }

    public EditDataModel getEditDataModel() {
        return mEditDataModel;
    }

    @Override
    public void run() {
        super.run();
        initialize();
        connection();
        minaClose();

    }

    public void setupConnectFuture() {//设置服务地址
        if (connectFuture == null && null != loginConfig) {
//            Object[] serverConfig = loginConfig.serverAddressConfig();
//            connectFuture = connector.connect(new InetSocketAddress(SERVER_ADDRESS, SERVER_POST) );
            connectFuture = connector.connect(new InetSocketAddress(RoleUtils.ip, RoleUtils.port));
        }
        return;
    }

    /**
     * 设置外部回调
     */
    public void initialize() {
        /* ClientHandlerMsg */
        if (clientHandlerMsg == null) {
            clientHandlerMsg = new ClientHandlerMsg();
            clientHandlerMsg.setDataStatus(new ClientHandlerMsg.IsSuccess() {
                @Override
                public void successStatus(Object message) {
                    if (status != null) {
                        status.successStatus(message);
                    }
                }
            });
            clientHandlerMsg.setMsg(new ClientHandlerMsg.ReceivedMsg() {
                @Override
                public void receivedMsg(MsgCodeModel message) {
                    if (msg != null) {
                        msg.receivedMsg(message);
                    }
                }
            });
        }
    }

    private void connection() {
        if (connector == null) {
            connector = new NioSocketConnector();
            /* 添加过滤器 */
            connector.getFilterChain().addLast("logger", new LoggingFilter());
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CodingProtocol()));
            /* ReadBufferSize */
            connector.getSessionConfig().setReadBufferSize(1024 * 10);
            /* 设置链接超时时间 */
            connector.setConnectTimeoutMillis(10 * 1000);
            /* timeout for read and write */
            connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDELTIMEOUT);
            //设置心跳工程
            KeepAliveMessageFactory heartBeatFactory = new KeepAliveMessageFactoryImpl();
            //// 当读操作空闲时发送心跳
            KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory);
            // 设置心跳包请求后超时无反馈情况下的处理机制，默认为关闭连接,在此处设置为输出日志提醒
            heartBeat.setRequestTimeoutHandler(KeepAliveRequestTimeoutHandler.LOG);
            /** 是否回发 */
            heartBeat.setForwardEvent(false);
            /** 发送频率 */
            heartBeat.setRequestInterval(HEARTBEATRATE);
            /** 设置心跳包请求后 等待反馈超时时间。 超过该时间后则调用KeepAliveRequestTimeoutHandler.CLOSE */
            heartBeat.setRequestTimeout(IDELTIMEOUT);
            //connector.getSessionConfig().setKeepAlive(true);
            connector.getFilterChain().addLast("heartbeat", heartBeat);

            //connector.getFilterChain().addLast("reconnect", new KeepAliveRequestTimeoutHandlerImpl()); //心跳超时后的处理,这里先默认处理

        }
        try {
            if (connectFuture == null) {
                connector.setHandler(clientHandlerMsg);
                setupConnectFuture();
            }
            Log.d(TAG, "开始于服务器建立连接...");
            connectFuture.awaitUninterruptibly(); //等待连接创建完成
            session = connectFuture.getSession(); //获得seccion
            //连接成功
            if (session != null && session.isConnected()) {
                Log.d(TAG, "连接成功");
//                if (null == loginConfig) {
//                    return;
//                }
//                writeData(mEditDataModel.sendData("002"));//首次连接(自己的id)
//                writeData(mEditDataModel.sendData(loginConfig.senderLogin("开始")));
                writeData(mEditDataModel.sendData(RoleUtils.sendUserId));
            } else {

            }

        } catch (Exception e) {
            Log.d(TAG, "客户端链接异常(在连接时直接抛出了异常):");
            Log.e(TAG, e.toString());
        }
        if (session != null && session.isConnected()) {
            session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
            System.out.println("客户端断开");
        }
    }

    public void writeData(Object order) {
        if (session != null && session.isConnected()) {
            session.write(order);
            Log.d(TAG, "数据上报成功:");
            return;
        }
    }

    /**
     * 关闭Mina长连接 **
     */
    public void minaClose() {
        if (session != null) {
            session.close(false);
            session = null;
        }
        if (connectFuture != null && connectFuture.isConnected()) {
            connectFuture.cancel();
            connectFuture = null;
        }
        if (connector != null && !connector.isDisposed()) {
            //清空里面注册的所以过滤器
            connector.getFilterChain().clear();
            connector.dispose();
            connector = null;
        }
        Log.i(TAG, "MINA长连接已关闭...");
        Log.i(TAG, "MINA长连接已关闭...");

    }

    /**
     * 接口
     */

    public interface LoginConfig {
//        Object[] serverAddressConfig();

        Object senderLogin(String msg);
    }

    private LoginConfig loginConfig;

    public void setLoginConfig(LoginConfig loginConfig) {
        this.loginConfig = loginConfig;
    }

    public interface IsSuccess {//是否发送成功

        void successStatus(Object message);
    }

    private IsSuccess status;

    public void setDataStatus(IsSuccess status) {
        this.status = status;
    }

    private ReceivedMsg msg;

    public interface ReceivedMsg {//接受对方消息

        void receivedMsg(MsgCodeModel message);
    }

    public void setMsg(ReceivedMsg msg) {
        this.msg = msg;
    }

}
