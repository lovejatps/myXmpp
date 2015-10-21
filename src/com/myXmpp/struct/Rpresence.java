package com.myXmpp.struct;

import java.io.Serializable;
import java.util.Date;

public class Rpresence implements Serializable {

    private static final long serialVersionUID = 1L;
    public final static int PRESENCE_SUBSCRIBE = 1;
    public final static int PRESENCE_SUBSCRIBED = 2;
    public final static int PRESENCE_UNSUBSCRIBE = 3;
    public final static int PRESENCE_UNSUBSCRIBED = 4;
    public final static int PRESENCE_UNAVAILABLE = 5;
    public final static int PRESENCE_AVAILABLE = 6;

    private int presenceType;
    private String pfesenceFrom;
    private String pfesenceto;
    private Date msgDate;
    private int handFlag = 0;

    public int getPresenceType() {
        return presenceType;
    }

    public void setPresenceType(int presenceType) {
        this.presenceType = presenceType;
    }

    public String getPfesenceFrom() {
        return pfesenceFrom;
    }

    public void setPfesenceFrom(String pfesenceFrom) {
        this.pfesenceFrom = pfesenceFrom;
    }

    public String getPfesenceto() {
        return pfesenceto;
    }

    public void setPfesenceto(String pfesenceto) {
        this.pfesenceto = pfesenceto;
    }

    public Date getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(Date msgDate) {
        this.msgDate = msgDate;
    }

    public int getHandFlag() {
        return handFlag;
    }

    public void setHandFlag(int handFlag) {
        this.handFlag = handFlag;
    }

    @Override
    public String toString() {
        String ret = "";
        String fromStr = pfesenceFrom.substring(0, pfesenceFrom.indexOf("@"));
        if (presenceType == PRESENCE_SUBSCRIBE) {
            ret = "收到来自 " + fromStr + " 的好友请求";
        }
        if (presenceType == PRESENCE_SUBSCRIBED) {
            ret = fromStr + "同意你的添加好友请求";
        }
        if (presenceType == PRESENCE_UNSUBSCRIBE) {
            ret = fromStr + "拒绝你的添加好友请求";
        }
        if (presenceType == PRESENCE_UNSUBSCRIBED) {
            ret = fromStr + "删除了你";
        }
        return ret;
    }

}
