package com.dragon.web.db.util;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-28 15:44
 */
public interface DBMethodInf {
    String getSqlParamDAC(String param);

    String getSqlParamLogin(String param);

    String getSqlParamSys(String param);

    String getTempTableNamePrefix();

    void newDBConnection(DBConnection dbConnection);
}
