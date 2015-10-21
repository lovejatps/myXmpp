package com.myXmpp.asyctask;

import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;
import android.os.AsyncTask;

import com.myXmpp.jaxmpp.JaxmppConnectManager;

public class PresenceAsyclTask extends AsyncTask<String, Integer, String> {

    ActionType type;
    Object object;
    public enum ActionType {
        sendPresence,
        removeRosterItem,
    }


    public PresenceAsyclTask(Object object,ActionType type) {
        this.object = object;
        this.type=type;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            doStuffs();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {

        }
        return null;
    }
    @SuppressWarnings("unchecked")
    private void doStuffs() throws Exception{
        switch (type) {
            case sendPresence :
                JaxmppConnectManager.getInstance().getJaxmpp().send(((Presence)object));
                break;
            case removeRosterItem :
                JaxmppConnectManager.getJaxmpp().getRoster().remove(BareJID.bareJIDInstance((String)object));
                break;

            default :
                break;
        }
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        System.out.println("onPostExecute");
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        System.out.println("onProgressUpdate");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("onPreExecute");
    }

}
