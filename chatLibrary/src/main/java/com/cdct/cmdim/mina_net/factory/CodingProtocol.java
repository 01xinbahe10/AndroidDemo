package com.cdct.cmdim.mina_net.factory;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.cdct.cmdim.mina_net.decoding.Decodex;
import com.cdct.cmdim.mina_net.decoding.Encodex;


public class CodingProtocol implements ProtocolCodecFactory {
    CharsetDecoder decoder = Charset.forName("utf-8").newDecoder();

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return new Encodex();
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return new Decodex();
    }
}
