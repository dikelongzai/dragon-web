package com.dragon.web.core;

import bsh.Interpreter;
import com.dragon.web.common.vo.ThreadObject;
import com.dragon.web.common.vo.User;
import com.dragon.web.db.util.DBConnection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * ResourcePool
 *
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-02 16:26
 */
public class ResourcePool {
    /**
     * 使用中的连接
     */
    private static ThreadLocal<List<DBConnection>> openedDBConnection = new ThreadLocal();
    private static ThreadLocal<DBConnection> currentDBConnection = new ThreadLocal();
    private static ThreadLocal<Interpreter> beanShell = new ThreadLocal();
    private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    private static ThreadLocal<ThreadObject> currentThreadObject = new ThreadLocal();
    private static ThreadLocal<User> currentUser = new ThreadLocal();
    public static ThreadObject getCurrentThreadObject() {
        ThreadObject u = (ThreadObject) currentThreadObject.get();
        if (u == null) {
            u = new ThreadObject();
            setCurrentThreadObject(u);
        }

        return u;
    }

    public static void setCurrentDBConnection(DBConnection o) {
        currentDBConnection.set(o);
    }

    public static void setCurrentThreadObject(ThreadObject threadObject) {
        currentThreadObject.set(threadObject);
    }

    public static void addOpenDBConnection(DBConnection dbc) {
        List<DBConnection> list = openedDBConnection.get();
        if (list == null) {
            list = new ArrayList<>();
            openedDBConnection.set(list);
        }
        list.add(dbc);

    }

    /**
     * 关闭连接
     *
     * @param fromMillis
     */
    public static void closeAllOpenedDBConnection(long fromMillis) {
        List<DBConnection> lst = (List) openedDBConnection.get();
        if (lst != null) {
            Iterator<DBConnection> itr = lst.iterator();
            while (itr.hasNext()) {
                DBConnection dbConnection = itr.next();
                if (dbConnection == null) {
                    lst.remove(dbConnection);
                    //超时连接
                } else if (dbConnection.getCreateTime().getTime() >= fromMillis) {
                    dbConnection.close();
                    lst.remove(dbConnection);
                }
            }
        }


    }

    /**
     * 获取使用中连接
     *
     * @return
     */
    public static DBConnection getCurrentDBConnection() {
        return currentDBConnection.get();
    }

    public static Interpreter getCurrentBeanShell() {
        Interpreter u = (Interpreter) beanShell.get();
        if (u == null) {
            u = new Interpreter();
            setCurrentBeanShell(u);
        }

        return u;
    }

    public static void setCurrentBeanShell(Interpreter o) {
        beanShell.set(o);
    }

    public static ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {
        return scheduledThreadPoolExecutor;
    }

    public static void removeAllThreadLocal() {
        openedDBConnection.remove();
        currentDBConnection.remove();
        beanShell.remove();
    }

    public static ThreadLocal<User> getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(ThreadLocal<User> currentUser) {
        ResourcePool.currentUser = currentUser;
    }
}
