package com.myXmpp;

import tigase.jaxmpp.core.client.Connector;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.widget.Toast;

import com.myXmpp.Utils.Util;
import com.myXmpp.fm.ChatMainFm;
import com.myXmpp.fm.LoginFM;
import com.myXmpp.jaxmpp.JaxmppConnectManager;
import com.myXmpp.service.XmppService;

public class MainActivity extends FragmentActivity {

    LoginFM loginFM = null;
    ProgressDialog p = null;
    public static boolean isAct = false;
     //    FriendListFM friendListFM = null;
    ChatMainFm chatMainFm = null;

    BroadcastReceiver broadcastReceiver2=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAct = true;
        setContentView(R.layout.activity_main);
        registerBoradcastReceiver();
        if (JaxmppConnectManager.getInstance().getState() == Connector.State.connected) {
            gotoFriendFM();
        }
        else {
            if (loginFM == null) {
                loginFM = new LoginFM();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.content, loginFM).commit();
        }
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        Util.screenWith = metric.widthPixels;  // 屏幕宽度（像素）
        Util.screenheigh = metric.heightPixels;  // 屏幕高度（像素）
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(XmppService.XMPP_MESSAGE)) {
                //                Toast.makeText(Test.this, "处理action名字相对应的广播", 200);
                if (intent.hasExtra(XmppService.XMPP_MESSAGE_LOGIN_SUCCESS)) {
                    dissmissProgressDialog();
                    Toast.makeText(MainActivity.this, "登陆成功", 200).show();
                    gotoFriendFM();
                }
                else if (intent.hasExtra(XmppService.XMPP_MESSAGE_LOGIN_FAIL)) {
                    dissmissProgressDialog();
                    Toast.makeText(MainActivity.this, "登陆失败", 200).show();
                }
                else if (intent.hasExtra(XmppService.XMPP_MESSAGE_SERVER_MESSAGE)) {
                    System.out.println("msg    " + intent.getStringExtra(XmppService.XMPP_MESSAGE_SERVER_MESSAGE));
                    new AlertDialog.Builder(MainActivity.this).setTitle("推送消息").setMessage(
                        intent.getStringExtra(XmppService.XMPP_MESSAGE_SERVER_MESSAGE)).setPositiveButton("确定", null).show();
                }else if(intent.hasExtra(XmppService.XMPP_MESSAGE_RELOGIN_FAIL)){
                	Toast.makeText(MainActivity.this, "网络连接异常", 200).show();
                	gotoLogin();
                }
            }
        }

    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(XmppService.XMPP_MESSAGE);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    public void removeBoradcastReceiver() {
        unregisterReceiver(mBroadcastReceiver);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        removeBoradcastReceiver();
        isAct = false;
    }

    public void login(String userName, String pwd) {
        Intent server = new Intent(MainActivity.this, XmppService.class);
        server.putExtra("userName", userName);
        server.putExtra("password", pwd);
        MainActivity.this.startService(server);
        showProgressDialog("登录中...");
    }

    void showProgressDialog(String message) {
        if (p == null) {
            p = new ProgressDialog(this);
        }
        p.setCancelable(false);
        p.setMessage(message);
        if (!p.isShowing()) {
            p.show();
        }
    }

    void dissmissProgressDialog() {
        if (p != null && p.isShowing()) {
            p.dismiss();
        }
    }

    void gotoFriendFM() {

        if (chatMainFm == null) {
            chatMainFm = new ChatMainFm();
        }
        if (loginFM != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, chatMainFm).remove(loginFM).detach(
                loginFM).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, chatMainFm).commit();
        }
    }
    
    void gotoLogin(){
    	 if (chatMainFm != null) {
    		
             getSupportFragmentManager().beginTransaction().remove(chatMainFm).detach(
            		 chatMainFm).commit();
         }
    	 if (loginFM == null) {
             loginFM = new LoginFM();
         }
         getSupportFragmentManager().beginTransaction().replace(R.id.content, loginFM).commit();
    }

}
