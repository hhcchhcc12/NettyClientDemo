package xiejie.com.myapplication.netty;

import android.util.Log;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Created by zhaiydong on 2017/5/26.
 */

public class Decode extends ByteToMessageDecoder {
    private final int MSG_HEAD_LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        //解码规则readableBytes为数据长度context为数据内容，需要与服务器约定
        //大于基本长度
        if (in.readableBytes() < MSG_HEAD_LENGTH) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] head = new byte[MSG_HEAD_LENGTH];
        in.readBytes(head);
        String headStr = new String(head);
        Log.e("client_decode","headStr : " + headStr);

        byte[] data = new byte[in.readableBytes() - 5];
        in.skipBytes(5);
        in.readBytes(data);
        String content = new String(data);
        Log.e("client_decode","content : " + content);

//        byte[] decoded = new byte[dataLength];
//        in.readBytes(decoded);
//        String content = new String(decoded, "UTF-8");
        list.add(content);
    }
}
