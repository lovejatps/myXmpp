package com.myXmpp.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.myXmpp.R;
import com.myXmpp.Utils.Util;

public class StockTickWindow {
	Context ctx;
	LayoutInflater inflater;
	String stock;
	PopupWindow p;
	View stockTicketView;
	View v;
	TextView close_btn, stock_code_text;
	
	public StockTickWindow(Context ctx, View v) {
		// TODO Auto-generated constructor stub
		this.ctx =ctx;
		inflater = LayoutInflater.from(ctx);
		this.v = v;
		initView();
		initListener();
	}
	
	void initView(){
		stockTicketView = inflater.inflate(R.layout.stock_ticket_layout, null);
		close_btn = (TextView) stockTicketView.findViewById(R.id.close_btn);
		stock_code_text = (TextView) stockTicketView.findViewById(R.id.stock_code_text);
	}
	
	void initListener(){
		close_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dissmissWindow();
			}
		});
	}
	
	
	public void showStockWindow(String stock){
		this.stock = stock;
		if(p == null){
			p  =
                    new PopupWindow();
		}
		p.setContentView(stockTicketView);
		p.setWidth(LayoutParams.MATCH_PARENT);
		p.setHeight(LayoutParams.MATCH_PARENT);
		p.setFocusable(true);
		p.setAnimationStyle(R.style.AnimFade);
		stock_code_text.setText(stock);
		if(!p.isShowing()){
			p.showAtLocation(v, Gravity.CENTER, 0, 0);
		}
	}
	
	public void dissmissWindow(){
		if(p != null && p.isShowing()){
			p.dismiss();
			p = null;
		}
	}

}
