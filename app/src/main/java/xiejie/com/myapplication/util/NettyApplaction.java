package xiejie.com.myapplication.util;

import android.app.Application;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by huchao on 2017/9/29.
 * Description :
 */

public class NettyApplaction extends Application {

    static NettyApplaction applaction;

    public static NettyApplaction getInstance(){
        return applaction;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        applaction = this;

    }

}
