package com.myXmpp.fm;

import com.myXmpp.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MineFM extends BaseFragment {
    
    View mineView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mineView = inflater.inflate(R.layout.mine, null);
        return mineView;
    }
    
    @Override
    public void onUpdate() {
        // TODO Auto-generated method stub
        super.onUpdate();
    }

}
