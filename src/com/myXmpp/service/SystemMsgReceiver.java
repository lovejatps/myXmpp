package com.myXmpp.service;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.myXmpp.asyctask.DataBaseAsyncTask;
import com.myXmpp.asyctask.DataBaseAsyncTask.ActionType;
import com.myXmpp.asyctask.DataBaseAsyncTask.OnActionCallback;
import com.myXmpp.asyctask.DataBaseAsyncTask.ReturnMode;
import com.myXmpp.fm.SystemMsgFM;
import com.myXmpp.jaxmpp.JaxmppConnectManager;
import com.myXmpp.struct.PresenceXmppMessage;
import com.myXmpp.struct.XmppMessage;

public class SystemMsgReceiver extends BroadcastReceiver {

    //Presence
    public final static String XMPP_MESSAGE_FROM_PRESENCE = "xmpp_message_from_Presence";

    public static List<PresenceXmppMessage> msgList = new ArrayList<PresenceXmppMessage>();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(JaxmppConnectManager.XMPP_MESSAGE_FROM_SYSTEM)) {
            Bundle bundle = intent.getExtras();
            PresenceXmppMessage rpresence = (PresenceXmppMessage) bundle.getSerializable(XMPP_MESSAGE_FROM_PRESENCE);

            msgList.add(rpresence);

            Intent mIntent = new Intent();
            mIntent.setAction(SystemMsgFM.REFLUSH_XMPP_MESSAGE_FROM_SYSTEM);
            context.sendBroadcast(mIntent);
        }
    }

}
