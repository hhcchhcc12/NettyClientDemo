package xiejie.com.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import xiejie.com.myapplication.domain.TCPData;
import xiejie.com.myapplication.netty.Client;
import xiejie.com.myapplication.netty.NettyClientBootstrap;
import xiejie.com.myapplication.util.NettyApplaction;
import xiejie.com.myapplication.util.ToastEvent;


public class MainActivity extends Activity {

    Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        final NettyClientBootstrap nettyClientBootstrap =
                new NettyClientBootstrap();

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //初始化
                //nettyClientBootstrap.start();
                client = new Client();
                client.start();
            }
        });

        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发送消息
                //nettyClientBootstrap.startNetty(new TCPData());
                if(client != null){
                    try {
                        client.sendData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Subscribe
    public void showToast(final ToastEvent event){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),event.msg,event.time).show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(client != null){
            client.closeConnect();
        }
    }
}
