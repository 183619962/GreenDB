package com.lpf.greendb.db.helper;

import android.content.Context;

import com.lpf.greendb.db.MyApplication;
import com.lpf.greendb.db.bean.User;

import java.util.List;

import de.greenrobot.dao.query.WhereCondition;

/**
 */
public class DBHelper {
    private static DBHelper dbHelper;
    private DaoSession daoSession;
    private UserDao userDao;
    private static Context mContext;

    public DBHelper() {
    }

    public static DBHelper getinstance(Context context) {
        if (null == dbHelper) {
            dbHelper = new DBHelper();
            if (null != context) {
                mContext = context.getApplicationContext();
                dbHelper.daoSession = MyApplication.getDaoSession(mContext);
                dbHelper.userDao = dbHelper.daoSession.getUserDao();
            }
        }
        return dbHelper;
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public User loadUser(long id) {
        return userDao.loadByRowId(id);
    }

    /**
     * 获取所有
     *
     * @return
     */
    public List<User> loadAll() {
        return userDao.loadAll();
    }

    /**
     * 添加
     *
     * @param user
     * @return
     */
    public long saveUser(User user) {
        return userDao.insertOrReplace(user);
    }

    /**
     * 根据条件查询
     *
     * @param where
     * @param query
     * @return
     */
    public List<User> queryUser(WhereCondition where, WhereCondition... query) {
        if (null == query)
            return userDao.queryBuilder().where(where).build().list();
        else
            return userDao.queryBuilder().where(where, query).build().list();
    }

    /**
     * 添加多个
     *
     * @param users
     */
    public void saveUsers(final List<User> users) {
        if (null == users && users.size() == 0)
            return;
        userDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < users.size(); i++) {
                    userDao.insertOrReplace(users.get(i));
                }
            }
        });
    }

    /**
     * 删除
     *
     * @param user
     */
    public void delUser(User user) {
        userDao.delete(user);
    }
}
