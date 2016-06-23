package com.lpf.greendb.db;

import android.app.Application;
import android.content.Context;

import com.lpf.greendb.db.helper.DaoMaster;
import com.lpf.greendb.db.helper.DaoSession;

public class MyApplication extends Application {
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 得到DaoMaster对象
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context) {
        if (null == daoMaster) {
            //第一个参数，上下文对象，第二个参数库名
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, "GREENDB", null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 得到DaoSession对象
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
