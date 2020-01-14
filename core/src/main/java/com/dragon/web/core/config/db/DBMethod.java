package com.dragon.web.core.config.db;

import com.dragon.web.db.util.DBConnection;
import com.dragon.web.db.util.DBMethodInf;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-07 12:04
 */
public class DBMethod implements DBMethodInf {
    @Override
    public String getSqlParamDAC(String param) {
        return null;
    }

    @Override
    public String getSqlParamLogin(String param) {
        return null;
    }

    @Override
    public String getSqlParamSys(String param) {
        return null;
    }

    @Override
    public String getTempTableNamePrefix() {
        return null;
    }

    @Override
    public void newDBConnection(DBConnection dbConnection) {

    }
}
