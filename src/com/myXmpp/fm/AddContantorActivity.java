package com.myXmpp.fm;

import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.myXmpp.R;
import com.myXmpp.asyctask.PresenceAsyclTask;
import com.myXmpp.jaxmpp.JaxmppConnectManager;

public class AddContantorActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcontantorlayout);
        Button button = (Button) findViewById(R.id.addContantor_comfirm);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText editText = (EditText) AddContantorActivity.this.findViewById(R.id.addContantor_login_username);
                String userName = editText.getText().toString();
                try {
                    Presence p2 = Presence.create();
                    p2.setType(StanzaType.subscribe);
                    p2.setTo(JID.jidInstance(userName + "@" + JaxmppConnectManager.domain));
                    new PresenceAsyclTask(p2,PresenceAsyclTask.ActionType.sendPresence).execute("");
                }
                catch (Exception e) {
                }
                finish();
            }
        });
        Button cancleButton = (Button) findViewById(R.id.addContantor_cancle);
        cancleButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

}
