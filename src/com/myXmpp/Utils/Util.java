package com.myXmpp.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sourceforge.pinyin4j.PinyinHelper;

import android.content.Context;

import com.myXmpp.R;
import com.myXmpp.struct.User;

public class Util {
    
    public static User LOGIN_USER = null;
    public final static String TODAY_KEY = "today";
    public final static String YESTODAY_KEY = "yestoday";
    public final static String TOW_DAY_AGO_KEY = "tow_day_ago";
    public static int screenWith;
    public static int screenheigh;
    
    
    public static String getPinYinHeadChar(String str) {  
        String convert = "";  
        for (int j = 0; j < str.length(); j++) {  
            char word = str.charAt(j);  
            // 提取汉字的首字母  
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);  
            if (pinyinArray != null) {  
                convert += pinyinArray[0].charAt(0);  
            } else {  
                convert += word;  
            }  
        }  
        return convert;  
    }
    
    public static String getDayCount(long createTime, Context c) {
        try {
            String ret = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            long create = sdf.parse(createTime).getTime();
            Calendar now = Calendar.getInstance();
            long ms =
                1000 * (now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND));//毫秒数
            long ms_now = now.getTimeInMillis();
//            Date d = sdf.parse(createTime);
//            SimpleDateFormat timeSDF = new SimpleDateFormat("HH:mm");
//            String time = timeSDF.format(d).toString();
            if (ms_now - createTime < ms) {
                ret = c.getResources().getString(R.string.today);
            }
            else if (ms_now - createTime < (ms + 24 * 3600 * 1000)) {
                ret = c.getResources().getString(R.string.yesterday);
            }
//            else if (ms_now - createTime < (ms + 24 * 3600 * 1000 * 2)) {
//                ret = TOW_DAY_AGO_KEY;
//            }
            else {
                ret = sdf.format(sdf.parse(new Date(createTime).toString())).toString();
            }
            return ret;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String preDate(long createTime){
    	try {
    		SimpleDateFormat timeSDF = new SimpleDateFormat("HH:mm");
    		String ret = "";
    		Date date = new Date(createTime);
    		ret = timeSDF.format(date);
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("e  :  " + e.toString());
		}
    	
    	return "";
    }
    
    public static String ToDBC(String input) {
    	   char[] c = input.toCharArray();
    	   for (int i = 0; i< c.length; i++) {
    	       if (c[i] == 12288) {
    	         c[i] = (char) 32;
    	         continue;
    	       }if (c[i]> 65280&& c[i]< 65375)
    	          c[i] = (char) (c[i] - 65248);
    	       }
    	   return new String(c);
    	}


}
