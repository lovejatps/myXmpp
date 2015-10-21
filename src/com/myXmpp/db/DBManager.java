package com.myXmpp.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.myXmpp.Utils.Util;
import com.myXmpp.struct.ChatHistoryInfo;
import com.myXmpp.struct.PresenceXmppMessage;
import com.myXmpp.struct.User;
import com.myXmpp.struct.XmppMessage;

/**
 * SQLite数据库管理类
 *
 * 主要负责数据库资源的初始化,开启,关闭,以及获得DatabaseHelper帮助类操作
 *
 */
public class DBManager {

    // 本地Context对象
    private Context mContext = null;

    private static DBManager dBManager = null;
    private DatabaseHelper helper = null;
    private SQLiteDatabase db = null;

    /**
     * 构造函数
     *
     * @param mContext
     */
    private DBManager(Context mContext) {
        super();
        this.mContext = mContext;
//        if(helper == null){
//            helper = new DatabaseHelper(mContext);
//        }
//        if(db == null){
//            db = helper.getWritableDatabase();
//        }
    }

    public static DBManager getInstance(Context mContext) {
        if (null == dBManager) {
            dBManager = new DBManager(mContext);
        }
        return dBManager;
    }

    public void initFriendList(ArrayList<User> friendList) {
        openDatabase();
        db.beginTransaction();
        try {
            db.execSQL("delete from ett_chat_user");
            int size = friendList.size();
            for (int i = 0; i < size; i++) {
                ContentValues cv = new ContentValues();
                User friend = friendList.get(i);
                cv.put("user_id", friend.getUserId());
                cv.put("user_name", friend.getUserName());
                cv.put("nick_name", friend.getNickName());
                cv.put("password", friend.getPwd());
                cv.put("user_head_word", friend.getUserHeadWord());
                cv.put("status", friend.getStatus());
                cv.put("user_itemtype", friend.getUserItemType().name());
                db.insert("ett_chat_user", null, cv);
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            closeDatabase();
        }
    }

    public List<User> getFriendsList(){
        openDatabase();
        db.beginTransaction();
        Cursor cursor = null;
        List<User> friendList = new ArrayList<User>();
        User friend = null;
        try {
            cursor = db.rawQuery("select * from ett_chat_user where user_itemtype in(?,?)",new String[]{User.UserItemType.both.name(),User.UserItemType.to.name()});
            if(cursor != null){
                while (cursor.moveToNext()) {
                    friend = new User();
                    friend.setUserId(cursor.getString(cursor.getColumnIndex("user_name")));
                    friend.setNickName(cursor.getString(cursor.getColumnIndex("nick_name")));
                    friend.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
                    friend.setUserHeadWord(cursor.getString(cursor.getColumnIndex("user_head_word")));
                    friend.setNickName(cursor.getString(cursor.getColumnIndex("nick_name")));
                    friend.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                    friendList.add(friend);
                }
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            // TODO: handle exception
        }finally{
            db.endTransaction();
            if(cursor != null)
            cursor.close();
            closeDatabase();
        }
        return friendList;
    }


    public void insertMsg(XmppMessage msg){
        openDatabase();
        db.beginTransaction();
        try {
			Cursor inCur = db
					.rawQuery(
							"select count(*) from ett_chat_his where msg_time = ? and msg_to = ?",
							new String[] { msg.getCreateTime() + "",
									msg.getMessageTo() });
			inCur.moveToFirst();
			Long inCount = inCur.getLong(0);
			inCur.close();
			System.out.println("inCount   :   "  +  inCount);
			if (inCount == 1) {
				db.delete("ett_chat_his", "msg_time = ? and msg_to = ?",
						new String[] { msg.getCreateTime() + "" , msg.getMessageTo() });
			}
            ContentValues cv=new ContentValues();
            msg.setCreateTime(System.currentTimeMillis());
            cv.put("msg_from", msg.getMessageFrom());
            cv.put("msg_to", msg.getMessageTo());
            cv.put("msg_type", msg.getMessageType());
            cv.put("msg_status", msg.getMessageStatus());
            cv.put("content", msg.getMessageBody());
            cv.put("msg_time", msg.getCreateTime());
            cv.put("is_read", msg.getIsMsgRead());
            cv.put("is_send", msg.getMessageSend());
            db.insert("ett_chat_his",null,cv);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            // TODO: handle exception
            System.out.println("e   !!!!!  "  + e.toString());
        }finally{
            db.endTransaction();
            closeDatabase();
        }
    }

    public void updateMsgStatus(XmppMessage msg){
        openDatabase();
        db.beginTransaction();
        try {
            ContentValues cv=new ContentValues();
            cv.put("msg_status", msg.getMessageStatus());
            db.update("ett_chat_his", cv, "msg_time"+"="+msg.getCreateTime(), null);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            // TODO: handle exception
        }finally{
            db.endTransaction();
            closeDatabase();
        }
    }

    public List<XmppMessage> getMsgList(User from){
        openDatabase();
        List<XmppMessage> msgList = new ArrayList<XmppMessage>();
        db.beginTransaction();
        Cursor cursor = null;
        XmppMessage msg;
        try {
            db.setTransactionSuccessful();
            int fromIndex = (from.getPageNumber() - 1) * from.getPageSize();
            cursor =
                db.rawQuery(
                    "select * from ett_chat_his where (msg_from =? or msg_to=?) order by msg_time desc limit ? , ? ",
                    new String[] {from.getUserName(), from.getUserName(), ""+fromIndex, ""+from.getPageSize()+""});
            System.out.println("from.getUserName(),   "  +  from.getUserName());
            if(cursor != null){
                while(cursor.moveToNext()){
                    msg = new XmppMessage();
                    msg.setMessageBody(cursor.getString(cursor.getColumnIndex("content")));
                    msg.setMessageFrom(cursor.getString(cursor.getColumnIndex("msg_from")));
                    msg.setMessageTo(cursor.getString(cursor.getColumnIndex("msg_to")));
                    msg.setMessageStatus(cursor.getInt(cursor.getColumnIndex("msg_status")));
                    msg.setMessageType(cursor.getInt(cursor.getColumnIndex("msg_time")));
                    msg.setMessageSend(cursor.getInt(cursor.getColumnIndex("is_send")));
                    msg.setCreateTime(cursor.getLong(cursor.getColumnIndex("msg_time")));
                    msg.setIsMsgRead(cursor.getInt(cursor.getColumnIndex("is_read")));
                    msgList.add(msg);
                }
            }
            updateMsgReadStatus(from.getUserName());
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            // TODO: handle exception
        }finally{
            db.endTransaction();
            closeDatabase();
        }
        return msgList;
    }

    public List<ChatHistoryInfo> getChatHistory(String from) {
        List<ChatHistoryInfo> retList = new ArrayList<ChatHistoryInfo>();
        openDatabase();
        db.beginTransaction();
        List<String> chatUsers = new ArrayList<String>();
        Cursor cursor = null;
        XmppMessage msg;
        int unReadCount = 0;
        try {
            cursor = db.rawQuery("select msg_to from ett_chat_his where  msg_from = ?", new String[] { from});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String chatUser = cursor.getString(cursor.getColumnIndex("msg_to"));
                    if (!chatUsers.contains(chatUser)) {
                        chatUsers.add(chatUser);
                    }
                }
            }
            cursor = db.rawQuery("select msg_from from ett_chat_his where  msg_to = ?", new String[] { from});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String chatUser = cursor.getString(cursor.getColumnIndex("msg_from"));
                    if (!chatUsers.contains(chatUser)) {
                        chatUsers.add(chatUser);
                    }
                }
            }
            for (String chatUser : chatUsers) {
                msg = null;
                unReadCount = 0;
                cursor =
                    db.rawQuery(
                        "select * from ett_chat_his where ( msg_from =? or msg_to =?) and msg_time=(select max(msg_time) from ett_chat_his where msg_from =? or msg_to =?)",
                        new String[] { chatUser, chatUser, chatUser, chatUser });
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        msg = new XmppMessage();
                        msg.setMessageBody(cursor.getString(cursor.getColumnIndex("content")));
                        msg.setMessageFrom(cursor.getString(cursor.getColumnIndex("msg_from")));
                        msg.setMessageTo(cursor.getString(cursor.getColumnIndex("msg_to")));
                        msg.setMessageStatus(cursor.getInt(cursor.getColumnIndex("msg_status")));
                        msg.setMessageType(cursor.getInt(cursor.getColumnIndex("msg_type")));
                        msg.setMessageSend(cursor.getInt(cursor.getColumnIndex("is_send")));
                        msg.setCreateTime(cursor.getLong(cursor.getColumnIndex("msg_time")));
                        msg.setIsMsgRead(cursor.getInt(cursor.getColumnIndex("is_read")));
                    }
                }
                cursor =
                    db.rawQuery(
                        "select sum(is_read) as unread from ett_chat_his where ( msg_from = ? or msg_to = ? ) ",
                        new String[] { chatUser, chatUser });
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        unReadCount = cursor.getInt(cursor.getColumnIndex("unread"));
                    }
                }
                if (msg != null) {
                    ChatHistoryInfo ret = new ChatHistoryInfo();
                    ret.setLastXmppMessage(msg);
                    ret.setUnReadCount(unReadCount);
                    ret.setChatFromUser(chatUser);
                    retList.add(ret);
                }
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            // TODO: handle exception
        }
        finally {
            db.endTransaction();
            closeDatabase();
        }
        return retList;
    }
    public void updateMsgReadStatus(String from){
        openDatabase();
        db.beginTransaction();
        try {
            ContentValues cv=new ContentValues();
            cv.put("is_read", XmppMessage.IS_READ);
            db.update("ett_chat_his", cv, "msg_from = ? and msg_to=?", new String[]{from, Util.LOGIN_USER.getUserName()});
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
        }finally{
            db.endTransaction();
            closeDatabase();
        }
    }

    public void updateFriendList(ArrayList<User> friendList) {
        openDatabase();
        db.beginTransaction();
        try {
            for (User user : friendList) {
                if (user.getDbFlag() == User.DBFLAG_ADD) {
                    ContentValues cv = new ContentValues();
                    cv.put("user_id", user.getUserId());
                    cv.put("user_name", user.getUserName());
                    cv.put("nick_name", user.getNickName());
                    cv.put("password", user.getPwd());
                    cv.put("user_head_word", user.getUserHeadWord());
                    cv.put("status", user.getStatus());
                    cv.put("user_itemtype", user.getUserItemType().name());
                    db.insert("ett_chat_user", null, cv);
                }
                else if (user.getDbFlag() == User.DBFLAG_DELETE) {
                    db.delete("ett_chat_user", "user_id = ?", new String[] { user.getUserId() });
                }
                else if (user.getDbFlag() == User.DBFLAG_UPDATE) {
                    Cursor inCur =
                        db.rawQuery(
                            "select count(*) from ett_chat_user where user_id = ?",
                            new String[] { user.getUserId() });
                    inCur.moveToFirst();
                    Long inCount = inCur.getLong(0);
                    inCur.close();
                    if (inCount > 0) {
                        ContentValues cv = new ContentValues();
                        cv.put("user_id", user.getUserId());
                        cv.put("user_name", user.getUserName());
                        cv.put("nick_name", user.getNickName());
                        cv.put("password", user.getPwd());
                        cv.put("user_head_word", user.getUserHeadWord());
                        cv.put("status", user.getStatus());
                        cv.put("user_itemtype", user.getUserItemType().name());
                        db.update("ett_chat_user", cv, "user_id = ?", new String[] { user.getUserId() });
                    }
                    else {
                        ContentValues cv = new ContentValues();
                        cv.put("user_id", user.getUserId());
                        cv.put("user_name", user.getUserName());
                        cv.put("nick_name", user.getNickName());
                        cv.put("password", user.getPwd());
                        cv.put("user_head_word", user.getUserHeadWord());
                        cv.put("status", user.getStatus());
                        cv.put("user_itemtype", user.getUserItemType().name());
                        db.insert("ett_chat_user", null, cv);
                    }
                }
                db.setTransactionSuccessful();
            }
        }
        catch (Exception e) {
            // TODO: handle exception
        }
        finally {
            db.endTransaction();
            closeDatabase();
        }
    }

    public void insertSystemMsg(PresenceXmppMessage presenceXmppMessage) {
        openDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("msg_from", "系统消息");
            cv.put("msg_to", presenceXmppMessage.getMessageTo());
            cv.put("msg_type", presenceXmppMessage.getMessageType());
            cv.put("msg_status", presenceXmppMessage.getHandFlag());
            cv.put("content", presenceXmppMessage.toString() + "_" + presenceXmppMessage.getMessageFrom());
            cv.put("msg_time", presenceXmppMessage.getCreateTime());
            cv.put("is_read", presenceXmppMessage.getIsMsgRead());
            cv.put("is_send", presenceXmppMessage.getPresenceType());
            db.insert("ett_chat_his", null, cv);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            // TODO: handle exception
            System.out.println("e   !!!!!  " + e.toString());
        }
        finally {
            db.endTransaction();
            closeDatabase();
        }
    }
    /**
     * 关闭数据库 注意:当事务成功或者一次性操作完毕时候再关闭
     */
    private void closeDatabase() {
        if (null != helper) {
            helper.close();
            db.close();
            db = null;
            helper = null;
        }
    }

    void openDatabase(){
        if(helper == null){
            helper = new DatabaseHelper(mContext);
        }
        if(db == null){
            db = helper.getWritableDatabase();
        }
    }


}
