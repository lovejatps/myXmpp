package com.myXmpp.fm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.myXmpp.MainActivity;
import com.myXmpp.R;
import com.myXmpp.Utils.Util;
import com.myXmpp.jaxmpp.JaxmppConnectManager;
import com.myXmpp.struct.User;
import com.myXmpp.view.KeyWordClickSpan;
import com.myXmpp.view.KeyWordClickSpan.OnTextviewClickeListener;

public class LoginFM extends Fragment {

    View loginView;
    EditText login_username, login_psw;
    Button login_login;
//    public static String USER_NAME = "Clarence";
//    public static String PWD = "123456";
    TextView text_link;
    Pattern keyPatter = Pattern.compile("#\\d+");
    String s = "#00011";
    SharedPreferences spf = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        loginView = inflater.inflate(R.layout.login, null);
        initView();
        initListener();
        return loginView;
    }

    void initView(){
        login_username = (EditText) loginView.findViewById(R.id.login_username);
        login_psw = (EditText) loginView.findViewById(R.id.login_psw);
        login_login = (Button) loginView.findViewById(R.id.login_login);
        spf = getActivity().getSharedPreferences(JaxmppConnectManager.USER_SPF, 0);
        if(spf != null){
            if(Util.LOGIN_USER == null){
                Util.LOGIN_USER = new User();
            }
            Util.LOGIN_USER.setUserName(spf.getString(JaxmppConnectManager.USER_NAME, null));
            Util.LOGIN_USER.setPwd(spf.getString(JaxmppConnectManager.PWD, null));
        }
        if(Util.LOGIN_USER != null){
            login_username.setText(Util.LOGIN_USER.getUserName());
            login_psw.setText(Util.LOGIN_USER.getPwd());
        }
        text_link = (TextView) loginView.findViewById(R.id.text_link);
        text_link.setText(s);
    }

    void initListener(){
        login_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //关闭软键盘
                InputMethodManager imm =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(
                        getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                }
                ((MainActivity)getActivity()).login(login_username.getText().toString(), login_psw.getText().toString());
            }
        });
        SpannableString ss = new SpannableString(text_link.getText());
        setKeywordClickable(text_link, ss, keyPatter);
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
                        System.out.println("keyWord   :  "  +   keyWord);
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
