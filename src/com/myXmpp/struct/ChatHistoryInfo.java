package com.myXmpp.struct;

public class ChatHistoryInfo {

    private String chatFromUser;

    private int ChatHistoryType;

    private XmppMessage lastXmppMessage;

    public String getChatFromUser() {
        return chatFromUser;
    }

    public void setChatFromUser(String chatFromUser) {
        this.chatFromUser = chatFromUser;
    }

    public XmppMessage getLastXmppMessage() {
        return lastXmppMessage;
    }

    public void setLastXmppMessage(XmppMessage lastXmppMessage) {
        this.lastXmppMessage = lastXmppMessage;
        this.ChatHistoryType =lastXmppMessage.getMessageType();
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    private int unReadCount;


    public int getChatHistoryType() {
        return ChatHistoryType;
    }

    public void setChatHistoryType(int chatHistoryType) {
        ChatHistoryType = chatHistoryType;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChatHistoryInfo) {
            return chatFromUser.equals(((ChatHistoryInfo) o).getChatFromUser());
        }
        return false;
    }

}
