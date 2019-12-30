package com.dragon.web.db.util;

import java.util.Properties;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-28 15:43
 */
public abstract class DBConnectionFactory {
    protected DBType dbType;
    protected String dsName;

    public DBConnectionFactory() {
        this.dbType=DBType.oracle;
        this.dsName="";
    }

    public DBType getDbType() {
        return dbType;
    }

    public void setDbType(DBType dbType) {
        this.dbType = dbType;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    /**
     * 初始化DBConnectionFactory
     * @param pro
     * @param dsName
     */
    public abstract void init(Properties pro, String dsName);

    /**
     * 获取DBConnection
     * @return
     */
    public abstract DBConnection getDBConnection();
    public void resetPool(){

    }
}
