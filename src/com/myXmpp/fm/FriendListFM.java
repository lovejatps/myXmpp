package com.myXmpp.fm;

import java.util.ArrayList;
import java.util.List;

import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.Connector.State;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.myXmpp.R;
import com.myXmpp.asyctask.DataBaseAsyncTask;
import com.myXmpp.asyctask.PresenceAsyclTask;
import com.myXmpp.asyctask.DataBaseAsyncTask.ActionType;
import com.myXmpp.asyctask.DataBaseAsyncTask.OnActionCallback;
import com.myXmpp.asyctask.DataBaseAsyncTask.ReturnMode;
import com.myXmpp.jaxmpp.JaxmppConnectManager;
import com.myXmpp.struct.User;

public class FriendListFM extends BaseFragment {

    View friendListView;
    //    TextView friend_name;
    List<User> friendList = new ArrayList<User>();
    ListView friend_list;
    FriendAdapter friendAdapter;
//    Map<String, ArrayList<XmppMessage>> friendMessgaeList = new HashMap<String, ArrayList<XmppMessage>>();
    private User handlerUser=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getFriend();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        friendListView = inflater.inflate(R.layout.friend_list_main, null);
        init();
        initListener();
        return friendListView;
    }

    void init() {
        friend_list = (ListView) friendListView.findViewById(R.id.friend_list);
    }

    void initListener(){
        friend_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getActivity(), ChatActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("friend", friendList.get(arg2));
                i.putExtras(data);
                startActivity(i);
                initOrRefreshAdapter();
            }
        });

        friend_list.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                handlerUser = (User) friendList.get(position);
                friend_list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
                    public void onCreateContextMenu(ContextMenu menu, View arg1, ContextMenu.ContextMenuInfo arg2) {
                        menu.setHeaderTitle("操作");
                        menu.add(0, 0, 0, "发起会话");
                        menu.add(0, 1, 0, "删除好友");
                    }
                });
                return false;
            }
        });
    }

    void getFriend() {
        DataBaseAsyncTask dbTask = new DataBaseAsyncTask(ActionType.getFriendList, getActivity(), null, new OnActionCallback() {


            @Override
            public void onActionCallback(boolean result, Object object) {
                // TODO Auto-generated method stub
                if(result){
                    friendList = (List<User>) object;
                    initOrRefreshAdapter();
                }

            }
        }, ReturnMode.listMode);
        dbTask.execute("");
    }

    void initOrRefreshAdapter(){
        if(friendAdapter == null){
            friendAdapter = new FriendAdapter(friendList, getActivity());
            friend_list.setAdapter(friendAdapter);
        }else{
            friendAdapter.setFriendList(friendList);
            friendAdapter.notifyDataSetChanged();
        }
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(JaxmppConnectManager.XMPP_MESSAGE_UPDATE_FRIENDLIST);
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(JaxmppConnectManager.XMPP_MESSAGE_UPDATE_FRIENDLIST)) {
                getFriend();
            }
        }

    };

//    private void getMessageAndRefresh(XmppMessage msg){
//
//        if(friendMessgaeList == null){
//            friendMessgaeList = new HashMap<String, ArrayList<XmppMessage>>();
//        }
//        if(friendMessgaeList.containsKey(msg.getMessageFrom())){
//            if(ChatActivity.chattingUser == null || !ChatActivity.chattingUser.equals(msg.getMessageFrom())){
//                friendMessgaeList.get(msg.getMessageFrom()).add(msg);
//            }
//        }else{
//            ArrayList<XmppMessage> messageList = new ArrayList<XmppMessage>();
//            if(ChatActivity.chattingUser == null || !ChatActivity.chattingUser.equals(msg.getMessageFrom())){
////                friendMessgaeList.get(msg.getMessageFrom()).add(msg);
//                messageList.add(msg);
//            }
//            friendMessgaeList.put(msg.getMessageFrom(), messageList);
//        }
//        initOrRefreshAdapter();
//    }

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            getFriend();
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0 :
                Intent i = new Intent(getActivity(), ChatActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("friend", handlerUser);
                i.putExtras(data);
                startActivity(i);
                initOrRefreshAdapter();
                break;
            case 1 :
                JaxmppConnectManager connect = JaxmppConnectManager.getInstance();
                if (connect.getState() == State.connected) {
                    try {
                        new PresenceAsyclTask(handlerUser.getUserId(),PresenceAsyclTask.ActionType.removeRosterItem).execute("");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default :
                break;
        }
        return super.onContextItemSelected(item);
    }
}
