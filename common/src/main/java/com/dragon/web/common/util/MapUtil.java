package com.dragon.web.common.util;

import java.util.*;

/**
 * MapUtil
 *
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-15 13:19
 */
public class MapUtil {
    /**
     * map 复制
     *
     * @param from
     * @param to
     * @param type
     */
    public static void copyMap(Map from, Map to, int type) {
        if (from != null && to != null) {
            Iterator it = from.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                if (key != null && from.get(key) != null) {
                    to.put(key.toString(), to.get(key));
                }
            }

        }

    }
    public static String getString(Map map, String key) {
        return map != null && key != null ? StringUtil.obj2String(map.get(key)) : null;
    }
    public static List<String> cloneList(List<String> obj) {
        if (obj == null) {
            return null;
        } else {
            List<String> res = new ArrayList();
            Iterator it = obj.iterator();
            while(it.hasNext()) {
                String val = (String)it.next();
                res.add(val);
            }

            return res;
        }
    }

    public static Map<String, String> cloneMap(Map<String, String> obj) {
        if (obj == null) {
            return null;
        } else {
            Map<String, String> res = new HashMap();
            Iterator it = obj.keySet().iterator();

            while(it.hasNext()) {
                String key = (String)it.next();
                String val = (String)obj.get(key);
                res.put(key, val);
            }

            return res;
        }
    }

    public static List<Map<String, String>> cloneListMap(List<Map<String, String>> obj) {
        if (obj == null) {
            return null;
        } else {
            List<Map<String, String>> res = new ArrayList();
            Iterator it = obj.iterator();

            while(it.hasNext()) {
                res.add(cloneMap((Map)it.next()));
            }

            return res;
        }
    }

}
