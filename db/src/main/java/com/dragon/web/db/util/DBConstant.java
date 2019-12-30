package com.dragon.web.db.util;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-30 11:07
 */
public class DBConstant {
    public static final String DEFAULT_DSNAME="core";
    // db类型枚举 todo 合并到DBTYPE枚举里
    public static final String DBTYPE_SYBASE="sybase";
    public static final String DBTYPE_MYSQL="mysql";
    public static final String DBTYPE_ORACLE="oracle";
    public static final String DBTYPE_SQLSERVER="sqlserver";
    // 必备连接参数
    public static final String JDBC_DRIVER="jdbc.driver";
    public static final String JDBC_URL="jdbc.url";
    public static final String JDBC_USERNAME="jdbc.username";
    public static final String JDBC_PWD="jdbc.password";
    // 初始化连接池参数
    public static final String CONNECTION_POOL_PARAM_MAXPOOLSIZE="maxPoolSize";
    public static final String CONNECTION_POOL_PARAM_MINPOOLSIZE="minPoolSize";
    public static final String CONNECTION_POOL_PARAM_INITPOOLSIZE="initPoolSize";
    public static final String CONNECTION_POOL_PARAM_WAITTIMEOUT="waitTimeout";






}
