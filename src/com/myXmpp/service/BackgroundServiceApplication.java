package com.myXmpp.service;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

public class BackgroundServiceApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //该广播注册后可每分钟发�?�?��该广播，用来判断service的状态，是否进行重新启动�?
        //此action只能通过动�?注册
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        ServiceBroadcastReceiver receiver = new ServiceBroadcastReceiver();
        registerReceiver(receiver, intentFilter);
    }
}
