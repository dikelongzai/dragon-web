package com.dragon.web.core.config.i18n;

import com.dragon.web.core.ResourcePool;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-02-04 20:38
 */
public class I18NUtil {
    public I18NUtil() {
    }

    public static String getResourceByTag(String tag) {
        if (tag != null && tag.startsWith("{I18N:") && tag.endsWith("}")) {
            String pkg = tag.substring(6);
            String name = pkg.substring(0, pkg.length() - 1);
            pkg = name.substring(0, name.indexOf(":"));
            name = name.substring(name.indexOf(":") + 1);
            return getResource(pkg, name);
        } else {
            return null;
        }
    }

    public static String getResource(String pkg, String name) {
        String local = I18NConfig.defaultLocal;
        if (ResourcePool.getCurrentUser() != null) {
            local = ResourcePool.getCurrentUser().get().getLocal();
        }

        HashMap<String, HashMap<String, String>> m2 = (HashMap)I18NConfig.resources.get(local);
        if (m2 == null) {
            return null;
        } else {
            HashMap<String, String> m3 = (HashMap)m2.get(pkg);
            return m3 == null ? null : (String)m3.get(name);
        }
    }

    public static String getResource(String pkg, String name, String... params) {
        String res = getResource(pkg, name);
        res = res == null ? null : MessageFormat.format(res, params);
        return res;
    }
}
