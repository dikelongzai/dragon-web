package com.dragon.web.db.util;

import com.dragon.web.common.security.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源
 *
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-28 15:43
 */
public class DataSource {
    public static final String CORE_DATASOURCE = "core";
    public static final DBType DEFAULT_DB_TYPE;
    private static Map<String, DBConnectionFactory> ds;
    protected static Map<String, DBConnection> usingDBConnection;
    private static DBMethodInf dbm;

    static {
        DEFAULT_DB_TYPE = DBType.oracle;
        ds = new HashMap<>();
        usingDBConnection = new HashMap<>();
        dbm = null;
    }


    public static DBMethodInf getDbm() {
        return dbm;
    }

    public static void setDbm(DBMethodInf dbMethodInf) {
        dbm = dbMethodInf;
    }

    public static Map<String, DBConnection> getUsingDBConnection() {
        return usingDBConnection;
    }

    /**
     * 添加 DataSource factory
     *
     * @param dsName 数据源标识
     * @param f
     */
    public static void addDBConnectionFactory(String dsName, DBConnectionFactory f) {
        ds.put(dsName, f);

    }

    /**
     * <p>
     * 根据dsName 获取连接
     * 先获取连接池 再获取连接 把连接加入正在使用连接
     *
     * </p>
     *
     * @param dsName
     * @return
     */
    public static DBConnection getDBConnection(String dsName) {
        DBConnectionFactory f = ds.get(dsName);
        if (f == null) {
            Log.exception("no datasource called " + dsName);
            return null;
        } else {
            DBConnection dbc = f.getDBConnection();
            if (dbm != null) {
                dbm.newDBConnection(dbc);
            }
            synchronized (usingDBConnection) {
                usingDBConnection.put(dbc.getUid(), dbc);
                return dbc;
            }
        }


    }

    /**
     * 释放连接
     *
     * @param dbConnection
     */
    public static void removeUsingDBConnection(DBConnection dbConnection) {
        synchronized (usingDBConnection) {
            usingDBConnection.remove(dbConnection);
        }

    }

    /**
     * 重置连接池
     *
     * @param dsName
     */
    public static void resetPool(String dsName) {
        ds.get(dsName).resetPool();
    }
}
