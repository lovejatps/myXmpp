package com.myXmpp.struct;

public class PresenceXmppMessage extends XmppMessage {

    private static final long serialVersionUID = 1L;
    public final static int PRESENCE_SUBSCRIBE = 1;
    public final static int PRESENCE_SUBSCRIBED = 2;
    public final static int PRESENCE_UNSUBSCRIBE = 3;
    public final static int PRESENCE_UNSUBSCRIBED = 4;
    public final static int PRESENCE_UNAVAILABLE = 5;
    public final static int PRESENCE_AVAILABLE = 6;

    private int presenceType;

    private int handFlag = 0;

    public int getPresenceType() {
        return presenceType;
    }

    public void setPresenceType(int presenceType) {
        this.presenceType = presenceType;
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
        String fromStr = getMessageFrom().substring(0, getMessageFrom().indexOf("@"));
        if (presenceType == PRESENCE_SUBSCRIBE) {
            ret = fromStr + "请求加你为好友";
        }
        if (presenceType == PRESENCE_SUBSCRIBED) {
            ret = fromStr + "同意你的好友请求";
        }
        if (presenceType == PRESENCE_UNSUBSCRIBE) {
            ret = fromStr + "拒绝你的好友请求";
        }
        if (presenceType == PRESENCE_UNSUBSCRIBED) {
            ret = fromStr + "删除了你";
        }
        return ret;
    }

}
