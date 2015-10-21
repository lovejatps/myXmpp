package com.myXmpp.service;

import com.myXmpp.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		//重启service
		if (action.equals(Intent.ACTION_TIME_TICK)) {
			if (!XmppService.checkServiceStatus(context) && !MainActivity.isAct) {
				Intent service = new Intent(context, XmppService.class);
				context.startService(service);
			}
		}
	}
}
