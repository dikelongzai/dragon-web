package com.dragon.web.core;

import com.dragon.web.core.config.BaseConfig;
import com.dragon.web.core.vo.Module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-02 18:52
 */
public class SystemManager {
    private static List<BaseConfig> configs = new ArrayList();
    protected static List<Module> modules = new ArrayList();
    private static String configPath = "/";

    public static void setConfigPath(String configPath) {
        SystemManager.configPath = configPath;
    }

    public static List<Module> getModules() {
        return modules;
    }

    /**
     * 获取模块
     * @param name
     * @return
     */
    public static Module getModule(String name) {
        Iterator iterator = modules.iterator();
        while (iterator.hasNext()) {
            Module m = (Module) iterator.next();
            if (name.equalsIgnoreCase(m.getName())) {
                return m;
            }
        }

        return null;
    }

    /**
     * 是否以jar方式运行
     *
     * @return
     */
    public static boolean isJARApp() {
        return configPath.startsWith("jar://");
    }

    public static List<BaseConfig> getConfigs() {
        return configs;
    }

    public static String getConfigPath() {
        return configPath;
    }
}
