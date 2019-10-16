package com.cdct.cmdim.mina_net.model;


/**
 * 消息协议
 * @author hsq
 *
 */
public class MsgCodeModel {
	
    private int headLength;// 参数头长度
    private String header;// 协议头
    private byte[] body;// 协议消息体

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getHeadLength() {
        return headLength;
    }

    public void setHeadLength(int headLength) {
        this.headLength = headLength;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
