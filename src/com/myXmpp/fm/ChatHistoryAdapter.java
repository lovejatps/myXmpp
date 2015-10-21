package com.myXmpp.fm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.myXmpp.R;
import com.myXmpp.Utils.Util;
import com.myXmpp.struct.ChatHistoryInfo;
import com.myXmpp.struct.XmppMessage;

class ChatHistoryAdapter extends BaseAdapter {

    private List<ChatHistoryInfo> chatHistoryList = new ArrayList<ChatHistoryInfo>();

    private LayoutInflater inflater;
    private Context mContext;

    public ChatHistoryAdapter(List<ChatHistoryInfo> chatHistoryList, Context mContext) {
        this.chatHistoryList = chatHistoryList;
        inflater = LayoutInflater.from(mContext);
        this.mContext=mContext;
    }

    public List<ChatHistoryInfo> getChatHistoryList() {
        return chatHistoryList;
    }

    public void setChatHistoryList(List<ChatHistoryInfo> chatHistoryList) {
        this.chatHistoryList = chatHistoryList;
    }

    @Override
    public int getCount() {
        return chatHistoryList.size();
    }

    @Override
    public Object getItem(int i) {
        return chatHistoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewgroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.chat_history_list_adapter, null);
            holder = new ViewHolder();
            holder.chatHistoryFromUser = (TextView) view.findViewById(R.id.chatHistoryFromUser);
            holder.chatHistotyCount = (TextView) view.findViewById(R.id.chatHistotyCount);
            holder.chatHistoryLastMsg = (TextView) view.findViewById(R.id.chatHistoryLastMsg);
            holder.chatHistotyLastDate = (TextView) view.findViewById(R.id.chatHistotyLastDate);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }
        ChatHistoryInfo chatHistoryInfo = chatHistoryList.get(i);
        String from = chatHistoryInfo.getChatFromUser();
        if(from.indexOf("@")!=-1){
            holder.chatHistoryFromUser.setText(from.substring(0, from.indexOf("@")));
        }else{
            holder.chatHistoryFromUser.setText(from);
        }
        if (chatHistoryInfo.getUnReadCount() == 0) {
            holder.chatHistotyCount.setVisibility(View.INVISIBLE);
        }
        else {
            if(chatHistoryInfo.getUnReadCount()>99){
                holder.chatHistotyCount.setText("99+");
            }else{
            holder.chatHistotyCount.setText(chatHistoryInfo.getUnReadCount()+"");
            }
            holder.chatHistotyCount.setVisibility(View.VISIBLE);
        }
        int msgType = chatHistoryInfo.getLastXmppMessage().getMessageType();
        if(msgType==XmppMessage.MESSAGE_Type_SYSTEM_MSG){
            String msgBody = chatHistoryInfo.getLastXmppMessage().getMessageBody();
            holder.chatHistoryLastMsg.setText(msgBody.substring(0, msgBody.indexOf("_")));
        }else{
            holder.chatHistoryLastMsg.setText(chatHistoryInfo.getLastXmppMessage().getMessageBody());
        }
        holder.chatHistotyLastDate.setText(Util.getDayCount(chatHistoryInfo.getLastXmppMessage().getCreateTime(), mContext));
        return view;
    }
    class ViewHolder {
        TextView chatHistoryFromUser, chatHistotyCount, chatHistoryLastMsg, chatHistotyLastDate;
    }

}
