package com.dragon.web.db.util;

import com.dragon.web.common.security.BizException;
import com.dragon.web.common.security.Log;
import com.dragon.web.common.util.DateUtil;
import com.dragon.web.common.util.Profiler;
import com.dragon.web.common.util.StringUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
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
        } catch (SQLException ex) {
            Log.exception(ex);
        }

    }

    public DBConnection(Connection conn, DBType dbType, String dsName) {
        this.dbType = DBType.oracle;
        this.dbType = dbType;
        this.dsName = dsName;
        this.conn = conn;
        this.createTime = new Date();
        this.uid = StringUtil.getUUID();

        try {
            this.conn.setAutoCommit(true);
        } catch (SQLException exp) {
            Log.exception(exp);
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
    public void commitTransaction() {
        try {
            this.conn.commit();
        } catch (Exception var2) {
            Log.exception(var2);
        }
    }

    /**
     * 数据集查询
     *
     * @param sql   源sql
     * @param param 参数
     * @return 多行结果集 List<Map<String, String>>
     * @throws SQLException
     */

    public List<Map<String, String>> queryBySql(String sql, Object param) throws SQLException {
        ResultSet rs = null;
        List lst;
        try {
            rs = this.queryResultSetBySql(sql, param);
            lst = this.resultSet2List(rs);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception exp) {
                    Log.exception(exp);
                }

                if (rs.getStatement() != null) {
                    try {
                        rs.getStatement().close();
                    } catch (Exception exp) {
                        Log.exception(exp);
                    }
                }
            }

        }

        return lst;
    }

    /**
     * convet ResultSet->List<Map<String, String>>
     * @param rs ResultSet
     * @return List<Map<String, String>>
     */
    public List<Map<String, String>> resultSet2List(ResultSet rs) {
        List<Map<String, String>> lst = new ArrayList();
        int size = 0;
        try {
            while (rs.next()) {
                Map<String, String> m = this.resultSet2Map2(rs);
                ++size;
                if (this.exBigData && Log.logBigData(size, "LAST SQL:" + this.lastSQL)) {
                    Log.debug("resultSet count:" + size);
                    throw new BizException("查询结果超过了" + size + "，数据量过多！");
                }

                lst.add(m);
            }
        } catch (SQLException exp) {
            Log.debug(exp);
        }

        Log.debug("resultSet count:" + lst.size());
        return lst;
    }

    /**
     * 无参数查询ResultSet
     * @param sql
     * @return
     * @throws SQLException
     */
    public ResultSet queryResultSetBySql(String sql)throws  SQLException {
        return this.queryResultSetBySql(sql, (Object) null);
    }

    /**
     * 查询 ResultSet
     * @param sql
     * @param param
     * @return ResultSet
     * @throws SQLException
     */
    public ResultSet queryResultSetBySql(String sql, Object param) throws SQLException {

        long t1 = System.currentTimeMillis();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = this.prepareStatement(sql, param);
            Log.debug("SQL:" + this.lastSQL);
            t1 = System.currentTimeMillis();
            rs = stmt.executeQuery();
        } catch (Exception var9) {
            Log.debug(var9);
            Log.debug("SQL0:" + this.lastSQL0);
            Log.debug("SQL1:" + this.lastSQL1);
            throw new SQLException(var9.getMessage());
        }

        long t2 = System.currentTimeMillis();
        Log.debug("executeQuery TimeMillis:" + (t2 - t1));
        if (Log.logLongTime(t1, t2, "SQL:" + this.lastSQL) && this.exLongTime) {
            throw new BizException("查询时间过长！");
        } else {
            return rs;
        }


    }

    /**
     * 参数绑定
     *
     * @param sql   绑定前sql
     * @param param 参数 可以是 List Map<paramName,paramName>
     * @return PreparedStatement
     * @throws Exception
     */
    public PreparedStatement prepareStatement(String sql, Object param) throws Exception {
        this.lastSQL0 = sql;
        this.lastSQL1 = sql;
        this.lastSQL = sql;
        String inputStr = sql;
        int cnt = 0;
        List params = new ArrayList();
        int indexStart = 0, indexEnd = 0;
        String tmpParamName = null;
        Object val = null;
        //字符参数替换
        for (boolean end = false; inputStr.indexOf("{p:", indexStart) > 0; indexStart += (val == null ? "null" : val.toString()).length()) {
            indexStart = inputStr.indexOf("{p:", indexStart);
            indexEnd = inputStr.indexOf("}", indexStart + 1);
            if (indexEnd < 0) {
                break;
            }
            tmpParamName = inputStr.substring(indexStart + 3, indexEnd);
            val = this.value(tmpParamName, param, "");
            inputStr = inputStr.substring(0, indexStart) + (val == null ? "null" : val.toString()) + inputStr.substring(indexEnd + 1);
            ++cnt;
        }
        indexStart = 0;
        for (boolean end = false; inputStr.indexOf("{dac:", indexStart) >= 0; indexStart += tmpParamName.length()) {
            indexStart = inputStr.indexOf("{dac:", indexStart);
            indexEnd = inputStr.indexOf("}", indexStart + 1);
            if (indexEnd < 0) {
                break;
            }

            tmpParamName = inputStr.substring(indexStart + 5, indexEnd);
            tmpParamName = this.dac && DataSource.getDbm() != null ? DataSource.getDbm().getSqlParamDAC(tmpParamName) : " 1=1 ";
            inputStr = inputStr.substring(0, indexStart) + tmpParamName + inputStr.substring(indexEnd + 1);
        }
        //isnotnull 处理
        inputStr = inputStr.replaceAll("\\{sql\\[isnotnull\\]\\}", " is not null ");
        //isnull 处理
        inputStr = inputStr.replaceAll("\\{sql\\[isnull\\]\\}", " is null ");
        tmpParamName = inputStr;
        indexStart = 0;
        indexEnd = 0;
        int indexOfStart = 0, indexOfEnd = 0;
//        int i1 = false;
//        var7 = false;

        while (tmpParamName.indexOf("{", indexStart) > 0) {
            indexStart = tmpParamName.indexOf("{", indexStart);
            indexEnd = tmpParamName.indexOf("}", indexStart);
            if (indexEnd < 0) {
                break;
            }

            String paramStr = tmpParamName.substring(indexStart + 1, indexEnd);
            String paramName = paramStr.substring(paramStr.indexOf(":") + 1);
            String paramType = "";
            String paramType2 = "";
            if (paramStr.indexOf(":") > 0) {
                paramType = paramStr.substring(0, paramStr.indexOf(":"));
                if (paramType.indexOf("[") >= 0 && paramType.endsWith("]")) {
                    paramType2 = paramType.substring(paramType.indexOf("[") + 1, paramType.length() - 1);
                    paramType = paramType.substring(0, paramType.indexOf("["));
                }
            }

            ++cnt;
            if ("dt".equalsIgnoreCase(paramType) && paramName.startsWith("'") && paramName.endsWith("'")) {
                val = StringUtil.toDate(paramName.substring(1, paramName.length() - 1));
            } else {
                if ("sql".equalsIgnoreCase(paramType) && paramType2.indexOf("#") > 0) {
                    int n = StringUtil.parseInt(paramType2.substring(0, paramType2.indexOf("#")), 0);
                    String t = paramType2.substring(paramType2.indexOf("#") + 1);
                    String v0 = "{sql[" + paramType2 + "]:";
                    indexEnd = indexStart + v0.length() + n + 1;
                    String v = inputStr.substring(indexStart + v0.length(), indexEnd - 1);
                    Object o = null;
                    indexOfStart = inputStr.indexOf(v0 + v + "}");
                    indexOfEnd = indexOfStart + v0.length() + v.length() + 1;
                    if ("%like%".equals(t)) {
                        o = "%" + v + "%";
                        if (this.dbType == DBType.mysql) {
                            inputStr = inputStr.substring(0, indexOfStart) + " CONCAT('%','" + v.replaceAll("'", "''") + "','%') " + inputStr.substring(indexOfEnd);
                        } else if (this.dbType == DBType.sybase) {
                            inputStr = inputStr.substring(0, indexOfStart) + " '%'+'" + v.replaceAll("'", "''") + "'+'%' " + inputStr.substring(indexOfEnd);
                        } else {
                            inputStr = inputStr.substring(0, indexOfStart) + " '%'||'" + v.replaceAll("'", "''") + "'||'%' " + inputStr.substring(indexOfEnd);
                        }
                    } else if ("n".equals(t)) {
                        try {
                            o = new BigDecimal(v);
                        } catch (Exception exp) {
                            Log.exception(exp);
                        }

                        inputStr = inputStr.substring(0, indexOfStart) + v + inputStr.substring(indexOfEnd);
                    } else {
                        if (!"s".equals(t)) {
                            indexStart = indexEnd;
                            continue;
                        }

                        o = v;
                        inputStr = inputStr.substring(0, indexOfStart) + "'" + v.replaceAll("'", "''") + "'" + inputStr.substring(indexOfEnd);
                    }

                    tmpParamName = tmpParamName.substring(0, indexStart) + "?" + tmpParamName.substring(indexEnd);
                    ++cnt;
                    params.add(o);
                    ++indexStart;
                    continue;
                }

                val = this.value(paramName, param, paramType);
            }

            indexOfStart = inputStr.indexOf("{" + paramStr + "}", indexOfStart);
            indexOfEnd = indexOfStart + ("{" + paramStr + "}").length() - 1;
            if (val == null) {
                inputStr = inputStr.substring(0, indexOfStart) + "null" + inputStr.substring(indexOfEnd + 1);
                tmpParamName = tmpParamName.substring(0, indexStart) + "null" + tmpParamName.substring(indexEnd + 1);
                ++indexStart;
            } else {
                if ("n".equalsIgnoreCase(paramType)) {
                    inputStr = inputStr.substring(0, indexOfStart) + val + inputStr.substring(indexOfEnd + 1);
                    params.add(val);
                } else if ("dt".equalsIgnoreCase(paramType)) {
                    if (!StringUtil.isEmpty(paramType2)) {
                        val = DateUtil.addDays((Date) val, StringUtil.parseInt(paramType2, 0));
                    }

                    String dt = StringUtil.dateToString((Date) val);
                    if (dt == null) {
                        dt = "null";
                    } else if (dt.length() > 10) {
                        if (this.dbType == DBType.mysql) {
                            dt = "DATE_FORMAT('" + dt + "','%Y %m %d %H %i %S')";
                        } else if (this.dbType == DBType.sybase) {
                            dt = "'" + dt + "'";
                        } else {
                            dt = "to_date('" + dt + "','yyyy-mm-dd hh24:mi:ss')";
                        }
                    } else if (this.dbType == DBType.mysql) {
                        dt = "DATE_FORMAT('" + dt + "','%Y %m %d')";
                    } else if (this.dbType == DBType.sybase) {
                        dt = "'" + dt + "'";
                    } else {
                        dt = "to_date('" + dt + "','yyyy-mm-dd')";
                    }

                    inputStr = inputStr.substring(0, indexOfStart) + dt + inputStr.substring(indexOfEnd + 1);
                    params.add(val);
                } else {
                    inputStr = inputStr.substring(0, indexOfStart) + "'" + ((String) val).replaceAll("'", "''") + "'" + inputStr.substring(indexOfEnd + 1);
                    params.add(val);
                }

                tmpParamName = tmpParamName.substring(0, indexStart) + "?" + tmpParamName.substring(indexEnd + 1);
                ++indexStart;
            }
        }

        this.lastSQL = inputStr;
        if (param != null && cnt == 0 && tmpParamName.indexOf("?") > 0) {
            if (param instanceof List) {
                params.addAll((List) param);
            } else if (params.isEmpty() && !(param instanceof Map)) {
                params.add(param);
            }
        }

        this.lastSQL1 = tmpParamName;
        PreparedStatement stmt = this.conn.prepareStatement(tmpParamName);

        for (int i = 0; i < params.size(); ++i) {
            Object valTmp = params.get(i);
            if (val == null) {
                stmt.setNull(i + 1, 0);
            } else if (val instanceof Date) {
                stmt.setTimestamp(i + 1, new Timestamp(((Date) val).getTime()));
            } else if (val instanceof BigDecimal) {
                stmt.setBigDecimal(i + 1, (BigDecimal) val);
            } else {
                stmt.setString(i + 1, val.toString());
            }
        }

        return stmt;

    }

    /**
     * 根据变量名 类型 获取变量值
     *
     * @param name      变量名
     * @param param     参数集合
     * @param paramType 变量类型
     * @return 变量值
     */
    private Object value(String name, Object param, String paramType) {
        // sql sysdate sysdatetime login sys 系统变量替换
        if ("sysdate".equalsIgnoreCase(name)) {
            return StringUtil.toDate(StringUtil.getSysdate());
        } else if ("sysdatetime".equalsIgnoreCase(name)) {
            return StringUtil.toDate(StringUtil.getSysdate());
        } else if (name.startsWith("login.")) {
            return DataSource.getDbm() != null ? DataSource.getDbm().getSqlParamLogin(name.substring(6)) : null;

        } else if (name.startsWith("sys.")) {
            return DataSource.getDbm() != null ? DataSource.getDbm().getSqlParamLogin(name.substring(4)) : null;
        } else {
            Object val = null;
            if (param == null) {
                return null;
            } else {
                if (param instanceof Map) {
                    if (!((Map) param).containsKey(name)) {
                        return null;
                    }
                    val = ((Map)param).get(name);
                } else if (param instanceof List) {
                    if (((List) param).isEmpty()) {
                        return null;
                    }

                    val = ((List) param).get(0);
                }
                //日期类型
                if ("dt".equalsIgnoreCase(paramType)) {
                    Date dt = null;
                    if (val instanceof String) {
                        dt = StringUtil.toDate((String) val);
                    } else if (val instanceof Date) {
                        dt = (Date) val;
                    }

                    return dt;
                } else if ("n".equalsIgnoreCase(paramType)) {//数值类型
                    try {
                        return new BigDecimal(val.toString());
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    return val == null ? null : val.toString();
                }
            }
        }


    }

    /**
     * 无参数绑定查询
     * @param sql
     * @return
     * @throws SQLException
     */
    public List<Map<String, String>> queryBySql(String sql) throws SQLException {
        return this.queryBySql(sql, (Object) null);
    }

    /**
     * @param sql
     * @param param
     * @return 结果集
     * @throws SQLException
     */
    public Map<String, String> selectBySql(String sql, Object param) throws SQLException {
        Map<String, String> map = null;
        ResultSet rs = null;

        try {
            rs = this.queryResultSetBySql(sql, param);
            if (rs.next()) {
                map = this.resultSet2Map2(rs);
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception exp) {
                    Log.exception(exp);
                }

                if (rs.getStatement() != null) {
                    try {
                        rs.getStatement().close();
                    } catch (Exception e) {
                        Log.exception(e);
                    }
                }
            }

        }

        return map;
    }

    /**
     * ResultSet转化为Map
     *
     * @param rs
     * @return
     */
    public Map<String, String> resultSet2Map2(ResultSet rs) {
        HashMap res = null;
        try {
//            java.sql.Types
            ResultSetMetaData rsmd = rs.getMetaData();
            int cnt = rs.getMetaData().getColumnCount();
            res = new HashMap();
            for (int i = 1; i <= cnt; ++i) {
                String val = rs.getString(i);
                String key = rsmd.getColumnLabel(i).toUpperCase();
                if (val == null) {
                    res.put(key, "");
                } else {
                    int t = rsmd.getColumnType(i);
                    //非日期类型 92 93 91日期类型
                    if (t != 92 && t != 93 && t != 91) {
                        // 2,6,8,3数据类型
                        if (t != 2 && t != 6 && t != 8 && t != 3) {
                            res.put(key, val);
                        } else {
                            //数据类型补0
                            if (val.charAt(0) == '.') {
                                val = "0" + val;
                            } else if (val.indexOf("-.") == 0) {
                                val = "-0." + val.substring(2);
                            }

                            res.put(key, val);
                        }
                    } else {
                        //日期类型
                        if (t == 93) {
                            Timestamp ts = rs.getTimestamp(i);
                            if (ts != null) {
                                val = ts.toString();
                            } else {
                                val = "";
                            }
                        }

                        try {
                            Date dt = StringUtil.toDate(val.trim());
                            val = dt != null ? StringUtil.dateToString(dt) : "";
                            res.put(key, val);
                        } catch (Exception exp) {
                            Log.exception(exp);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.debug(ex);
        }

        return res;
    }


    /**
     * 获取meta信息
     *
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

    /**
     * 查询结果集条数
     * @param sql
     * @param param
     * @return
     * @throws SQLException
     */
    public String selectDataBySql(String sql, Object param) throws SQLException {
        String res = null;
        ResultSet rs = null;

        try {
            rs = this.queryResultSetBySql(sql, param);
            if (rs.next()) {
                res = rs.getString(1);
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception var13) {
                    ;
                }

                if (rs.getStatement() != null) {
                    try {
                        rs.getStatement().close();
                    } catch (Exception var12) {
                        ;
                    }
                }
            }

        }

        return res;
    }

    /**
     * 查询结果条数
     * @param sql
     * @param param
     * @return
     * @throws SQLException
     */
    public int countBySql(String sql, Object param) throws SQLException {
        if (this.dbType == DBType.sybase) {
            sql = sql.replaceAll("ORDER BY", "order by");
            if (sql.lastIndexOf("order by") != -1) {
                sql = sql.substring(0, sql.lastIndexOf("order by"));
            }
        }

        sql = "select count(1) cnt from (" + sql + ") a";
        return StringUtil.parseInt(this.selectDataBySql(sql, param), 0);
    }

    /**
     * 查询只有一列结果集
     * @param sql
     * @return List<String>
     * @throws SQLException
     */
    public List<String> queryDataBySql(String sql) throws SQLException {
        return this.queryDataBySql(sql, (Object)null);
    }

    /**
     * 查询只有一列结果集
     * @param sql
     * @param param
     * @return
     * @throws SQLException
     */
    public List<String> queryDataBySql(String sql, Object param) throws SQLException {
        List<String> res = new ArrayList();
        ResultSet rs = null;

        try {
            rs = this.queryResultSetBySql(sql, param);

            while(rs.next()) {
                res.add(rs.getString(1));
            }

            Log.debug("resultSet count:" + res.size());
            return res;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception var13) {
                    ;
                }

                if (rs.getStatement() != null) {
                    try {
                        rs.getStatement().close();
                    } catch (Exception var12) {
                        ;
                    }
                }
            }

        }
    }

}
