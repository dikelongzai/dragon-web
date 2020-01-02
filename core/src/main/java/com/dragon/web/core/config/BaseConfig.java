package com.dragon.web.core.config;

import com.dragon.web.common.security.Log;
import com.dragon.web.common.util.FileUtil;
import com.dragon.web.common.util.StringUtil;
import com.dragon.web.core.Reloader;
import com.dragon.web.core.SystemManager;
import com.dragon.web.core.vo.Module;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * BaseConfig
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-02 18:43
 */
public abstract class BaseConfig {
    protected long lastLoadTimeMillis = 0L;
    private long thisTime = 0L;
    private List<String> additionalFiles = new ArrayList();
    private Reloader reloader = null;
    protected Element configElement = null;
    protected static final String FILE_START = "file:/";

    public abstract boolean checkFilename(String var1);

    protected abstract void loadFromFile(String var1);

    public Element getConfigElement() {
        return configElement;
    }

    public void setConfigElement(Element configElement) {
        this.configElement = configElement;
    }

    public long getLastLoadTimeMillis() {
        return lastLoadTimeMillis;
    }

    public void setLastLoadTimeMillis(long lastLoadTimeMillis) {
        this.lastLoadTimeMillis = lastLoadTimeMillis;
    }

    public long getThisTime() {
        return thisTime;
    }

    public void setThisTime(long thisTime) {
        this.thisTime = thisTime;
    }

    public Reloader getReloader() {
        return reloader;
    }

    public void setReloader(Reloader reloader) {
        this.reloader = reloader;
    }

    /**
     * 加载配置
     */
    public synchronized void load() {
        this.thisTime = this.lastLoadTimeMillis;
        if (SystemManager.isJARApp()) {

        }
    }

    /**
     * jar包加载
     */
    public void loadJarApp() {
        this.loadAdditionalConfig();
        try {
            String url = FileUtil.getResourceClass().getResource("/" + SystemManager.getConfigPath().substring(6)).getFile();
            if (url.startsWith(FILE_START)) {
                url = url.substring(6);
            }
            if (url.indexOf(33) > 0) {
                url = url.substring(0, url.indexOf(33));
            }
            JarFile file = new JarFile(url);
            try {
                Enumeration enums = file.entries();
                while (enums.hasMoreElements()) {
                    JarEntry entry = (JarEntry) enums.nextElement();
                    if (this.checkFilename(FileUtil.getLastFilename(entry.getName()))) {
                        String fn = "jar://" + entry.getName();
                        if (this.isValidateModuleFileName(fn)) {
                            this.loadFromFile(fn);
                        }
                    }
                }
            } finally {
                file.close();
            }
        } catch (Exception var10) {
            Log.exception(var10);
        }
    }

    /**
     * 文件是否验证
     * @param fn
     * @return
     */
    public boolean isValidateModuleFileName(String fn) {
        if (StringUtil.isEmpty(fn)) {
            return false;
        } else {
            if (fn.startsWith(SystemManager.getConfigPath())) {
                fn = fn.substring(SystemManager.getConfigPath().length());
            } else if (fn.startsWith("jar://configs/")) {
                fn = fn.substring("jar://configs/".length());
            }
            int a = fn.indexOf("/");
            a = Math.max(a, fn.indexOf("\\"));
            if (a > 0) {
                fn = fn.substring(0, a);
            }
            return SystemManager.getModule(fn) != null;
        }
    }

    /**
     * 加载config配置维护已加载文件 模板方法
     */
    protected void loadAdditionalConfig() {
        for (int i = this.additionalFiles.size() - 1; i >= 0; --i) {
            File f = new File(this.additionalFiles.get(i));
            if (f.exists() && f.isFile()) {
                if (this.lastLoadTimeMillis < f.lastModified()) {
                    this.thisTime = Math.max(this.thisTime, f.lastModified());
                    this.loadFromFile(this.additionalFiles.get(i));
                }
            } else {
                this.loadFromFile(this.additionalFiles.get(i));
                this.additionalFiles.remove(i);
            }
        }

    }

    /**
     * 获取文件夹最后修改时间 为了动态加载配置项
     *
     * @param dir
     * @return
     */
    private long getLastModifiedOfDir(File dir) {
        if (dir != null && dir.exists()) {
            if (dir.isDirectory()) {
                File[] fs = dir.listFiles();
                long res = 0L;
                for (int i = 0; i < fs.length; ++i) {
                    if (fs[i].isDirectory()) {
                        res = Math.max(res, this.getLastModifiedOfDir(fs[i]));
                    } else if (this.checkFilename(fs[i].getName())) {
                        res = Math.max(res, fs[i].lastModified());
                    }
                }
                return res;
            } else {
                return this.checkFilename(dir.getName()) ? dir.lastModified() : 0L;
            }
        } else {
            return 0L;
        }
    }

    /**
     * 最后修改时间
     *
     * @return
     */
    public long getLastModified() {
        long res = 0L;
        Iterator iterator = this.additionalFiles.iterator();
        File dir;
        while (iterator.hasNext()) {
            String fn = (String) iterator.next();
            dir = new File(fn);
            if (dir.exists() && dir.isFile()) {
                res = Math.max(res, dir.lastModified());
            }
        }
        for (Iterator iter = SystemManager.getModules().iterator(); iterator.hasNext(); res = Math.max(res, this.getLastModifiedOfDir(dir))) {
            Module m = (Module) iter.next();
            dir = new File(SystemManager.getConfigPath() + m.getName());
        }

        return res;
    }
}
