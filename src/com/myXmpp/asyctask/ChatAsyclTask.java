package com.myXmpp.asyctask;

import tigase.jaxmpp.core.client.JID;
import android.content.Context;
import android.os.AsyncTask;

import com.myXmpp.db.DBManager;
import com.myXmpp.jaxmpp.ChatCallBack;
import com.myXmpp.jaxmpp.JaxmppConnectManager;
import com.myXmpp.struct.XmppMessage;

public class ChatAsyclTask extends AsyncTask<String, Integer, String> {

//    Chat chat = null;
    XmppMessage msg;
    public Status mStatus = Status.pending;
    ChatCallBack chatCallBack;
    Context mContext;
    JaxmppConnectManager jaxmpp;

    public enum Status {
        pending,
        sended,
        error,
    }

    public ChatAsyclTask(XmppMessage msg, ChatCallBack chatCallBack, Context mContext) {
        // TODO Auto-generated constructor stub
//        this.chat = chat;
        this.msg = msg;
        this.chatCallBack = chatCallBack;
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        mStatus = Status.pending;
        msg.setMessageStatus(mStatus) ;
        try {
//            new Thread().sleep(3000);
//            chat.sendMessage(msg.getMessageBody());
        	jaxmpp.getInstance().getJaxmpp().sendMessage(JID.jidInstance(msg.getMessageTo()), "", msg.getMessageBody());
            mStatus = Status.sended;
            msg.setMessageStatus(Status.sended) ;
//            sendhandlemsg(ChatActivity.chattingUser,msg,true);
        }
        catch (Exception e){
            e.printStackTrace();
            mStatus = Status.error;
            msg.setMessageStatus(mStatus) ;
        }finally{
            msg.setIsMsgRead(XmppMessage.IS_READ);
            msg.setSend(true);
            DBManager.getInstance(mContext).insertMsg(msg);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        System.out.println("onPostExecute");
        chatCallBack.onChatCallBack(msg);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
        System.out.println("onProgressUpdate");
        msg.setMessageStatus(Status.sended) ;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        System.out.println("onPreExecute");
    }

}
