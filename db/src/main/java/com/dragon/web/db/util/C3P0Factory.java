package com.dragon.web.db.util;

import com.dragon.web.common.security.Log;
import com.dragon.web.common.util.StringUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

import java.util.Properties;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-30 11:00
 */
public class C3P0Factory extends DBConnectionFactory {
    private ComboPooledDataSource dataSource = null;

    @Override
    public void init(Properties props, String dsName) {
        this.dsName = dsName;
        if (this.dataSource != null) {
            try {
                DataSources.destroy(this.dataSource);
            } catch (Exception e) {
                Log.exception("C3P0Factory init fail msg " + e.getMessage());
            }
            this.dataSource = null;

        }
        this.dataSource = new ComboPooledDataSource();
        this.dataSource.setAcquireIncrement(1);
        String driver = props.getProperty(DBConstant.JDBC_DRIVER);
        String url = props.getProperty(DBConstant.JDBC_URL);
        String userName = props.getProperty(DBConstant.JDBC_USERNAME);
        String pwd = props.getProperty(DBConstant.JDBC_PWD);
        try {
            //load jdbc driver class
            Thread.currentThread().getContextClassLoader().loadClass(driver);
            this.dataSource.setDriverClass(driver);
            if (driver.toLowerCase().indexOf(DBConstant.DBTYPE_SYBASE) >= 0) {
                this.dbType = DBType.sybase;
            } else if (driver.toLowerCase().indexOf(DBConstant.DBTYPE_MYSQL) >= 0) {
                this.dbType = DBType.mysql;
            } else if (driver.toLowerCase().indexOf(DBConstant.DBTYPE_SQLSERVER) >= 0) {
                this.dbType = DBType.sqlserver;
            }
        } catch (Exception e) {
            Log.exception(e);
        }
        this.dataSource.setJdbcUrl(url);
        if (!StringUtil.isEmpty(userName)) {
            this.dataSource.setUser(userName);
        }

        if (!StringUtil.isEmpty(pwd)) {
            this.dataSource.setPassword(pwd);
        }
        this.dataSource.setMaxPoolSize(StringUtil.parseInt(props.getProperty(DBConstant.CONNECTION_POOL_PARAM_MAXPOOLSIZE), 50));
        this.dataSource.setMinPoolSize(StringUtil.parseInt(props.getProperty(DBConstant.CONNECTION_POOL_PARAM_MINPOOLSIZE), 1));
        this.dataSource.setInitialPoolSize(StringUtil.parseInt(props.getProperty(DBConstant.CONNECTION_POOL_PARAM_INITPOOLSIZE), 10));
        this.dataSource.setCheckoutTimeout(StringUtil.parseInt(props.getProperty(DBConstant.CONNECTION_POOL_PARAM_WAITTIMEOUT), 20000));
        this.dataSource.setMaxIdleTime(StringUtil.parseInt(props.getProperty("maxIdleTime"), 0));
        this.dataSource.setAcquireIncrement(1);


    }

    @Override
    public DBConnection getDBConnection() {
        try {
            return new DBConnection(this.dataSource.getConnection(),this.dbType,this.dsName);
        }catch (Exception e){
            Log.exception(e);
            return null;
        }

    }

    @Override
    public void resetPool() {
        //关闭超时连接
        this.dataSource.resetPoolManager(true);
    }
}
