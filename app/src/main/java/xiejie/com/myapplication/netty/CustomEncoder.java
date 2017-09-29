package xiejie.com.myapplication.netty;

/**
 * Created by huchao on 2017/9/29.
 * Description :
 */

import android.util.Log;

import com.google.gson.Gson;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import xiejie.com.myapplication.domain.TCPData;

public class CustomEncoder extends MessageToByteEncoder<TCPData> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TCPData msg, ByteBuf out) throws Exception {
        if(null == msg){
            Log.e("client","msg is null");
            return;

        }

        //String body = new Gson().toJson(msg);
        //Log.e("client_encode","dataGson : " + body);
        String body = msg.getContent();
        byte[] bodyBytes = body.getBytes(Charset.forName("utf-8"));
        out.writeByte(msg.getMsgType());
        //out.writeByte(msg.getFlag());
        out.writeInt(bodyBytes.length);
        out.writeBytes(bodyBytes);

    }

}
