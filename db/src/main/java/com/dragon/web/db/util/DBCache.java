package com.dragon.web.db.util;

import com.dragon.web.common.security.Log;
import com.dragon.web.common.util.MapUtil;

import java.util.*;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-15 13:18
 */
public class DBCache {
    private static Map<String, String> CACHE_DS = new HashMap();
    private static Map<String, List<Map<String, String>>> CACHE_DATA = new HashMap();

    /**
     * 获取缓存数据
     * @param name
     * @return
     */
    public static List<Map<String, String>> getCahceData(String name) {
        List<Map<String, String>> datas = (List)CACHE_DATA.get(name);
        return MapUtil.cloneListMap(datas);
    }

    /**
     * 清除缓存
     * @param table
     */
    public static void remove(String table) {
        CACHE_DS.remove(table);
        CACHE_DATA.remove(table);
    }

    /**
     * 添加缓存
     * @param table
     * @param datasource
     */
    public static void add(String table, String datasource) {
        CACHE_DS.put(table, datasource);
        reload(table);
    }

    /**
     * 获取list缓存
     * @return
     */
    public static List<String> getCacheList() {
        List<String> lst = new ArrayList();
        Iterator it = CACHE_DS.keySet().iterator();

        while(it.hasNext()) {
            lst.add((String)it.next());
        }

        return lst;
    }

    /**
     * 重新加载表缓存
     * @param table
     */
    public static void reload(String table) {
        if (CACHE_DS.containsKey(table)) {
            DBConnection dbc = DataSource.getDBConnection((String)CACHE_DS.get(table));

            try {
                CACHE_DATA.remove(table);
                CACHE_DATA.put(table, dbc.queryBySql("select * from " + table));
            } catch (Exception exp) {
                Log.exception(exp);
            } finally {
                if (dbc != null) {
                    dbc.close();
                }

            }

        }
    }
}
