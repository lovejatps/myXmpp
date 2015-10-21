package com.myXmpp.view;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

public class KeyWordClickSpan extends ClickableSpan {
    
    
    private String keyWord = "";
    public interface OnTextviewClickeListener{
        public void clickTextView(String keyWord);
        public void setSlyle(TextPaint ds);
    }
    
    public String getKeyWord() {
        return keyWord;
    }
    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    private OnTextviewClickeListener mListener;

    public KeyWordClickSpan(OnTextviewClickeListener listener) {
        // TODO Auto-generated constructor stub
        this.mListener = listener;
    }
    @Override
    public void onClick(View arg0) {
        mListener.clickTextView(keyWord);
    }
    
    @Override
    public void updateDrawState(TextPaint ds) {
        // TODO Auto-generated method stub
        mListener.setSlyle(ds);
    }

}
