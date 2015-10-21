package com.myXmpp.fm;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ChatMainFMAdatper extends FragmentStatePagerAdapter {
	ArrayList<BaseFragment> list;

	public ChatMainFMAdatper(FragmentManager fm) {
		super(fm);
	}

	public ChatMainFMAdatper(FragmentManager fm,ArrayList<BaseFragment> list) {
		super(fm);
		this.list=list;
	}


	@Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		return list.size();
	}
	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}
}
