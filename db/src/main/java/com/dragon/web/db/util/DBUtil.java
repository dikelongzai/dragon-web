package com.dragon.web.db.util;

import com.dragon.web.common.util.StringUtil;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @date 2019-12-28 14:38
 */
public class DBUtil {
    public static DBConnection newDBConnection() {
        return DataSource.getDBConnection(DBConstant.DEFAULT_DSNAME);
    }

    public static List<Map<String, String>> getTableWithComment(String tableName) throws Exception {
        List<Map<String, String>> list = new ArrayList<>();
        DBConnection dbc = newDBConnection();
        ResultSet rs = null;

        try {
            DatabaseMetaData md = dbc.getMetaData();
            if (dbc.getDbType() == DBType.oracle) {
                list = dbc.queryBySql("select table_name,comments from all_tab_comments where (table_type='TABLE' or table_type='VIEW') and table_name not like '%$%' and owner='" + md.getUserName().toUpperCase() + "' " + (StringUtil.isEmpty(tableName) ? "" : " and table_name like '%" + tableName.toUpperCase() + "%'") + " order by table_name");
            } else {
                rs = md.getTables(dbc.getCatalog(), md.getUserName(), "%" + tableName + "%", new String[]{"TABLE", "VIEW"});

                while (rs.next()) {
                    Map<String, String> m = new HashMap();
                    m.put("TABLE_NAME", StringUtil.null2blank(rs.getString("TABLE_NAME")).toUpperCase());
                    m.put("COMMENTS", rs.getString("REMARKS"));
                    list.add(m);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }

            dbc.close();
        }

        return  list;
    }

}
