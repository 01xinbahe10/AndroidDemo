package com.cdct.cmdim.presenter;


import com.cdct.cmdim.mina_net.ClientMina;

/**
 * Created by hxb on 2017/11/17.
 *
 */

public class SendDataPresenter {

    private ClientMina mClientMina;
    public static SendDataPresenter init(){
        SendDataPresenter presenter = new SendDataPresenter();
        presenter.connectionService();
        return presenter;
    }

    public ClientMina getmClientMina() {
        return mClientMina;
    }

    private void connectionService() {
        closeMinaThread();
        if (mClientMina == null) {
//            mClientMina = ClientMina.init();
            mClientMina = ClientMina.getIntrans();
            mClientMina.initialize();
            mClientMina.start();
        }
    }

    public void closeMinaThread() {
//        if (mClientMina != null) {
//            mClientMina.interrupt();
//            mClientMina = null;
//        }
        if (mClientMina!=null){
            mClientMina.minaClose();
        }
    }


    public void sendOBDData(Object order) {
        if (mClientMina != null)
            mClientMina.writeData(order);
    }
}
