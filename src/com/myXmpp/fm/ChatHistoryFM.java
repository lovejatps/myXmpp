package com.myXmpp.fm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myXmpp.R;
import com.myXmpp.Utils.Util;
import com.myXmpp.asyctask.DataBaseAsyncTask;
import com.myXmpp.asyctask.DataBaseAsyncTask.ActionType;
import com.myXmpp.asyctask.DataBaseAsyncTask.OnActionCallback;
import com.myXmpp.asyctask.DataBaseAsyncTask.ReturnMode;
import com.myXmpp.jaxmpp.JaxmppConnectManager;
import com.myXmpp.struct.ChatHistoryInfo;
import com.myXmpp.struct.User;
import com.myXmpp.struct.XmppMessage;

public class ChatHistoryFM extends BaseFragment {

    View chatHistoryView;

    private ListView chatHistoryListView;

    private ChatHistoryAdapter chatHistoryListViewAdapeter;

    private List<ChatHistoryInfo> chatHistoryList = new ArrayList<ChatHistoryInfo>();
    TextView no_recore_text;
    ProgressBar loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        chatHistoryView = inflater.inflate(R.layout.chat_history, null);

        chatHistoryListView = (ListView) chatHistoryView.findViewById(R.id.chat_histoty_list);
        no_recore_text = (TextView) chatHistoryView.findViewById(R.id.no_recore_text);
        loading = (ProgressBar) chatHistoryView.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        no_recore_text.setVisibility(View.GONE);
        init();
        return chatHistoryView;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        updateChatInfo();
        super.onResume();
    }

    void updateChatInfo() {
        DataBaseAsyncTask dbTask =
            new DataBaseAsyncTask(
                ActionType.getLastMsgHis,
                getActivity(),
                Util.LOGIN_USER.getUserName(),
                new OnActionCallback() {
                    @Override
                    public void onActionCallback(boolean result, Object object) {
                        // TODO Auto-generated method stub
                        if (result) {
                            chatHistoryList = (List<ChatHistoryInfo>) object;
                            Collections.sort(chatHistoryList, new Comparator<ChatHistoryInfo>() {
                                @Override
                                public int compare(ChatHistoryInfo lhs, ChatHistoryInfo rhs) {
                                    return rhs.getLastXmppMessage().getCreateTime().compareTo(
                                        lhs.getLastXmppMessage().getCreateTime());
                                }
                            });
                            initOrRefreshAdapter();
                        }

                    }
                },
                ReturnMode.listMode);
        dbTask.execute("");
    }

    private void init() {

        chatHistoryListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                ChatHistoryInfo chatHistoryInf = chatHistoryList.get(arg2);
                Intent i = null;
                if (chatHistoryInf.getLastXmppMessage().getMessageType() == 1) {
                    i = new Intent(getActivity(), ChatActivity.class);
                }
                else if (chatHistoryInf.getLastXmppMessage().getMessageType() == XmppMessage.MESSAGE_Type_SYSTEM_MSG) {
                    i = new Intent(getActivity(), SystemMsgActivity.class);
                }
                Bundle data = new Bundle();
                User user = new User();
                user.setUserName(chatHistoryInf.getChatFromUser());
                data.putSerializable("friend", user);
                i.putExtras(data);
                DataBaseAsyncTask dbTask =
                    new DataBaseAsyncTask(
                        ActionType.updateMsgReadStatus,
                        getActivity(),
                        chatHistoryInf.getChatFromUser(),
                        new OnActionCallback() {
                            @Override
                            public void onActionCallback(boolean result, Object object) {
                                //
                            }
                        },
                        ReturnMode.objectMode);
                dbTask.execute("");
                startActivity(i);

            }
        });
    }

    void initOrRefreshAdapter() {
        if (chatHistoryListViewAdapeter == null) {
            chatHistoryListViewAdapeter = new ChatHistoryAdapter(chatHistoryList, getActivity());
            chatHistoryListView.setAdapter(chatHistoryListViewAdapeter);
        }
        else {
            chatHistoryListViewAdapeter.setChatHistoryList(chatHistoryList);
            chatHistoryListViewAdapeter.notifyDataSetChanged();
        }
        if(chatHistoryList != null && chatHistoryList.size() > 0){
        	no_recore_text.setVisibility(View.GONE);
        }else{
        	no_recore_text.setVisibility(View.VISIBLE);
        }
        loading.setVisibility(View.GONE);
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(JaxmppConnectManager.XMPP_MESSAGE_FROM_FRIEND);
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(JaxmppConnectManager.XMPP_MESSAGE_FROM_FRIEND)) {
                if (intent != null) {
                    Bundle data = intent.getExtras();
                    if (data.containsKey(JaxmppConnectManager.FROM_FRIEND_MSG)) {
                        XmppMessage xmppMessage =
                            (XmppMessage) data.getSerializable(JaxmppConnectManager.FROM_FRIEND_MSG);
                        ChatHistoryInfo chatHistoryInfo = new ChatHistoryInfo();
                        if (xmppMessage.isSend()) {
                            chatHistoryInfo.setChatFromUser(xmppMessage.getMessageTo());
                        }
                        else {
                            chatHistoryInfo.setChatFromUser(xmppMessage.getMessageFrom());
                        }
                        chatHistoryInfo.setLastXmppMessage(xmppMessage);
                        if (!chatHistoryList.contains(chatHistoryInfo)) {
                            chatHistoryInfo.setUnReadCount(xmppMessage.NOT_READ);
                        }
                        else {
                            ChatHistoryInfo chatHistoryInfo2 = null;
                            for (ChatHistoryInfo chl : chatHistoryList) {
                                if (chl.equals(chatHistoryInfo)) {
                                    chatHistoryInfo2 = chl;
                                    break;
                                }
                            }
                            chatHistoryList.remove(chatHistoryInfo2);
                            if (xmppMessage.getIsMsgRead() == XmppMessage.NOT_READ) {
                                chatHistoryInfo.setUnReadCount(XmppMessage.NOT_READ + chatHistoryInfo2.getUnReadCount());
                            }
                            else {
                                chatHistoryInfo.setUnReadCount(XmppMessage.IS_READ);
                            }
                        }
                        chatHistoryList.add(chatHistoryInfo);
                        Collections.sort(chatHistoryList, new Comparator<ChatHistoryInfo>() {
                            @Override
                            public int compare(ChatHistoryInfo lhs, ChatHistoryInfo rhs) {
                                return rhs.getLastXmppMessage().getCreateTime().compareTo(
                                    lhs.getLastXmppMessage().getCreateTime());
                            }
                        });
                        initOrRefreshAdapter();
                    }
                }
            }
        }
    };

    @Override
    public void onUpdate() {
        // TODO Auto-generated method stub
        super.onUpdate();
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onDetach();
    }

}
