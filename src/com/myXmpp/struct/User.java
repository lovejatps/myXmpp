package com.myXmpp.struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.stanzas.Message;

public class User implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String userName;
    private String pwd = "";
    private String nickName;
    private int status = 0;
    private String userHeadWord;
    private String userId;
    private UserItemType userItemType;
    private int dbFlag;
    public static final int DBFLAG_ADD=1;//增加
    public static final int DBFLAG_UPDATE=2;//更新
    public static final int DBFLAG_DELETE=3;//删除

    //用于分页查询
    private int pageNumber = 1;
    private int pageSize = 10;

    private List<Message> messageList = new ArrayList<Message>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserHeadWord() {
        return userHeadWord;
    }

    public void setUserHeadWord(String userHeadWord) {
        this.userHeadWord = userHeadWord;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}


    public UserItemType getUserItemType() {
        return userItemType;
    }

    public void setUserItemType(UserItemType userItemType) {
        this.userItemType = userItemType;
    }

    public int getDbFlag() {
        return dbFlag;
    }

    public void setDbFlag(int dbFlag) {
        this.dbFlag = dbFlag;
    }

    public void setUserItemType(RosterItem.Subscription itemType) {
        if (itemType.equals(RosterItem.Subscription.both)) {
            this.userItemType = UserItemType.both;
        }
        else if (itemType.equals(RosterItem.Subscription.from)) {
            this.userItemType = UserItemType.from;
        }
        else if (itemType.equals(RosterItem.Subscription.to)) {
            this.userItemType = UserItemType.to;
        }
        else if (itemType.equals(RosterItem.Subscription.none)) {
            this.userItemType = UserItemType.none;
        }
        else if (itemType.equals(RosterItem.Subscription.remove)) {
            this.userItemType = UserItemType.remove;
        }
    }

    public enum UserItemType {
        none("none"),
        to("to"),
        from("from"),
        both("both"),
        remove("remove");
        String typeName;

        private UserItemType(String typeName) {
            this.typeName = typeName;
        }
    }
}
