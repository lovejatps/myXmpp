package com.myXmpp.fm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.myXmpp.R;

public class SystemMsgActivity extends FragmentActivity {

    SystemMsgFM systemMsgFM;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.systemmsg_activity_main);
        if(systemMsgFM == null){
            systemMsgFM = new SystemMsgFM(getIntent());
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content, systemMsgFM).commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().beginTransaction().remove(systemMsgFM).detach(systemMsgFM);
        systemMsgFM = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
    }

}
