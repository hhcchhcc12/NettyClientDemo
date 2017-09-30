package xiejie.com.myapplication.netty;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by huchao on 2017/9/26.
 * Description :
 */

public class ClientHandler extends CustomHeartbeatHandler {
    private Client client;
    private ChannelInactiveCallBack callBack;
    public ClientHandler(Client client,ChannelInactiveCallBack callBack) {
        super("client");
        this.client = client;
        this.callBack = callBack;
    }

    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        byte[] data = new byte[byteBuf.readableBytes() - 5];
        byteBuf.skipBytes(5);
        byteBuf.readBytes(data);
        String content = new String(data);
        Log.e("client","content : " + content);
    }

//    @Override
//    protected void handleAllIdle(ChannelHandlerContext ctx) {
//        super.handleAllIdle(ctx);
//        sendPingMsg(ctx);
//    }

    @Override
    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        super.handleWriterIdle(ctx);
        sendPingMsg(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if(callBack != null){
            callBack.callBack(ctx);
        }
    }

    public interface ChannelInactiveCallBack{
        void callBack(ChannelHandlerContext ctx);
    }

}