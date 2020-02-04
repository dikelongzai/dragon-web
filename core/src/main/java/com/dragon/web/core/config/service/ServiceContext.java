package com.dragon.web.core.config.service;

import com.dragon.web.common.security.Log;
import com.dragon.web.common.util.FileUtil;
import com.dragon.web.common.util.StringUtil;
import com.dragon.web.common.vo.ParameterObject;
import com.dragon.web.common.vo.ResultObject;
import com.dragon.web.common.vo.ThreadObject;
import com.dragon.web.core.ResourcePool;
import com.dragon.web.core.SystemManager;
import com.dragon.web.core.config.BaseConfig;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * ServiceContext 加载serviceContext
 *
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-15 13:50
 */
public class ServiceContext extends BaseConfig {
    protected static final String SERVICE_CONFIG_FILENAME = "service.xml";
    protected static final String SERVICE_EXPRESSION = "/services/service";
    protected static final String SYSTEM_CODE = "systemCode";
    public static final String REMOTE = "Remote";
    public static final String LOCAL = "Local";
    public static final String CORE_MODULE = "core";
    private static Map<String, InitialContext> sc = new HashMap();
    private static Map<String, String> LOCAL_CLASS = new HashMap();

    @Override
    public boolean checkFilename(String fileName) {
        return false;
    }

    @Override
    protected void loadFromFile(String fileName) {

    }

    @Override
    public synchronized void load() {
        InputStream fis = null;
        try {
            Log.debug("ServiceContext init...");
            fis = FileUtil.getInputStream(SystemManager.getConfigPath() + SERVICE_CONFIG_FILENAME);
            SAXReader reader = new SAXReader(false);
            Document doc = reader.read(fis);
            List<Element> services = doc.selectNodes(SERVICE_EXPRESSION);
            services.stream().forEach(service -> {
                List<Element> lst = service.selectNodes(PROPERTY_EXPRESSION);
                Properties properties = new Properties();
                lst.stream().forEach(pro -> {
                    properties.setProperty(pro.attributeValue(NAME_EXPRESSION), pro.getTextTrim());
                });
                Log.debug("ServiceContext:" + service.attributeValue(SYSTEM_CODE));
                try {
                    InitialContext ctx = new InitialContext(properties);
                    sc.put(service.attributeValue(SYSTEM_CODE), ctx);
                } catch (Exception e) {
                    Log.exception(e);
                }

            });
            Log.debug("ServiceContext init ok");
        } catch (Exception exp) {
            Log.exception(exp);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioException) {
                    Log.exception(ioException);
                }
            }

        }
    }

    /**
     * 反射加载类
     *
     * @param id
     * @param clazz
     */
    public static void addLocalClass(String id, String clazz) {
        try {
            if (Class.forName(clazz).getSuperclass() == CommonService.class) {
                LOCAL_CLASS.put(id, clazz);
            }
        } catch (Throwable throwable) {
            Log.debug("load：" + clazz + "err,msg:" + throwable.getMessage());
        }

    }

    /**
     * 获取服务实例 classForName->loadClass->getRemoteService->FileUtil classLoad
     *
     * @param module
     * @param name
     * @return
     */
    private static Object getService(String module, String name) {
        Object object = null;
        //反射instance

        if (LOCAL_CLASS.containsKey(name)) {
            try {
                object = Class.forName((String) LOCAL_CLASS.get(name)).newInstance();
            } catch (Exception e) {
                Log.exception(e);
            }
        }
        if (object == null) {
            try {
                object = Thread.currentThread().getContextClassLoader().loadClass(name).newInstance();
            } catch (Exception e) {
                Log.exception(e);
            }
        }
        if (object == null) {
            object = getRemoteService(SystemManager.getModule(module).getPropertyValue("service"), name);
            if (object == null) {
                try {
                    object = FileUtil.getResourceClass().getClassLoader().loadClass(name).newInstance();
                } catch (Exception exception) {
                    Log.exception(exception);
                }
            }
        }

        return object;
    }

    /**
     * 获取远程服务
     *
     * @param systemCode
     * @param name
     * @return
     */
    private static Object getRemoteService(String systemCode, String name) {
        Object obj = null;
        InitialContext ctx = (InitialContext) sc.get(systemCode);

        if (ctx != null) {
            try {
                obj = ctx.lookup(name);
            } catch (NamingException e) {

            }
        }
        if (obj == null) {
            try {
                obj = ctx.lookup(name + REMOTE);
            } catch (NamingException nameException) {

            }
        }

        if (obj == null) {
            try {
                obj = ctx.lookup(name + LOCAL);
            } catch (NamingException nameException) {

            }
        }

        return obj;
    }

    public static String[] getRemoteSystems() {
        String[] lst = new String[sc.size()];
        sc.keySet().toArray(lst);
        return lst;
    }

    /**
     * 调用服务
     *
     * @param csi
     * @param params
     * @return
     */
    private static ResultObject callService(Object csi, Map params) {
        ThreadObject to = ResourcePool.getCurrentThreadObject();
        if (StringUtil.isEmpty(to.getModuleName())) {
            to.setModuleName(CORE_MODULE);
        }
        if (StringUtil.isEmpty(to.getThreadUID())) {
            to.setThreadUID(StringUtil.getUUID() + System.currentTimeMillis());
        }
        ParameterObject po = new ParameterObject(params);
        po.setThreadObject(to);
        ResultObject ro = null;
        if (csi instanceof CommonServiceRemoteInf) {
            ro = ((CommonServiceRemoteInf) csi).call(po);
        } else if (csi instanceof CommonServiceLocalInf) {
            ro = ((CommonServiceLocalInf) csi).call(po);
        }
        return ro;

    }

    public static ResultObject callService(String module, String name, Map params) {
        Object csi = getService(module, name);
        return callService(csi, params);
    }

    public static ResultObject callRemoteService(String systemCode, String name, Map params) {
        Object csi = getService(systemCode, name);
        return callService(csi, params);
    }

}
