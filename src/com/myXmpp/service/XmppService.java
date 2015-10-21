package com.myXmpp.service;

import tigase.jaxmpp.core.client.Connector;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.myXmpp.Utils.Util;
import com.myXmpp.jaxmpp.JaxmppConnectManager;
import com.myXmpp.struct.User;

public class XmppService extends Service {

//    XmppConnectManager connect;
    String userName = null;
    String password = null;
    public final static String XMPP_MESSAGE = "xmpp_message";
    public final static int LOGIN_SUCCESS = 1;
    public final static int LOGIN_FAIL = 2;
    public final static int SERVER_MESSAGE = 3;
    public final static int RELOGIN_SUCCESS = 4;
    public final static int RELOGIN_FAIL = 5;
    public final static int RECONNECT= 6;
    public final static String XMPP_MESSAGE_LOGIN_SUCCESS = "login_success";
    public final static String XMPP_MESSAGE_LOGIN_FAIL = "login_fail";
    public final static String XMPP_MESSAGE_SERVER_MESSAGE = "server_msg";
    public final static String XMPP_MESSAGE_RELOGIN_FAIL = "relogin_fail";
    SharedPreferences spf = null;
    JaxmppConnectManager connect;
    private static final String TAG = "NetworkReceiver";
    public static final String netACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    int MAX_RECONNECT_TIME = 5;
    int reconnectTime = 0;
    boolean isLogin = false;

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case LOGIN_SUCCESS :
                    sendMessageToActivity(XMPP_MESSAGE_LOGIN_SUCCESS);
                    spf.edit().putBoolean(JaxmppConnectManager.USER_IS_LOGIN, true).commit();
                    regNetworkBroadcast();
                    break;
                case LOGIN_FAIL :
                    sendMessageToActivity(XMPP_MESSAGE_LOGIN_FAIL);
                    break;
                case SERVER_MESSAGE :
                    sendMessageToActivity(XMPP_MESSAGE_SERVER_MESSAGE, (String) msg.obj);
                    break;
                case RELOGIN_FAIL:
                	if(reconnectTime < MAX_RECONNECT_TIME){
                		handler.sendEmptyMessage(RECONNECT);
                	}else{
                		reconnectTime = 0;
                		System.out.println("重连失败！！！");
                		sendMessageToActivity(XMPP_MESSAGE_RELOGIN_FAIL);
                	}
                	break;
                case RELOGIN_SUCCESS:
                	reconnectTime = 0;
                	break;
                case RECONNECT:
                	reconnect();
                	break;
                default :
                    break;
            }
        };
    };

    @Override
    public IBinder onBind(Intent i) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        spf = getSharedPreferences(JaxmppConnectManager.USER_SPF, 0);
        if (intent.hasExtra("userName")) {
            userName = intent.getStringExtra("userName");
            System.out.println(userName);
            password = intent.getStringExtra("password");
            isLogin = true;
        }else if (spf != null) {
            if (Util.LOGIN_USER == null) {
                Util.LOGIN_USER = new User();
            }
            Util.LOGIN_USER.setUserName(spf.getString(JaxmppConnectManager.USER_NAME, null));
            Util.LOGIN_USER.setPwd(spf.getString(JaxmppConnectManager.PWD, null));
            userName = spf.getString(JaxmppConnectManager.USER_NAME, null);
            password = spf.getString(JaxmppConnectManager.PWD, null);
            isLogin = spf.getBoolean(JaxmppConnectManager.USER_IS_LOGIN, false);
        }
        //        final XmppConnectionManager client = XmppConnectionManager.getInstance();
        if (userName != null && password != null && isLogin) {
//            connect = XmppConnectManager.getInstance();
//            if (connect.getState() == ConnectState.Default || connect.getState() == ConnectState.Stopped) {
//                connect.Login(getApplicationContext(), handler, userName, password);
//            }
//            else if (connect.getState() == ConnectState.Connected) {
//                // ���¼�����
//                //            XmppConnectionManager.getInstance().start();
//                connect.loginSuccess();
//            }
        	connect = JaxmppConnectManager.getInstance();
        	connect.initialize(getApplicationContext(), handler);
            if(connect.getState() == Connector.State.disconnected){
            	connect.login(userName, password);
            }
        }
        return START_REDELIVER_INTENT;
    }


    @Override
    public final void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkBroadcast);
        stopForeground(true);
    }

    public static boolean checkServiceStatus(Context context) {
        boolean isServiceRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE)) {
            if (XmppService.class.getName().equals(service.service.getClassName())) {
                isServiceRunning = true;
                break;
            }
        }
        return isServiceRunning;
    }

    private void sendMessageToActivity(String message) {
        Intent mIntent = new Intent(XMPP_MESSAGE);
        mIntent.putExtra(message, message);
        //发送广播
        sendBroadcast(mIntent);
    }

    private void sendMessageToActivity(String message, String msg) {
        Intent mIntent = new Intent(XMPP_MESSAGE);
        mIntent.putExtra(message, msg);
        //发送广播
        sendBroadcast(mIntent);
    }

    private void regNetworkBroadcast(){
    	 IntentFilter myIntentFilter = new IntentFilter();
         myIntentFilter.addAction(netACTION);
         //注册广播
         registerReceiver(networkBroadcast, myIntentFilter);
    }

    BroadcastReceiver networkBroadcast = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(netACTION)){
//	             Log.v(TAG, "Receiver come");
	             //启动服务 (不管我在这里判断是GPRS还是WIFI在启动服务，这个Receiver好像都会执行多次，所有我把多余的判断代码去掉了)
	        	if(intent.getAction().equals(netACTION)){
	                //Intent中ConnectivityManager.EXTRA_NO_CONNECTIVITY这个关键字表示着当前是否连接上了网络
	                //true 代表网络断开   false 代表网络没有断开
	                boolean isBreak = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
	                JaxmppConnectManager.isNetWorkBreak = isBreak;
	                System.out.println("isBreak  :  "  +  isBreak);
	                handler.sendEmptyMessage(RECONNECT);
	        }
		}
		}

    };
    
    void reconnect(){
    	if(!JaxmppConnectManager.isNetWorkBreak && connect != null && connect.getState() == Connector.State.disconnected){
    		reconnectTime++;
        	connect.reconnect();
        }
    }
}
