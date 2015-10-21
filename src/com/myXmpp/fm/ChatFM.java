package com.myXmpp.fm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.myXmpp.R;
import com.myXmpp.Utils.Util;
import com.myXmpp.asyctask.ChatAsyclTask;
import com.myXmpp.asyctask.DataBaseAsyncTask;
import com.myXmpp.asyctask.DataBaseAsyncTask.ActionType;
import com.myXmpp.asyctask.DataBaseAsyncTask.OnActionCallback;
import com.myXmpp.asyctask.DataBaseAsyncTask.ReturnMode;
import com.myXmpp.jaxmpp.ChatCallBack;
import com.myXmpp.jaxmpp.JaxmppConnectManager;
import com.myXmpp.struct.User;
import com.myXmpp.struct.XmppMessage;

public class ChatFM extends Fragment implements OnClickListener, ChatCallBack {

	View chatView;
	List<XmppMessage> messageList = new ArrayList<XmppMessage>();;
	TextView main_sendto;
	Button sendmessage, back;
	ListView messagebody;
	MessageAdapter mseeageAdapter;
//	private Chat chat = null;
	EditText message;
	Intent i;
	User friend = null;
	private LinearLayout listHeaderLayout;
	private boolean isFinishChatMsg = false;
	private boolean isBottom = true;

	// Map<String, XmppMessage> msgMap = new HashMap<String, XmppMessage>();

	public ChatFM(Intent i) {
		// TODO Auto-generated constructor stub
		this.i = i;
		this.friend = (User) i.getExtras().getSerializable("friend");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		registerBoradcastReceiver();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		chatView = inflater.inflate(R.layout.chat, null);
		initView();
		initListener();
		refreshChat();
		return chatView;
	}

	void initView() {
		main_sendto = (TextView) chatView.findViewById(R.id.main_sendto);
		sendmessage = (Button) chatView.findViewById(R.id.sendmessage);
		back = (Button) chatView.findViewById(R.id.back);
		messagebody = (ListView) chatView.findViewById(R.id.messagebody);
		message = (EditText) chatView.findViewById(R.id.message);
		listHeaderLayout = (LinearLayout) getActivity().getLayoutInflater()
				.inflate(R.layout.chat_list_header, null);
	}

	void initListener() {
		back.setOnClickListener(this);
		sendmessage.setOnClickListener(this);
		messagebody.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:

					// 判断滚动到底部
					if (messagebody.getLastVisiblePosition() == (messagebody
							.getCount() - 1)) {
						isBottom = true;
					}else
					{
						isBottom = false;
					}
					// 判断滚动到顶部
					if (messagebody.getFirstVisiblePosition() == 0 && !isFinishChatMsg) {
						// getChatMsg();
						messagebody.addHeaderView(listHeaderLayout);
						friend.setPageNumber(friend.getPageNumber() + 1);
//						refreshChat();
						loadMoreChat();
						// listHeaderLayout.setVisibility(View.GONE);
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void refreshChat() {
		main_sendto.setText(friend.getNickName());
		// if(i != null){
		// messageList = (List<XmppMessage>) i.getSerializableExtra("msg_list");
		// }
		new DataBaseAsyncTask(ActionType.getMsgHis, getActivity(), friend,
				new OnActionCallback() {

					@Override
					public void onActionCallback(boolean result, Object object) {
						// TODO Auto-generated method stub
						if (result) {
							messageList = (List<XmppMessage>) object;
							Collections.reverse(messageList);
							initOrRefresAdapter();
						}
					}
				}, ReturnMode.listMode).execute("");
	}

	void loadMoreChat() {
		new DataBaseAsyncTask(ActionType.getMsgHis, getActivity(), friend,
				new OnActionCallback() {

					@Override
					public void onActionCallback(boolean result, Object object) {
						// TODO Auto-generated method stub
						if (result) {
							List<XmppMessage> list = (List<XmppMessage>) object;
							if(list.size() == 0) {
								isFinishChatMsg = true;
							} else {
								if(list.size() < friend.getPageSize()) {
									isFinishChatMsg = true;
								} else {
									isFinishChatMsg = false;
								}
								Collections.reverse(list);
								mseeageAdapter.addData(list);
							}
							messagebody.removeHeaderView(listHeaderLayout);
							messagebody.setSelection(list.size() - 1);
						}
					}
				}, ReturnMode.listMode).execute("");
	}

	public void initOrRefresAdapter() {
		if (mseeageAdapter == null) {
			mseeageAdapter = new MessageAdapter(messageList, getActivity(), chatView, this);
			messagebody.addHeaderView(listHeaderLayout);
			messagebody.setAdapter(mseeageAdapter);
			messagebody.removeHeaderView(listHeaderLayout);
		} else {
			mseeageAdapter.notifyDataSetChanged();
		}
		if(isBottom)
		messagebody.setSelection(messageList.size());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			getActivity().finish();
			break;
		case R.id.sendmessage:
			if ("".equals(message.getText().toString().trim())) {
				return;
			}
			sendMessage(message.getText().toString());
			break;

		default:
			break;
		}
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		getActivity().unregisterReceiver(mBroadcastReceiver);
	}

	public void sendMessage(final String msg) {
//		if (chat == null) {
//			chat = XmppConnectManager.getInstance().getXmppConnection()
//					.getChatManager()
//					.createChat(friend.getUserName(), null);
//		}
		message.setText("");
		XmppMessage xmppMsg = new XmppMessage(msg,
				Util.LOGIN_USER.getUserName(), friend.getUserName(),
				true);
		xmppMsg.setMessageStatus(ChatAsyclTask.Status.pending);
		messageList.add(xmppMsg);
		// msgMap.put(xmppMsg.getMessageFrom()+xmppMsg.getCreateTime(),
		// xmppMsg);
		initOrRefresAdapter();
		new ChatAsyclTask(xmppMsg, this, getActivity()).execute("");
		// new Thread()
		// {
		// @Override
		// public void run() {
		// try {
		// chat.sendMessage(msg);
		// // sendhandlemsg(ChatActivity.chattingUser,msg,true);
		// } catch (XMPPException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }.start();
	}
	
	public void reSendMessage(XmppMessage xmppMsg, int location) {
		message.setText("");
		xmppMsg.setMessageStatus(ChatAsyclTask.Status.pending);
		messageList.remove(location);
		messageList.add(xmppMsg);
		initOrRefresAdapter();
		new ChatAsyclTask(xmppMsg, this, getActivity()).execute("");
	}

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(JaxmppConnectManager.XMPP_MESSAGE_FROM_FRIEND);
		// 注册广播
		getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			System.out.println("action    :   " + action);
			if (action.equals(JaxmppConnectManager.XMPP_MESSAGE_FROM_FRIEND)) {
				// Toast.makeText(Test.this, "处理action名字相对应的广播", 200);
				if (intent != null) {
					Bundle data = intent.getExtras();
					if (data.containsKey(JaxmppConnectManager.FROM_FRIEND_MSG)) {
						messageList
								.add((XmppMessage) data
										.getSerializable(JaxmppConnectManager.FROM_FRIEND_MSG));
						initOrRefresAdapter();
					}
				}
			}
		}

	};

	@Override
	public void onChatCallBack(XmppMessage msg) {
		// TODO Auto-generated method stub
		// if(msgMap.containsKey(msg.getMessageFrom()+msg.getCreateTime())){
		// System.out.println("msg.getMessageStatus()   :  " +
		// msg.getMessageStatus());
		// msgMap.get(msg.getMessageFrom()+msg.getCreateTime()).setMessageStatus(msg.getMessageStatus());
		// msgMap.remove(msg.getMessageFrom()+msg.getCreateTime());
		// }
		initOrRefresAdapter();
	}


}
