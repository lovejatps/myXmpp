package com.myXmpp.db;

import java.io.File;

import com.myXmpp.Utils.Util;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    // 用户数据库文件的版本
    private static final int DB_VERSION = 1;
//    private SQLiteDatabase myDataBase = null;
    private Context myContext;
    private static String DB_NAME = "ett_chat.db";
    public static final String PACKAGE_NAME = "com.myXmpp";
//    private static String DB_BASE_PATH = "/sdcard/ett/chat/";
    public static String DB_BASE_PATH = "/data"
        + Environment.getDataDirectory().getAbsolutePath() + "/"
        + PACKAGE_NAME + "/";
    private static String DB_PATH = "";
    private static final String CREATE_USER_TABLE =
        "CREATE TABLE IF NOT EXISTS [ett_chat_user] ([user_id] NVARCHAR NOT NULL PRIMARY KEY, [user_name] NVARCHAR, [nick_name] NVARCHAR, [password] NVARCHAR, [user_head_word] NVARCHAR, [status] INTEGER, [user_itemtype] NVARCHAR)";
    private static final String CREATE_CHAT_HIS_TABLE =
        "CREATE TABLE [ett_chat_his] ([content] NVARCHAR, [msg_from] NVARCHAR, [msg_to] NVARCHAR, [msg_time] LONG, [msg_type] INTEGER, [msg_status] INTEGER, [is_send] INTEGER, [is_read] INTEGER)";

    final static String TAG = "DatabaseHelper";

    // 构造函数，调用父类SQLiteOpenHelper的构造函数
    public DatabaseHelper(
        Context context,
        String name,
        CursorFactory factory,
        int version,
        DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);

    }

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        // SQLiteOpenHelper的构造函数参数：
        // context：上下文环境
        // name：数据库名字
        // factory：游标工厂（可选）
        // version：数据库模型版本号
    }

    public DatabaseHelper(Context context) {
        super(context, DB_BASE_PATH + Util.LOGIN_USER.getUserName() + "/" + DB_NAME, null, DB_VERSION);
        this.myContext = context;
        // 数据库实际被创建是在getWritableDatabase()或getReadableDatabase()方法调用时
        DB_PATH = DB_BASE_PATH + Util.LOGIN_USER.getUserName() + "/";
        System.out.println("DB_PATH     :   "  +  DB_PATH);
        Log.d(TAG, "DatabaseHelper Constructor");
        // CursorFactory设置为null,使用系统默认的工厂类
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        // TODO Auto-generated method stub
        return super.getWritableDatabase();
    }

    // 继承SQLiteOpenHelper类,必须要覆写的三个方法：onCreate(),onUpgrade(),onOpen()

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 调用时间：数据库第一次创建时onCreate()方法会被调用

        // onCreate方法有一个 SQLiteDatabase对象作为参数，根据需要对这个对象填充表和初始化数据
        // 这个方法中主要完成创建数据库后对数据库的操作
        try {
            if(!checkDataBase()){
                File dir = new File(DB_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
            db.execSQL(CREATE_CHAT_HIS_TABLE);
            db.execSQL(CREATE_USER_TABLE);
        }
        catch (Exception e) {
            // TODO: handle exception
            System.out.println("e    :   "  + e.getMessage());
        }
        Log.d(TAG, "DatabaseHelper onCreate???????????????????????");

    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        String myPath = DB_PATH + DB_NAME;
        try {
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // database does't exist yet.
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 调用时间：如果DATABASE_VERSION值被改为别的数,系统发现现有数据库版本不同,即会调用onUpgrade

        // onUpgrade方法的三个参数，一个 SQLiteDatabase对象，一个旧的版本号和一个新的版本号
        // 这样就可以把一个数据库从旧的模型转变到新的模型
        // 这个方法中主要完成更改数据库版本的操作

        //        Log.d(AppConstants.LOG_TAG, "DatabaseHelper onUpgrade");
        //
        //        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //        onCreate(db);
        // 上述做法简单来说就是，通过检查常量值来决定如何，升级时删除旧表，然后调用onCreate来创建新表
        // 一般在实际项目中是不能这么做的，正确的做法是在更新数据表结构时，还要考虑用户存放于数据库中的数据不丢失

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // 每次打开数据库之后首先被执行

        Log.d(TAG, "DatabaseHelper onOpen");
    }

}
