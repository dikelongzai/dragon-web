package com.dragon.web.db.util;

import java.util.Map;

/**
 * 数据源
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-28 15:43
 */
public class DataSource {
    public static final String CORE_DATASOURCE = "core";
    public static final DBType DEFAULT_DB_TYPE;
    private static Map<String, DBConnectionFactory> ds;
    protected static Map<String, DBConnection> usingDBConnection;
    private static DBMethodInf dbm;
}
