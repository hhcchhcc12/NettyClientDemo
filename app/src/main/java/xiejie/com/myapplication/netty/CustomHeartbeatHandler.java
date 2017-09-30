package xiejie.com.myapplication.netty;

/**
 * Created by huchao on 2017/9/26.
 * Description :
 */

import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import xiejie.com.myapplication.domain.TCPData;
import xiejie.com.myapplication.util.NettyApplaction;
import xiejie.com.myapplication.util.ToastEvent;

/**
 * @author xiongyongshun
 * @version 1.0
 * @email yongshun1228@gmail.com
 * @created 16/9/18 13:02
 */
public abstract class CustomHeartbeatHandler extends SimpleChannelInboundHandler<TCPData> {
    public static final byte PING_MSG = 1;
    public static final byte PONG_MSG = 2;
    public static final byte CUSTOM_MSG = 3;
    protected String name;
    private int heartbeatCount = 0;

    public CustomHeartbeatHandler(String name) {
        this.name = name;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, TCPData byteBuf) throws Exception {
//        if (byteBuf.getByte(4) == PING_MSG) {
//            sendPongMsg(context);
//        } else if (byteBuf.getByte(4) == PONG_MSG){
//            ToastEvent toastEvent = new ToastEvent(" get pong msg from " + context.channel().remoteAddress(),Toast.LENGTH_SHORT);
//            EventBus.getDefault().post(toastEvent);
//        } else {
//            handleData(context, byteBuf);
//        }

        if(byteBuf != null){
            Log.e("client","get msg from " + context.channel().remoteAddress() + " , contnent : "+ byteBuf.getContent());
        }
    }

    protected void sendPingMsg(ChannelHandlerContext context) {
        Log.e("client",name + " sent ping msg to " + context.channel().remoteAddress() + ", count: " + heartbeatCount);
//        ByteBuf buf = context.alloc().buffer(5);
//        buf.writeInt(5);
//        buf.writeByte(PING_MSG);
//        context.writeAndFlush(buf);


        TCPData data = new TCPData();
        data.setMsgType(Encode.PING_MSG);
        data.setContent("this is a ping msg");
        context.writeAndFlush(data);
        heartbeatCount++;
        //System.err.println(name + " sent ping msg to " + context.channel().remoteAddress() + ", count: " + heartbeatCount);
    }

//    private void sendPongMsg(ChannelHandlerContext context) {
//        ByteBuf buf = context.alloc().buffer(5);
//        buf.writeInt(5);
//        buf.writeByte(PONG_MSG);
//        context.channel().writeAndFlush(buf);
//        heartbeatCount++;
//        System.out.println(name + " sent pong msg to " + context.channel().remoteAddress() + ", count: " + heartbeatCount);
//    }

    protected abstract void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //System.err.println("---" + ctx.channel().remoteAddress() + " is active---");
        Log.e("client","---" + ctx.channel().remoteAddress() + " is active---");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //System.err.println("---" + ctx.channel().remoteAddress() + " is inactive---");
        Log.e("client","---" + ctx.channel().remoteAddress() + " is inactive---");
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        Log.e("client","---READER_IDLE---");
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        Log.e("client","---WRITER_IDLE---");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        Log.e("client","---ALL_IDLE---");
    }
}
