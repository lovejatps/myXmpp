package com.myXmpp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//系统重启或�?�?��启动程序
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//			Intent startIntent = new Intent(context, MainActivity.class);
//			startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(startIntent);
		    System.out.println("BootCompletedBroadcastReceiver?????");
		    if (!XmppService.checkServiceStatus(context)) {
                Intent service = new Intent(context, XmppService.class);
                context.startService(service);
            }
		}
			
	}
}
