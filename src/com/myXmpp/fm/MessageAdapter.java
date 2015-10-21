package com.myXmpp.fm;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myXmpp.R;
import com.myXmpp.Utils.Util;
import com.myXmpp.struct.XmppMessage;
import com.myXmpp.view.KeyWordClickSpan;
import com.myXmpp.view.LinkText;
import com.myXmpp.view.KeyWordClickSpan.OnTextviewClickeListener;
import com.myXmpp.view.LinkText.OnKeyTextClickListener;
import com.myXmpp.view.StockTickWindow;

public class MessageAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<XmppMessage> messageList;
	private Context ctx;
	final static int VIEW_TYPE_FROM = 0;
	final static int VIEW_TYPE_TO = 1;
	StockTickWindow stockWindow = null;
	View contentView;
	Pattern keyPatter = Pattern.compile("#\\d{6}|#\\d{5}");
	ChatFM chatFM;

	private class FromViewHolder {
		public TextView nameTextView, recive_time, day_text;
		public TextView messageTextView;
	}

	// public void setMessageList(List<XmppMessage> messageList) {
	// this.messageList = messageList;
	// }

	private class SendViewHolder {
		public TextView nameTextView, send_time, day_text;
		public TextView messageTextView, resend;
		ProgressBar p;
	}

	public MessageAdapter(List<XmppMessage> messageList, Context ctx, View contentView, ChatFM chatFM) {
		this.messageList = messageList;
		this.ctx = ctx;
		this.mInflater = LayoutInflater.from(ctx);
		this.contentView = contentView; 
		this.chatFM = chatFM;
	}

	public int getCount() {
		return messageList.size();
	}

	public Object getItem(int position) {
		return messageList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void addData(List<XmppMessage> list) {
		messageList.addAll(0, list);
		notifyDataSetChanged();
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return messageList.get(position).isSend() ? VIEW_TYPE_TO
				: VIEW_TYPE_FROM;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		XmppMessage message = messageList.get(position);
		// int viewType = message.isSend() ? VIEW_TYPE_TO : VIEW_TYPE_FROM;
		switch (getItemViewType(position)) {
		case VIEW_TYPE_FROM:
			return getFromView(convertView, parent, message);
			// break;
		case VIEW_TYPE_TO:
			return getSendView(convertView, parent, message, position);
			// break;

		default:
			break;
		}

		return null;
	}

	private View getFromView(View convertView, ViewGroup parent,
			XmppMessage message) {
		FromViewHolder fromViewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.reciveview, null);
			fromViewHolder = new FromViewHolder();
			fromViewHolder.nameTextView = (TextView) convertView
					.findViewById(R.id.recive_name);
			fromViewHolder.messageTextView = (TextView) convertView
					.findViewById(R.id.recive_msg);
			fromViewHolder.recive_time = (TextView) convertView
					.findViewById(R.id.recive_time);
			fromViewHolder.day_text = (TextView) convertView
					.findViewById(R.id.day_text);
			convertView.setTag(fromViewHolder);
		} else {
			fromViewHolder = (FromViewHolder) convertView.getTag();
		}
		fromViewHolder.nameTextView.setText(message.getMessageFrom());
		fromViewHolder.messageTextView.setText(Util.ToDBC(message.getMessageBody()));
		fromViewHolder.recive_time
				.setText(Util.preDate(message.getCreateTime()));
		String key = Util.getDayCount(message.getCreateTime(), ctx);
		fromViewHolder.day_text.setText(key);
		SpannableString ss = new SpannableString(fromViewHolder.messageTextView.getText());
	    setKeywordClickable(fromViewHolder.messageTextView, ss, keyPatter);
		// int day = Util.getDayCont(message.getCreateTime());
		return convertView;
	}

	private View getSendView(View convertView, ViewGroup parent,
			final XmppMessage message, final int position) {
		SendViewHolder sendViewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sendview, null);
			sendViewHolder = new SendViewHolder();
			sendViewHolder.nameTextView = (TextView) convertView
					.findViewById(R.id.send_name);
			sendViewHolder.messageTextView = (TextView) convertView
					.findViewById(R.id.send_msg);
			sendViewHolder.p = (ProgressBar) convertView
					.findViewById(R.id.progress);
			sendViewHolder.send_time = (TextView) convertView
					.findViewById(R.id.send_time);
			sendViewHolder.day_text = (TextView) convertView
					.findViewById(R.id.day_text);
			sendViewHolder.resend = (TextView) convertView
			.findViewById(R.id.resend);
			convertView.setTag(sendViewHolder);
		} else {
			sendViewHolder = (SendViewHolder) convertView.getTag();
		}
		sendViewHolder.nameTextView.setText(message.getMessageFrom());
		sendViewHolder.messageTextView.setText(Util.ToDBC(message.getMessageBody()));
		sendViewHolder.send_time.setText(Util.preDate(message.getCreateTime()));
		String key = Util.getDayCount(message.getCreateTime(), ctx);
		sendViewHolder.day_text.setText(key);
		if (message.getMessageStatus() == XmppMessage.STATUS_PENDING) {
			sendViewHolder.p.setVisibility(View.VISIBLE);
			sendViewHolder.resend.setVisibility(View.GONE);
		} else if (message.getMessageStatus() == XmppMessage.STATUS_ERROR) {
			sendViewHolder.p.setVisibility(View.GONE);
			sendViewHolder.resend.setVisibility(View.VISIBLE);
		} else{
			sendViewHolder.p.setVisibility(View.GONE);
			sendViewHolder.resend.setVisibility(View.GONE);
		}
		SpannableString ss = new SpannableString(sendViewHolder.messageTextView.getText());
	    setKeywordClickable(sendViewHolder.messageTextView, ss, keyPatter);
	    sendViewHolder.resend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				chatFM.reSendMessage(message, position);
			}
		});
		return convertView;
	}
	
	void showTicket(View v, String stock){
		if(stockWindow == null){
			stockWindow = new StockTickWindow(ctx, v);
		}
		stockWindow.showStockWindow(stock);
	}
	
	
	private void setKeywordClickable(TextView textview, SpannableString ss , Pattern pattern){
        Matcher matcher = pattern.matcher(ss.toString());
        while(matcher.find()){
            String key = matcher.group();
            if(!"".equals(key)){
                int start = ss.toString().indexOf(key);
                int end = start + key.length();
                setClickText(textview, ss, start, end, new KeyWordClickSpan(new OnTextviewClickeListener() {
                    
                    @Override
                    public void clickTextView(String keyWord) {
                        // TODO Auto-generated method stub
                    	showTicket(contentView, keyWord);
                    }

					@Override
					public void setSlyle(TextPaint ds) {
						// TODO Auto-generated method stub
						 ds.setColor(Color.RED);
	                        ds.setLinearText(false);
					}
                }));
            }
        }
        
    }
    
    private void setClickText(TextView textview, SpannableString ss , int start, int end, KeyWordClickSpan cs){
        ss.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview.setText(ss);
        textview.setMovementMethod(new LinkMovementMethod());
        cs.setKeyWord(textview.getText().toString().substring(start + 1, end));
    }
}
