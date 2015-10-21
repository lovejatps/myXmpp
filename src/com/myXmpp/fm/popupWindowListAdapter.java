package com.myXmpp.fm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.myXmpp.R;

public class popupWindowListAdapter extends BaseAdapter {

    List<String> items = new ArrayList<String>();
    LayoutInflater inflater;
    OnClickListener clickListenerAdd = null;

    public popupWindowListAdapter(LayoutInflater inflater, final Activity activity, final PopupWindow m_PopupWindow) {
        super();
        items.add("添加联系人");
        this.inflater = inflater;
        clickListenerAdd = new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(activity, AddContantorActivity.class);
                m_PopupWindow.dismiss();
                activity.startActivityForResult(intent, 1);
            }
        };
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.popupwindowlistt_adapter, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.popupItem);
        tv.setText(items.get(position));
        if (position == 0) {
            convertView.setOnClickListener(clickListenerAdd);
        }
        return convertView;
    }

}
