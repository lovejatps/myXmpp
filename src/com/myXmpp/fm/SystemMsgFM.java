package com.myXmpp.fm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.myXmpp.R;
import com.myXmpp.asyctask.DataBaseAsyncTask;
import com.myXmpp.asyctask.DataBaseAsyncTask.ActionType;
import com.myXmpp.asyctask.DataBaseAsyncTask.OnActionCallback;
import com.myXmpp.asyctask.DataBaseAsyncTask.ReturnMode;
import com.myXmpp.asyctask.PresenceAsyclTask;
import com.myXmpp.struct.User;
import com.myXmpp.struct.XmppMessage;

public class SystemMsgFM extends BaseFragment implements android.view.View.OnClickListener {

    public final static String REFLUSH_XMPP_MESSAGE_FROM_SYSTEM = "reflush_xmpp_message_from_system";

    private View systemView;

    private ListView listView;

    private Button back;

    User friend = null;

    Intent i;

    private SysMsgAdapter listViewAdapeter;

    private List<XmppMessage> msgList = new ArrayList<XmppMessage>();

    public SystemMsgFM(Intent i) {
        this.i = i;
        this.friend = (User) i.getExtras().getSerializable("friend");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSystemMsg();
    }

    public void refreshSystemMsg() {
        new DataBaseAsyncTask(ActionType.getMsgHis, getActivity(), friend, new OnActionCallback() {
            @Override
            public void onActionCallback(boolean result, Object object) {
                // TODO Auto-generated method stub
                if (result) {
                    msgList = (List<XmppMessage>) object;
                    RefreshAdapter();
                }
            }
        }, ReturnMode.listMode).execute("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        systemView = inflater.inflate(R.layout.systemmsg_main, null);
        listView = (ListView) systemView.findViewById(R.id.systemMsg);
        back = (Button) systemView.findViewById(R.id.back);
        initListener();
        init();
        return systemView;
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    private void init() {
        listViewAdapeter = new SysMsgAdapter(getActivity(), msgList);
        listView.setAdapter(listViewAdapeter);
    }

    void initListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                final XmppMessage item = (XmppMessage) listView.getItemAtPosition(arg2);
                if (item.getMessageStatus() == 1) {
                    String msgBody = item.getMessageBody();
                    final String from = msgBody.split("_")[1];
                    final String to = item.getMessageTo();
                    AlertDialog.Builder builder = new Builder(getActivity());
                    builder.setMessage("是否同意" + from.substring(0, from.indexOf("@")) + " 的添加好友請求");
                    builder.setTitle("提示");
                    builder.setPositiveButton("同意", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Presence p = Presence.create();
                                p.setType(StanzaType.subscribed);
                                p.setTo(JID.jidInstance(from));
                                new PresenceAsyclTask(p, PresenceAsyclTask.ActionType.sendPresence).execute("");
                                Presence p2 = Presence.create();
                                p2.setType(StanzaType.subscribe);
                                p2.setTo(JID.jidInstance(from));
                                new PresenceAsyclTask(p2, PresenceAsyclTask.ActionType.sendPresence).execute("");
                                item.setMessageStatus(2);
                                new DataBaseAsyncTask(
                                    ActionType.updateMsgStatus,
                                    getActivity(),
                                    item,
                                    new OnActionCallback() {
                                        @Override
                                        public void onActionCallback(boolean result, Object object) {
                                            listViewAdapeter.notifyDataSetChanged();
                                        }
                                    },
                                    ReturnMode.objectMode).execute("");
                                dialog.dismiss();
                            }
                            catch (XMLException e) {
                                e.printStackTrace();
                            }
                            catch (JaxmppException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.setNegativeButton("拒絕", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Presence p = Presence.create();
                                p.setType(StanzaType.unsubscribed);
                                p.setTo(JID.jidInstance(from));
                                new PresenceAsyclTask(p, PresenceAsyclTask.ActionType.sendPresence).execute("");
                                item.setMessageStatus(3);
                                new DataBaseAsyncTask(
                                    ActionType.updateMsgStatus,
                                    getActivity(),
                                    item,
                                    new OnActionCallback() {
                                        @Override
                                        public void onActionCallback(boolean result, Object object) {
                                            listViewAdapeter.notifyDataSetChanged();
                                        }
                                    },
                                    ReturnMode.objectMode).execute("");
                                dialog.dismiss();

                            }
                            catch (XMLException e) {
                                e.printStackTrace();
                            }
                            catch (JaxmppException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.create().show();
                }
            }
        });

        back.setOnClickListener(this);
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(REFLUSH_XMPP_MESSAGE_FROM_SYSTEM);
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    void RefreshAdapter() {
        if (listViewAdapeter == null) {
            listViewAdapeter = new SysMsgAdapter(getActivity(), msgList);
            listView.setAdapter(listViewAdapeter);
        }
        else {
            listViewAdapeter.setMsgList(msgList);
            listViewAdapeter.notifyDataSetChanged();
        }
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(REFLUSH_XMPP_MESSAGE_FROM_SYSTEM)) {
                RefreshAdapter();
            }
        }
    };

    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    };

    @Override
    public void onUpdate() {
        // TODO Auto-generated method stub
        super.onUpdate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back :
                getActivity().finish();
                break;
            default :
                break;
        }
    }

}
