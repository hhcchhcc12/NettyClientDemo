package xiejie.com.myapplication.netty;

import android.util.Log;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import xiejie.com.myapplication.domain.TCPData;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author xiongyongshun
 * @version 1.0
 * @email yongshun1228@gmail.com
 * @created 16/9/18 12:59
 */
public class Client {

    public static final String TCP_URL = "10.10.13.118";
    public static final int TCP_PORT = 9999;

    private NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
    private Channel channel;
    private Bootstrap bootstrap;

    private static boolean isLogOut;

    private static final int MAX_FRAME_LENGTH = 1024 * 1024;
    private static final int LENGTH_FIELD_LENGTH = 4;
    private static final int LENGTH_FIELD_OFFSET = 2;
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 0;

    public static boolean isLogOut(){
        return isLogOut;
    }

    public void sendData() throws Exception {
        if (channel != null && channel.isActive()) {
            String content = "  {\n" +
                    "    \"code\": 10000,\n" +
                    "    \"message\": \"用户名或密码错误\",\n" +
                    "    \"data\": \"123456\"\n" +
                    "  }";
//            ByteBuf buf = channel.alloc().buffer(5 + content.getBytes().length);
//            buf.writeInt(5 + content.getBytes().length);
//            buf.writeByte(CustomHeartbeatHandler.CUSTOM_MSG);
//            buf.writeBytes(content.getBytes());
//            channel.writeAndFlush(buf);

            TCPData data = new TCPData();
            data.setMsgType(Encode.CONTENT_MSG);
            data.setContent(content);
            channel.writeAndFlush(data);
        }
    }

    public void start() {
        try {
            bootstrap = new Bootstrap();
            bootstrap
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new IdleStateHandler(0, 5, 0));
                            //p.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, -4, 0));
                            p.addLast(new CustomEncoder());
                            p.addLast(new CustomDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_LENGTH,
                                    LENGTH_FIELD_OFFSET,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP,false));
                            p.addLast(new ClientHandler(Client.this,callBack));
                        }
                    });
            doConnect();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ClientHandler.ChannelInactiveCallBack callBack = new ClientHandler.ChannelInactiveCallBack() {
        @Override
        public void callBack(ChannelHandlerContext ctx) {
            Log.e("client","isLogOut : " + isLogOut);

            if(isLogOut){
                ctx.close();
            }else {
                doConnect();
            }
        }
    };

    protected void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }

        ChannelFuture future = bootstrap.connect(TCP_URL, TCP_PORT);
        isLogOut = false;

        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    Log.e("client","Connect to server successfully!");
                } else {
                    Log.e("client","Failed to connect to server, try connect after 10s");

                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });
    }

    public void closeConnect(){
        isLogOut = true;
        channel.close();
        channel.closeFuture();
        channel = null;
        if(channel != null && channel.isActive()){
            Log.e("client","channel is still active!!");
        }
    }

}
