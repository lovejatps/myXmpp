package com.myXmpp.asyctask;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.myXmpp.db.DBManager;
import com.myXmpp.struct.PresenceXmppMessage;
import com.myXmpp.struct.User;
import com.myXmpp.struct.XmppMessage;

public class DataBaseAsyncTask extends AsyncTask<String, Integer, String> {

    ActionType type;
    Context mContext;
    DBManager db;
//    ArrayList list;
    Object object;
    OnActionCallback onActionCallback;
    ArrayList returnList =  new ArrayList();
    ReturnMode mode;
    boolean success = false;
    Object returnObject;

    public interface OnActionCallback{
        void onActionCallback(boolean result, Object object);
    }

    public enum ActionType {
        initFriendList,
        updateFriendList,
        getFriendList,
        insertMsg,
        getMsgHis,
        getLastMsgHis,
        updateMsgReadStatus,
        insertSysMsg,
        updateMsgStatus,
    }

    public enum ReturnMode{
        objectMode,
        listMode,
    }


//    public DataBaseAsyncTask(ActionType type, Context mContext, ArrayList list, OnActionCallback onActionCallback, ReturnMode mode) {
//        this.type = type;
//        db = DBManager.getInstance(mContext);
//        this.list = list;
//        this.onActionCallback = onActionCallback;
//        this.mode = mode;
//    }

    public DataBaseAsyncTask(ActionType type, Context mContext, Object object, OnActionCallback onActionCallback, ReturnMode mode) {
        this.type = type;
        db = DBManager.getInstance(mContext);
        this.object = object;
        this.onActionCallback = onActionCallback;
        this.mContext = mContext;
        this.mode = mode;
    }

    @Override
    protected String doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        try {
            doStuffs();
            success = true;
        }
        catch (Exception e) {
            // TODO: handle exception
            success = false;
            System.out.println(e.toString());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private void doStuffs(){
        switch (type) {
            case initFriendList :
                db.initFriendList(((ArrayList<User>)object));
                break;
            case updateFriendList :
                db.updateFriendList(((ArrayList<User>)object));
                break;
            case getFriendList:
                returnList = (ArrayList) db.getFriendsList();
                break;
            case insertMsg:
                db.insertMsg((XmppMessage) object);
                break;
            case getMsgHis:
                returnList = (ArrayList) db.getMsgList((User) object);
                break;
            case getLastMsgHis:
                returnList = (ArrayList) db.getChatHistory(object.toString());
                 break;
            case updateMsgReadStatus:
                 db.updateMsgReadStatus(object.toString());
                break;
            case insertSysMsg :
                db.insertSystemMsg((PresenceXmppMessage) object);
                break;
            case updateMsgStatus :
                db.updateMsgStatus((XmppMessage) object);
                break;
            default :
                break;
        }
    }

    private void returnCallbalk(){
        switch (mode) {
            case objectMode :
                onActionCallback.onActionCallback(success, returnObject);
                break;
            case listMode :
                onActionCallback.onActionCallback(success, returnList);
                break;

            default :
                break;
        }
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        returnCallbalk();
//        onActionCallback.onActionCallback(success, returnList);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
    }

}
