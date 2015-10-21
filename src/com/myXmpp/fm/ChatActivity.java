package com.myXmpp.fm;

import com.myXmpp.R;
import com.myXmpp.struct.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ChatActivity extends FragmentActivity {
    
    ChatFM chatFM;
    public static String chattingUser = "";
    
    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.chat_activity_main);
        if(chatFM == null){
            Intent i = getIntent();
            chattingUser = ((User)i.getExtras().getSerializable("friend")).getUserName();
            chatFM = new ChatFM(i);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content, chatFM).commit();
//        if(i != null){
//            System.out.println("chattingUser   :  "  +  chattingUser);
//            chatFM.refreshChat(i);
//        }
    }
    
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        chattingUser = null;
        getSupportFragmentManager().beginTransaction().remove(chatFM).detach(chatFM);
        chatFM = null;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        chatFM.refreshChat();
    }

}
