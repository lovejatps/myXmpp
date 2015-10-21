package com.myXmpp.fm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myXmpp.R;
import com.myXmpp.struct.User;
import com.myXmpp.struct.XmppMessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FriendAdapter extends BaseAdapter {

    List<User> friendList;
    Context mContext;
    LayoutInflater inflater;
//    Map<String, ArrayList<XmppMessage>> friendMessgaeList = null;

    public FriendAdapter(List<User> friendList, Context mContext) {
        // TODO Auto-generated constructor stub
        this.friendList = friendList;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
//        if(friendMessgaeList == null){
//            friendMessgaeList = new HashMap<String, ArrayList<XmppMessage>>();
//        }
    }

//    public Map<String, ArrayList<XmppMessage>> getFriendMessgaeList() {
//        return friendMessgaeList;
//    }
//
//
//    public void setFriendMessgaeList(Map<String, ArrayList<XmppMessage>> friendMessgaeList) {
//        this.friendMessgaeList = friendMessgaeList;
//    }


    public List<User> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return friendList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.friend_list_adapter, null);
            holder = new ViewHolder();
            holder.message_count = (TextView) convertView.findViewById(R.id.message_count);
            holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        String fromUserName = friendList.get(position).getNickName();
        holder.user_name.setText(fromUserName);
//        if(friendMessgaeList != null && friendMessgaeList.containsKey(fromUserName) && friendMessgaeList.get(fromUserName).size() > 0){
//            holder.message_count.setVisibility(View.VISIBLE);
//            holder.message_count.setText(friendMessgaeList.get(fromUserName).size() + "");
//        }else{
//            holder.message_count.setText("0");
//            holder.message_count.setVisibility(View.INVISIBLE);
//        }
        return convertView;
    }

    class ViewHolder {
        TextView user_name, message_count;
    }

}
