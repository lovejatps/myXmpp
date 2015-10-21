package com.myXmpp.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myXmpp.view.KeyWordClickSpan.OnTextviewClickeListener;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;

public class LinkText extends TextView {
    
    Pattern keyPatter = Pattern.compile("#\\d{6}|#\\d{5}");
    SpannableString ss = null;
    
    public interface OnKeyTextClickListener{
        
        public void onKeyTextClick(String keyText);
    }
    public OnKeyTextClickListener onKeyTextClickListener;
    
    public void setOnKeyTextClickListener(OnKeyTextClickListener onKeyTextClickListener) {
        this.onKeyTextClickListener = onKeyTextClickListener;
    }
    public LinkText(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public LinkText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public LinkText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
    
    public void setText(String text){
        super.setText(text);
//        if(ss == null){
            ss = new SpannableString(text);
//        }
        setKeywordClickable(this, ss, keyPatter);
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
                    public void setSlyle(TextPaint ds) {
                        // TODO Auto-generated method stub
                        ds.setColor(Color.RED);
                        ds.setLinearText(true);
                    }
                    
                    @Override
                    public void clickTextView(String keyWord) {
                        // TODO Auto-generated method stub
                        if(onKeyTextClickListener != null){
                            onKeyTextClickListener.onKeyTextClick(keyWord);
                        }
                    }
                }));
            }
        }
        
    }
    
    private void setClickText(TextView textview, SpannableString ss , int start, int end, KeyWordClickSpan cs){
        ss.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview.setText(ss);
        textview.setMovementMethod(LinkMovementMethod.getInstance());
        cs.setKeyWord(textview.getText().toString().substring(start + 1, end));
    }

}
