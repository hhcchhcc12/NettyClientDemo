package xiejie.com.myapplication.netty;


import android.content.Context;
import android.util.Log;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import xiejie.com.myapplication.domain.TCPData;


/**
 * Created by zhaiydong on 2017/4/19.
 */

public class NettyClientBootstrap {

    public static final String TCP_URL = "10.10.13.118";
    public static final int TCP_PORT = 9999;

//    private int port;
//    private String host;
    public SocketChannel socketChannel;
    private NioEventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;
    private NettyClientHandler nettyClientHandler;
//    private Context context;
    private long lastTime = 0;
    private boolean isSendHeart = false;
    private boolean isConnect = false;
    private static final String TAG = "NettyClientBootstrap";
//    private String[] actions;
    private TCPData heart;
    //设置心跳时间 30s
    public static final int MIN_CLICK_DELAY_TIME = 1000 * 30;
    public boolean isRepeate = false;

    private static NettyClientBootstrap nettyClient;

    public static NettyClientBootstrap getInstance(){
        if(nettyClient == null){
            nettyClient = new NettyClientBootstrap();
        }
        return nettyClient;
    }

    public NettyClientBootstrap() {
        //this.actions = actions;
        //this.context = context;
        //初始化连接
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(1024, 1024 * 32, 1024 * 64));
        bootstrap.group(eventLoopGroup);
        bootstrap.remoteAddress(TCP_URL, TCP_PORT);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                //超时处理
                p.addLast(new IdleStateHandler(0, 0, 5));
                p.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, -4, 0));
                //p.addLast("handler",new ClientHandler());
                //编码，解码，回调，如果没有用带编解码删除new Encode(), new Decode()
                //socketChannel.pipeline().addLast(new Encode(), new Decode(), new ClientHandler());
            }
        });
    }

    /**
     * send tcp msg
     *
     * @param msg
     */
    public void startNetty(final TCPData msg) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (isConnect) {
                    socketChannel.writeAndFlush("android test");
                }
            }
        }.start();

    }

    public void sendData() throws Exception {
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 10000; i++) {
            if (socketChannel != null && socketChannel.isActive()) {
                String content = "client msg " + i;
                ByteBuf buf = socketChannel.alloc().buffer(5 + content.getBytes().length);
                buf.writeInt(5 + content.getBytes().length);
                buf.writeByte(CustomHeartbeatHandler.CUSTOM_MSG);
                buf.writeBytes(content.getBytes());
                socketChannel.writeAndFlush(buf);
            }

            Thread.sleep(random.nextInt(20000));
        }
    }

    /**
     * first init tcp connect
     */
    private ChannelFuture future = null;

    public void start() {

        try {
            future = bootstrap.connect(new InetSocketAddress(TCP_URL, TCP_PORT)).sync();
            if (future.isSuccess()) {
                socketChannel = (SocketChannel) future.channel();
                Log.e("tcp", "connect server  Success---------" + TCP_URL + TCP_PORT);
                isConnect = true;
               //成功状态监听在此处（包括重连成功状态）

                return;
            } else {
                Log.e("tcp", "TcpUnConnect------" + "future is unConnect");
            }
        } catch (Exception e) {
            Log.e("tcp", "TcpUnConnect------" + e.toString());
        }
        //连接状态在此处处理



        repeateTcp();
    }


    public void repeateTcp() {
        if (!isRepeate) {
            if (isConnect) {
                isConnect = false;
                //断线监听在此处处理



            }
            isRepeate = true;
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isRepeate = false;
            start();
        }
    }

    public void close() {
        if (future != null && future.channel() != null) {
            if (future.channel().isOpen())
                future.channel().close();
        }
    }


    public class NettyClientHandler extends SimpleChannelInboundHandler<Object> {
        //利用写空闲发送心跳检测消息
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                switch (e.state()) {
                    case WRITER_IDLE:
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastTime > MIN_CLICK_DELAY_TIME) {
                            if (!isSendHeart) {
                                isSendHeart = true;
                                lastTime = currentTime;
                                if (heart == null) {
                                    //心跳数据在出处编写
                                    heart = new TCPData();
                                }
                                startNetty(heart);
                                isSendHeart = false;
                            }
                        }
                        break;
                }
            }
        }


        //这里是接受服务端发送过来的消息
        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object baseMsg) throws Exception {
            String msgObj = (String) baseMsg;

        }


        //这里是断线要进行的操作
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            Log.e("tcp", "TcpHandler--RepeatConnect");
            //断线监听在repeateTcp方法内
            repeateTcp();
        }

        //这里是出现异常的话要进行的操作
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            Log.e("tcp", "TcpHandler--ErrorConnect--" + cause.toString());
            //断线监听在repeateTcp方法内
            repeateTcp();
        }

    }


}


