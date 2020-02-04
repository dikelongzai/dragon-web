package com.dragon.web.core.config.i18n;

import com.dragon.web.common.security.Log;
import com.dragon.web.common.util.FileUtil;
import com.dragon.web.common.util.StringUtil;
import com.dragon.web.core.config.BaseConfig;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-02-04 20:33
 */
public class I18NConfig extends BaseConfig {
    protected static Map<String, HashMap<String, HashMap<String, String>>> resources = new HashMap();
    private static String[] locals = new String[0];
    protected static String defaultLocal = "zh-CN";

    public static Map<String, HashMap<String, HashMap<String, String>>> getAllResources() {
        return resources;
    }

    public static String getDefaultLocal() {
        return defaultLocal;
    }

    public static boolean isSupport(String local) {
        return StringUtil.exists(local, locals, true, false);
    }

    protected void loadFromFile(String filename) {
        InputStream fis = null;
        try {
            fis = FileUtil.getInputStream(filename);
            SAXReader reader = new SAXReader(false);
            Document doc = reader.read(fis);
            String resPkg = doc.getRootElement().attributeValue("package");
            String local = doc.getRootElement().attributeValue("local");
            List<?> nodes = doc.selectNodes("/i18n/*");
            for (int j = 0; j < nodes.size(); ++j) {
                Element el2 = (Element) nodes.get(j);
                setResource(local, resPkg, el2.getName(), el2.getText());
            }
        } catch (Exception exp) {
            Log.debug("读取I18N(" + filename + ")配置异常", exp);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception exp) {

                }
            }

        }

    }


    public boolean checkFilename(String filename) {
        return filename.endsWith(".i18n.xml");
    }

    public static Map<String, HashMap<String, HashMap<String, String>>> getResources() {
        return resources;
    }

    public static synchronized void setResource(String local, String resPackage, String resId, String value) {
        if (resources.get(local) == null) {
            resources.put(local, new HashMap());
        }

        if (((HashMap) resources.get(local)).get(resPackage) == null) {
            ((HashMap) resources.get(local)).put(resPackage, new HashMap());
        }

        Map<String, String> res = (Map) ((HashMap) resources.get(local)).get(resPackage);
        res.put(resId, value);
    }

    public static void addResources(Map<String, HashMap<String, HashMap<String, String>>> resources) {
        Iterator iterator = resources.keySet().iterator();

        while (iterator.hasNext()) {
            String local = (String) iterator.next();
            HashMap<String, HashMap<String, String>> map2 = (HashMap) resources.get(local);
            Iterator iter = map2.keySet().iterator();
            while (iter.hasNext()) {
                String resPkg = (String) iter.next();
                HashMap<String, String> map3 = (HashMap) map2.get(resPkg);
                Iterator iterator4 = map3.keySet().iterator();
                while (iterator4.hasNext()) {
                    String resId = (String) iterator4.next();
                    String res = (String) map3.get(resId);
                    setResource(local, resPkg, resId, res);
                }
            }
        }

    }
}

