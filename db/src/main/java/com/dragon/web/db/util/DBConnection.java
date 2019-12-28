package com.dragon.web.db.util;

import com.dragon.web.common.security.Log;
import com.dragon.web.common.util.StringUtil;
import lombok.Data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-28 14:47
 */
@Data
public class DBConnection {
    private Connection conn = null;
    private Date createTime = null;
    //    private ThreadObject to = null;
    private String uid = "";
    private String dsName = "";
    private String lastSQL = null;
    private String lastSQL0 = null;
    private String lastSQL1 = null;
    private boolean dac = true;
    private boolean exBigData = true;
    private boolean exLongTime = false;
    private DBType dbType;

    public DBConnection(Connection conn, Date createTime, String uid, String dsName, DBType dbType) {
        this.conn = conn;
        this.createTime = createTime;
        this.uid = StringUtil.getUUID();
        this.dsName = dsName;
        this.dbType = dbType;
        try {
            this.conn.setAutoCommit(true);
        }catch (SQLException ex){
            Log.exception(ex);
        }

    }

    /***
     * 开始事物
     * @throws SQLException
     */
    public void beginTransaction() throws SQLException {
        this.conn.setAutoCommit(false);
    }

    /**
     * 回滚事物
     */
    public void rollbackTransaction() {
        try {
            this.conn.rollback();
        } catch (Exception var2) {
            Log.exception(var2);
        }

    }

    /**
     * 提交事物
     */
    public void commitTransaction(){
        try {
            this.conn.commit();
        } catch (Exception var2) {
            Log.exception(var2);
        }
    }

    /**
     * 获取meta信息
     * @return
     */
    public DatabaseMetaData getMetaData() {
        try {
            return this.conn.getMetaData();
        } catch (Exception ex) {
            Log.exception(ex);
            return null;
        }
    }
    public String getCatalog() {
        try {
            return this.conn.getCatalog();
        } catch (Exception var2) {
            Log.exception(var2);
            return null;
        }
    }

    public void close() {
        try {
            if (!this.conn.isClosed()) {
                this.conn.close();
            }

            DataSource.removeUsingDBConnection(this);
        } catch (Exception var2) {
            Log.exception(var2);
        }

    }
}
