package com.myXmpp.fm;

import java.util.ArrayList;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.myXmpp.MainActivity;
import com.myXmpp.R;

public class ChatMainFm extends Fragment {

    View chatMainView;
    TextView scrollmenu_history, scrollmenu_communication, scrollmenu_mine;
    private HorizontalScrollView scrollmenu;
    private ImageView scrollmenu_left;
    private ImageView scrollmenu_right;

    private final static int SCROLLMENU_HISTORY = 0;
    private final static int SCROLLMENU_COMMUNICATION = 1;
    private final static int SCROLLMENU_MINE = 2;

    private int cur_view = -1;

    ViewPager chat_main_viewpager;
    ArrayList<BaseFragment> list = null;
    Button add_btn;

    PopupWindow m_PopupWindow=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        chatMainView = inflater.inflate(R.layout.chat_main, null);
        initView();
        initListener();
        setCurView(SCROLLMENU_HISTORY);

        //

        m_PopupWindow= new PopupWindow(250, LayoutParams.WRAP_CONTENT);
        View popupWindowListView = inflater.inflate(R.layout.popupwindow_list_main, null);
        ListView popupwindow_list = (ListView) popupWindowListView.findViewById(R.id.popupwindowList);
        popupwindow_list.setAdapter(new popupWindowListAdapter(inflater,getActivity(),m_PopupWindow));
        m_PopupWindow.setContentView(popupWindowListView);
        m_PopupWindow.setBackgroundDrawable(new BitmapDrawable());
        m_PopupWindow.setOutsideTouchable(true);
        m_PopupWindow.setFocusable(true);

        return chatMainView;
    }

    void initView() {
        scrollmenu_history = (TextView) chatMainView.findViewById(R.id.scrollmenu_history);
        scrollmenu_communication = (TextView) chatMainView.findViewById(R.id.scrollmenu_communication);
        scrollmenu_mine = (TextView) chatMainView.findViewById(R.id.scrollmenu_mine);
        scrollmenu = (HorizontalScrollView) chatMainView.findViewById(R.id.scrollmenu);
        scrollmenu_left = (ImageView) chatMainView.findViewById(R.id.scrollmenu_left);
        scrollmenu_right = (ImageView) chatMainView.findViewById(R.id.scrollmenu_right);
        chat_main_viewpager = (ViewPager) chatMainView.findViewById(R.id.chat_main_viewpager);
        add_btn = (Button) chatMainView.findViewById(R.id.add_btn);
        if (list == null || list.size() <= 0) {
            list = new ArrayList<BaseFragment>();
        }
        BaseFragment chatHistoryFM = new ChatHistoryFM();
        BaseFragment friendListFM = new FriendListFM();
        BaseFragment mineFM = new MineFM();
        list.add(chatHistoryFM);
        list.add(friendListFM);
        list.add(mineFM);
        ChatMainFMAdatper chatMainFMAdapter = new ChatMainFMAdatper(getChildFragmentManager(), list);
        chat_main_viewpager.setOffscreenPageLimit(4);
        chat_main_viewpager.setAdapter(chatMainFMAdapter);
        chat_main_viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case SCROLLMENU_HISTORY :
                    setCurView(SCROLLMENU_HISTORY);
                    break;
                case SCROLLMENU_COMMUNICATION :
                    setCurView(SCROLLMENU_COMMUNICATION);
                    break;
                case SCROLLMENU_MINE :
                    setCurView(SCROLLMENU_MINE);
                    break;
                default :
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    void initListener() {
        scrollmenu_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollmenu.arrowScroll(View.FOCUS_LEFT);
            }
        });
        scrollmenu_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollmenu.arrowScroll(View.FOCUS_RIGHT);
            }
        });

        scrollmenu_history.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setCurView(SCROLLMENU_HISTORY);
            }
        });

        scrollmenu_communication.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setCurView(SCROLLMENU_COMMUNICATION);
            }
        });

        scrollmenu_mine.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setCurView(SCROLLMENU_MINE);
            }
        });

        add_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                m_PopupWindow.showAsDropDown(v);

            }
        });
    }

    void initMenu() {
        scrollmenu_history.setBackgroundResource(R.drawable.company_info_menu_normal);
        scrollmenu_communication.setBackgroundResource(R.drawable.company_info_menu_normal);
        scrollmenu_mine.setBackgroundResource(R.drawable.company_info_menu_normal);
    }

    void setCurView(int viewIndex) {
        TextView iv = null;
        if (cur_view == viewIndex) {
            return;
        }
        else {
            cur_view = viewIndex;
        }
        initMenu();
        switch (viewIndex) {
            case SCROLLMENU_HISTORY :
                scrollmenu_history.setBackgroundResource(R.drawable.company_info_menu_selected);
                iv = scrollmenu_history;
                break;
            case SCROLLMENU_COMMUNICATION :
                scrollmenu_communication.setBackgroundResource(R.drawable.company_info_menu_selected);
                iv = scrollmenu_communication;
                break;
            case SCROLLMENU_MINE :
                scrollmenu_mine.setBackgroundResource(R.drawable.company_info_menu_selected);
                iv = scrollmenu_mine;
                break;
            default :
                break;
        }
        chat_main_viewpager.setCurrentItem(viewIndex);
        //adjust ScrollX to show the selected menu item
        if (iv == null || iv.getWidth() <= 0)
            return;
        int scrollBarRangeMin = scrollmenu.getLeft() + scrollmenu.getScrollX() - scrollmenu_left.getWidth();
        int scrollBarRangeMax = scrollmenu.getRight() + scrollmenu.getScrollX() - scrollmenu_left.getWidth();
        if (scrollBarRangeMin <= iv.getLeft() && iv.getRight() <= scrollBarRangeMax) { //in range
            return;
        }
        else if (iv.getLeft() < scrollBarRangeMin) { //need to scroll to left
            scrollmenu.scrollTo(iv.getLeft() - scrollBarRangeMin + scrollmenu.getScrollX(), 0);
        }
        else if (scrollBarRangeMax < iv.getRight()) { //need to scroll to right
            scrollmenu.scrollTo(iv.getRight() - scrollBarRangeMax + scrollmenu.getScrollX(), 0);
        }
    }

}
