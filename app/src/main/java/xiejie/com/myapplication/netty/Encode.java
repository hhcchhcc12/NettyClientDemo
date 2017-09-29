package xiejie.com.myapplication.netty;

import android.util.Log;

import com.google.gson.Gson;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import xiejie.com.myapplication.domain.TCPData;
import xiejie.com.myapplication.util.TextUtil;

/**
 * Created by zhaiydong on 2017/5/31.
 */

public class Encode extends MessageToByteEncoder<TCPData> {

    public static final byte PING_MSG = 1;
    public static final byte PONG_MSG = 2;
    public static final byte CONTENT_MSG = 3;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, TCPData tcpData, ByteBuf byteBuf) throws Exception {
        //编码规则writeInt为数据长度，writeBytes为数据内容，需要与服务器约定
//        String authString =  TextUtil.createAuthKey();
//        byte[] auth = authString.getBytes();
//        byteBuf.writeInt(auth.length);
//        byteBuf.writeBytes(auth);

//        byteBuf.writeInt(5);
//        byteBuf.writeByte(tcpData.getMsgType());

        String dataGson = new Gson().toJson(tcpData);
        Log.e("client_encode","dataGson : " + dataGson);

        //String contentString= tcpData.getContent();
        byte[] content = dataGson.getBytes();
        byteBuf.writeInt(content.length);
        byteBuf.writeBytes(content);
    }
}
