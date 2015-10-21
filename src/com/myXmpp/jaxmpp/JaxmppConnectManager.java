package com.myXmpp.jaxmpp;

import java.util.ArrayList;
import java.util.List;

import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.Connector;
import tigase.jaxmpp.core.client.Connector.State;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.chat.MessageModule;
import tigase.jaxmpp.core.client.xmpp.modules.chat.MessageModule.MessageEvent;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule.PresenceEvent;
import tigase.jaxmpp.core.client.xmpp.modules.pubsub.PubSubErrorCondition;
import tigase.jaxmpp.core.client.xmpp.modules.pubsub.PubSubModule;
import tigase.jaxmpp.core.client.xmpp.modules.pubsub.PubSubModule.PubSubEvent;
import tigase.jaxmpp.core.client.xmpp.modules.pubsub.PubSubModule.SubscriptionElement;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterModule;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterModule.RosterEvent;
import tigase.jaxmpp.core.client.xmpp.stanzas.IQ;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;
import tigase.jaxmpp.j2se.Jaxmpp;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.myXmpp.MainActivity;
import com.myXmpp.R;
import com.myXmpp.Utils.Util;
import com.myXmpp.asyctask.DataBaseAsyncTask;
import com.myXmpp.asyctask.DataBaseAsyncTask.ActionType;
import com.myXmpp.asyctask.DataBaseAsyncTask.OnActionCallback;
import com.myXmpp.asyctask.DataBaseAsyncTask.ReturnMode;
import com.myXmpp.asyctask.PresenceAsyclTask;
import com.myXmpp.fm.ChatActivity;
import com.myXmpp.service.XmppService;
import com.myXmpp.struct.PresenceXmppMessage;
import com.myXmpp.struct.User;
import com.myXmpp.struct.XmppMessage;


public class JaxmppConnectManager {
	
	private static final String TAG ="JaxmppConnectManager";
	
	private static JaxmppConnectManager jaxmppConnectManager;
	private static Jaxmpp jaxmpp;
	private Context mContext;
	public static String domain = "192.168.100.161";//your domain
	private static String serverIP = "192.168.100.161";//your Ip
	private static int serverPort = 5222;			//your port
	private static String resource = "Android";
	private State state = Connector.State.disconnected;
	SharedPreferences spf = null;
	public final static String PWD = "pwd";
	public final static String USER_NAME = "user_name";
	public final static String USER_SPF = "user";
	public final static String USER_IS_LOGIN = "is_login";

    public final static String XMPP_MESSAGE_FROM_SYSTEM = "xmpp_message_from_system";
	public final static String XMPP_MESSAGE_UPDATE_FRIENDLIST = "xmpp_message_update_friendlist";
	public final static String XMPP_MESSAGE_FROM_CHATTING = "xmpp_message_from_chatting";
	public final static String FROM_FRIEND_MSG = "from_friend_msg";
	public final static String XMPP_MESSAGE_FROM_FRIEND = "xmpp_message_from_friend";
	public static boolean isNetWorkBreak = false;
	boolean isLock = false;
	Handler mHandler;

	public void initialize(Context ctx,  Handler handler){
		mContext =ctx;
		mHandler = handler;
	}

	public JaxmppConnectManager() {
		// TODO Auto-generated constructor stub
	}

	public State getState() {
		return state;
	}

	public static JaxmppConnectManager getInstance() {
		if (jaxmppConnectManager == null) {
			jaxmppConnectManager = new JaxmppConnectManager();
		}
		return jaxmppConnectManager;
	}

	public static Jaxmpp getJaxmpp() {
		return jaxmpp;
	}

	void init(){
		if(jaxmpp == null){
			jaxmpp = new Jaxmpp();
		}

		jaxmpp.getConnectionConfiguration().setServer(serverIP);
		jaxmpp.getConnectionConfiguration().setPort(serverPort);
		jaxmpp.getConnectionConfiguration().setDomain(domain);
		jaxmpp.getConnectionConfiguration().setResource(resource);
	}

	public void login(final String userName, final String pwd){
		init();
		new Thread(){

			@Override
			public void run() {
				try {
					isLock = true;
					String user = userName + "@" + domain;
					System.out.println(user   + "   "  + pwd);
					jaxmpp.getConnectionConfiguration().setUserJID(user);
					jaxmpp.getConnectionConfiguration().setUserPassword(pwd);
					if (spf == null) {
						spf = mContext.getSharedPreferences(USER_SPF, 0);
						spf.edit().putString(USER_NAME, userName)
								.putString(PWD, pwd).putBoolean(USER_IS_LOGIN, false).commit();
					}
					
					if (Util.LOGIN_USER == null) {
						Util.LOGIN_USER = new User();
					}
					Util.LOGIN_USER.setUserName(user);
					Util.LOGIN_USER.setNickName(userName);
					Util.LOGIN_USER.setPwd(pwd);
					initBeforeLogin();
					jaxmpp.login(true);
					if (jaxmpp != null && jaxmpp.getConnector().getState() != Connector.State.connected)
						mHandler.sendEmptyMessage(XmppService.LOGIN_FAIL);
					else{
						mHandler.sendEmptyMessage(XmppService.LOGIN_SUCCESS);
						initAfterLogin();
					}
				} catch (JaxmppException e) {
					// TODO Auto-generated catch block
					Log.i(TAG, e.toString());
					mHandler.sendEmptyMessage(XmppService.LOGIN_FAIL);
				}finally{
					isLock = false;
				}
				state = jaxmpp.getConnector().getState();

			};
		}.start();
	}
	void initBeforeLogin(){
		initChatMsgListener();
//		initFriendStatusListener();
		initConnectChangeListener();
	}

	void initAfterLogin(){
		initFriendList();
		initFriendStatusListener();
		initRosterItemListener();
		subscribe("NEWS_EN");
	}



    void initFriendStatusListener() {
        jaxmpp.getModulesManager().getModule(PresenceModule.class).addListener(MessageModule.MessageReceived, new Listener<PresenceEvent>() {

            @Override
            public void handleEvent(PresenceEvent presenceEvent) throws JaxmppException {
                Presence presence = presenceEvent.getPresence();
                if(presence==null) return;
                JID fromJid = presence.getFrom();
                JID toJid = presence.getTo();
                RosterItem rosterItem = jaxmppConnectManager.jaxmpp.getRoster().get(fromJid.getBareJid());
                if(presence.getType()==null)return;
                if (presenceEvent.getType().equals(PresenceModule.ContactAvailable)) {

                }
                else if (presence.getType().equals(StanzaType.subscribed)) {
                    if (rosterItem != null && rosterItem.getSubscription().equals(RosterItem.Subscription.from)) {
                        return;
                    }
                    sendPresenceMessage(fromJid.toString(), toJid.toString(), PresenceXmppMessage.PRESENCE_SUBSCRIBED,0);
                }
                else if (presence.getType().equals(StanzaType.unsubscribed)) {
                    if (rosterItem != null && rosterItem.getSubscription().equals(RosterItem.Subscription.none)) {
                        sendPresenceMessage(fromJid.toString(), toJid.toString(), PresenceXmppMessage.PRESENCE_UNSUBSCRIBE,0);
                        return;
                    }
                    sendPresenceMessage(fromJid.toString(), toJid.toString(), PresenceXmppMessage.PRESENCE_UNSUBSCRIBED,0);
                }
                else if (presence.getType().equals(StanzaType.unsubscribe)) {
                    if (rosterItem != null && rosterItem.getSubscription().equals(RosterItem.Subscription.both)) {
                        return;
                    }
                    sendPresenceMessage(fromJid.toString(), toJid.toString(), PresenceXmppMessage.PRESENCE_UNSUBSCRIBE,0);
                }
                else if (presence.getType().equals(StanzaType.set)) {

                }
                else if (presence.getType().equals(StanzaType.unavailable)) {

                }
                else if (presence.getType().equals(StanzaType.subscribe)) {
                    if (rosterItem != null && rosterItem.getSubscription().equals(RosterItem.Subscription.to)) {
                        Presence p = Presence.create();
                        p.setType(StanzaType.subscribed);
                        p.setTo(fromJid);
                        new PresenceAsyclTask(p,PresenceAsyclTask.ActionType.sendPresence).execute("");
                        return;
                    }
                    sendPresenceMessage(fromJid.toString(), toJid.toString(), PresenceXmppMessage.PRESENCE_SUBSCRIBE,1);
                }
            }
        });
    }

	private void sendPresenceMessage(String from, String to, int value,int type) {
        Intent mIntent = new Intent();
        mIntent.setAction(XMPP_MESSAGE_FROM_SYSTEM);
        PresenceXmppMessage presenceXmppMessage = new PresenceXmppMessage();
        presenceXmppMessage.setMessageFrom(from);
        presenceXmppMessage.setMessageTo(to);
        presenceXmppMessage.setPresenceType(value);
        presenceXmppMessage.setIsMsgRead(XmppMessage.NOT_READ);
        presenceXmppMessage.setMessageType(XmppMessage.MESSAGE_Type_SYSTEM_MSG);
        presenceXmppMessage.setHandFlag(type);
        insertToMsgHis(presenceXmppMessage);
        XmppMessage xmppMsg = new XmppMessage();
        xmppMsg.setCreateTime(presenceXmppMessage.getCreateTime());
        xmppMsg.setIsMsgRead(presenceXmppMessage.getIsMsgRead());
        xmppMsg.setMessageBody(presenceXmppMessage.toString()+"_"+presenceXmppMessage.getMessageFrom());
        xmppMsg.setMessageFrom("系统消息");
        xmppMsg.setMessageTo(presenceXmppMessage.getMessageTo());
        xmppMsg.setSend(presenceXmppMessage.isSend());
        xmppMsg.setMessageStatus(presenceXmppMessage.getMessageStatus());
        xmppMsg.setMessageType(presenceXmppMessage.getMessageType());
        sendMessage(XMPP_MESSAGE_FROM_FRIEND, xmppMsg);
    }

    void initRosterItemListener() {
        jaxmppConnectManager.jaxmpp.getModulesManager().getModule(RosterModule.class).addListener(
            new Listener<RosterModule.RosterEvent>() {
                @Override
                public void handleEvent(RosterEvent be) throws JaxmppException {
                    if (be.getType().equals(RosterModule.ItemAdded)) {
//                        updateFriends(be, User.DBFLAG_ADD);
                    }
                    else if (be.getType().equals(RosterModule.ItemRemoved)) {
                        updateFriends(be, User.DBFLAG_DELETE);
                    }
                    else if (be.getType().equals(RosterModule.ItemUpdated)) {
                        updateFriends(be, User.DBFLAG_UPDATE);
                    }
                }
            });
    }

    public void updateFriends(RosterEvent be, int handleFlag) {
        List<User> friends = new ArrayList<User>();
        if (handleFlag == User.DBFLAG_DELETE) {
            User friend = new User();
            friend.setUserId(be.getItem().getJid().toString());
            friend.setDbFlag(handleFlag);
            friends.add(friend);
        }
        else {
            RosterItem rosterItem = jaxmppConnectManager.jaxmpp.getRoster().get(be.getItem().getJid());
            User friend = new User();
            friend.setNickName(rosterItem.getName());
            friend.setUserName(rosterItem.getJid().toString());
            friend.setUserId(rosterItem.getJid().toString());
            friend.setUserHeadWord(rosterItem.getName());
            friend.setUserItemType(rosterItem.getSubscription());
            friend.setDbFlag(handleFlag);
            friends.add(friend);
        }
        DataBaseAsyncTask dbTask =
            new DataBaseAsyncTask(ActionType.updateFriendList, mContext, friends, new OnActionCallback() {
                public void onActionCallback(boolean result, Object object) {
                    if (result) {
                        Intent i = new Intent(XMPP_MESSAGE_UPDATE_FRIENDLIST);
                        mContext.sendBroadcast(i);
                    }
                }
            }, ReturnMode.objectMode);
        dbTask.execute("");
    }

    void initFriendList() {
        List<User> friends = new ArrayList<User>();
        int size = jaxmpp.getRoster().getAll().size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                RosterItem rosterEntry = jaxmpp.getRoster().getAll().get(i);
                User friend = new User();
                friend.setNickName(rosterEntry.getName());
                friend.setUserName(rosterEntry.getJid().toString());
                friend.setUserId(rosterEntry.getJid().toString());
                friend.setUserHeadWord(rosterEntry.getName());
                friend.setUserItemType(rosterEntry.getSubscription());
                friends.add(friend);
            }
            DataBaseAsyncTask dbTask =
                new DataBaseAsyncTask(ActionType.initFriendList, mContext, friends, new OnActionCallback() {

                    public void onActionCallback(boolean result, Object object) {
                        // TODO Auto-generated method stub
                        if (result) {
                            Intent i = new Intent(XMPP_MESSAGE_UPDATE_FRIENDLIST);
                            mContext.sendBroadcast(i);
                        }

                    }
                }, ReturnMode.objectMode);
            dbTask.execute("");
        }
    }

	private void setNotiType(int iconId, String contentTitle,
			String contentText, Class activity, String from) {
		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
		Intent notifyIntent = new Intent(mContext, activity);
		notifyIntent.putExtra("to", from);
		// notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(mContext, 0,
				notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		/* 创建Notication，并设置相关参数 */
		Notification myNoti = new Notification();
		// 点击自动消失
		myNoti.flags = Notification.FLAG_AUTO_CANCEL;
		/* 设置statusbar显示的icon */
		myNoti.icon = iconId;
		/* 设置statusbar显示的文字信息 */
		myNoti.tickerText = contentTitle;
		/* 设置notification发生时同时发出默认声音 */
		myNoti.defaults = Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
		myNoti.setLatestEventInfo(mContext, contentTitle, contentText,
				appIntent);
		/* 送出Notification */
		NotificationManager notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, myNoti);
	}

	/**
	 * reg chat messge listener
	 * */
	void initChatMsgListener(){
		
		jaxmppConnectManager.jaxmpp.getModulesManager().getModule( MessageModule.class ).addListener( MessageModule.MessageReceived,new Listener<MessageEvent>() {

            @Override
            public void handleEvent(MessageEvent be) throws JaxmppException {
            	
            	if(be != null && be.getType() != null ){
					if(be.getType().equals(MessageModule.ChatStateChanged)){
						 XmppMessage xmppMsg = new XmppMessage();
	                	  xmppMsg.setMessageBody(be.getMessage().getBody());
	                	  String from = be.getMessage().getFrom()+"";
	                	  from = from.contains("/")? from.substring(0, from.indexOf("/")) : from;
	                	  xmppMsg.setMessageFrom(from);
	                	  String to = be.getMessage().getTo()+"";
	                	  to = to.contains("/") ? to.substring(0, to.indexOf("/")) : to;
	                	  System.out.println("msg  :  " + be.getMessage().getBody());
	                	  xmppMsg.setMessageTo(to);
	                	  if (ChatActivity.chattingUser == null
	      						|| !ChatActivity.chattingUser.equals(xmppMsg.getMessageFrom()
	      								)) {
	      					setNotiType(R.drawable.ic_launcher, xmppMsg.getMessageFrom(),
	      							xmppMsg.getMessageBody(), MainActivity.class, "");
	      					xmppMsg.setIsMsgRead(XmppMessage.NOT_READ);
	      				} else if (ChatActivity.chattingUser != null
	      						&& ChatActivity.chattingUser.equals(xmppMsg.getMessageFrom()
	      								)) {
	      					xmppMsg.setIsMsgRead(XmppMessage.IS_READ);
	      					sendMessage(XMPP_MESSAGE_FROM_CHATTING, xmppMsg);
	      				} else {
	      					setNotiType(R.drawable.ic_launcher, xmppMsg.getMessageFrom(),
	      							xmppMsg.getMessageBody(), MainActivity.class, "");
	      					xmppMsg.setIsMsgRead(XmppMessage.NOT_READ);
	      				}
	                	 insertToMsgHis(xmppMsg);
	      				 sendMessage(XMPP_MESSAGE_FROM_FRIEND, xmppMsg);
					}
					
				}

            }
        });

	}


	/**
	 * reg connect change listener
	 * */
	void initConnectChangeListener(){

		jaxmpp.getConnector().addListener(Connector.StateChanged, new Listener<Connector.ConnectorEvent>() {

			@Override
			public void handleEvent(Connector.ConnectorEvent paramAnonymousConnectorEvent) throws JaxmppException {
				// TODO Auto-generated method stub
				 state = jaxmpp.getConnector().getState();
				 if(state == State.disconnected){
					 mHandler.sendEmptyMessage(XmppService.RECONNECT);
				 }else{

				 }
			}

		});
	}
	
	
	/**
	 * subscribe a topin
	 * */
	void subscribe(String topic){
		PubSubModule pubsub = jaxmpp.getModule(PubSubModule.class);
		try {
			pubsub.subscribe(BareJID.bareJIDInstance("pubsub." + domain) , topic, JID.jidInstance(Util.LOGIN_USER.getUserName()), new  PubSubModule.SubscriptionAsyncCallback() {
				
				@Override
				public void onTimeout() throws JaxmppException {
					// TODO Auto-generated method stub
					System.out.println("----onTimeout----");
				}
				
				@Override
				protected void onEror(IQ response, ErrorCondition errorCondition,
						PubSubErrorCondition pubSubErrorCondition) throws JaxmppException {
					// TODO Auto-generated method stub
					System.out.println("----onEror----");
				}
				
				@Override
				protected void onSubscribe(IQ response,
						SubscriptionElement subscriptionElement) {
					// TODO Auto-generated method stub
					System.out.println("----onSubscribe----");
					try {
						System.out.println("response : " + response.getAsString());
						System.out.println("subscriptionElement : " + subscriptionElement.getAsString());
					} catch (XMLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			pubsub.addListener(new Listener<PubSubEvent>() {

				@Override
				public void handleEvent(PubSubEvent be) throws JaxmppException {
					// TODO Auto-generated method stub
					if(be != null && be.getMessage() != null){
						System.out.println("sub  :  "   +   be.getMessage().getAsString());
					}
				}
			});
		} catch (JaxmppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * reconnect
	 * */
	public void reconnect(){
		if(state == State.disconnected){
			if(!isNetWorkBreak && !isLock && jaxmpp != null){
//				login(Util.LOGIN_USER.getUserName(), Util.LOGIN_USER.getPwd());
				new Thread(){
					public void run() {
						try {
							isLock = true;
							jaxmpp.login(true);
							if (jaxmpp != null
									&& jaxmpp.getConnector().getState() != Connector.State.connected)
								mHandler.sendEmptyMessage(XmppService.RECONNECT);
							else{
								mHandler.sendEmptyMessage(XmppService.RELOGIN_SUCCESS);
								initFriendList();
							}
						} catch (JaxmppException e) {
							// TODO Auto-generated catch block
							mHandler.sendEmptyMessage(XmppService.RECONNECT);
						}finally{
							isLock = false;
						}
					};
				}.start();

			}
		}
	}

	private void sendMessage(String messageType, XmppMessage msg) {
		Intent mIntent = new Intent(messageType);
		Bundle b = new Bundle();
		b.putSerializable(FROM_FRIEND_MSG, msg);
		mIntent.putExtras(b);
		// 发送广播
		mContext.sendBroadcast(mIntent);
	}

	
	void insertToMsgHis(XmppMessage msg) {
		new DataBaseAsyncTask(ActionType.insertMsg, mContext, msg,
				new OnActionCallback() {

					@Override
					public void onActionCallback(boolean result, Object object) {
						// TODO Auto-generated method stub

					}
				}, ReturnMode.objectMode).execute("");
	}

    void insertToMsgHis(PresenceXmppMessage presenceXmppMessage) {
        new DataBaseAsyncTask(ActionType.insertSysMsg, mContext, presenceXmppMessage, new OnActionCallback() {
            @Override
            public void onActionCallback(boolean result, Object object) {

            }
        }, ReturnMode.objectMode).execute("");
    }

	public void disconnect(){
		if(jaxmpp != null){
			try {
				jaxmpp.removeAllListeners();
				jaxmpp.login(false);
				jaxmpp.disconnect();
				spf.edit().putBoolean(JaxmppConnectManager.USER_IS_LOGIN, false).commit();
			} catch (JaxmppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
