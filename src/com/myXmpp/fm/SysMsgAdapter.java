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
import com.myXmpp.struct.PresenceXmppMessage;
import com.myXmpp.struct.XmppMessage;

class SysMsgAdapter extends BaseAdapter {

    private List<XmppMessage> msgList = new ArrayList<XmppMessage>();

    SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    LayoutInflater inflater;

    public List<XmppMessage> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<XmppMessage> msgList) {
        this.msgList = msgList;
    }

    public SysMsgAdapter(Context mContext, List<XmppMessage> msgList) {
        inflater = LayoutInflater.from(mContext);
        this.msgList = msgList;
    }

    public void addMsg(PresenceXmppMessage rpresence) {
        msgList.add(rpresence);
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int i) {
        return msgList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewgroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.system_msg_list_adapter, null);
            holder = new ViewHolder();
            holder.systemMsgItem = (TextView) view.findViewById(R.id.systemMsgItem);
            holder.systemMsgItemInfo = (TextView) view.findViewById(R.id.systemMsgItemInfo);
            holder.systemMsgItemDate = (TextView) view.findViewById(R.id.systemMsgItemDate);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }
        XmppMessage xmppMessage = msgList.get(i);
        String msgBody = xmppMessage.getMessageBody();
        holder.systemMsgItem.setText(msgBody.split("_")[0]);

        if (xmppMessage.getMessageStatus() == 0) {
            holder.systemMsgItemInfo.setVisibility(View.INVISIBLE);
        }
        else if (xmppMessage.getMessageStatus() == 1) {
            holder.systemMsgItemInfo.setText("未处理");
        }
        else if (xmppMessage.getMessageStatus() == 2) {
            holder.systemMsgItemInfo.setText("已同意");
        }
        else if (xmppMessage.getMessageStatus() == 3) {
            holder.systemMsgItemInfo.setText("已拒绝");
        }
        holder.systemMsgItemDate.setText(dataFormat.format(new Date(xmppMessage.getCreateTime())));
        return view;
    }
    class ViewHolder {
        TextView systemMsgItem, systemMsgItemInfo, systemMsgItemDate;
    }

}
