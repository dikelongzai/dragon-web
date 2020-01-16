package com.dragon.web.core.config.security;

import com.dragon.web.common.security.Log;
import com.dragon.web.common.util.FileUtil;
import com.dragon.web.common.util.StringUtil;
import com.dragon.web.core.SystemManager;
import com.dragon.web.core.config.BaseConfig;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * log4j配置加载
 *
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-16 16:29
 */
public class LogConfig extends BaseConfig {

    public static final String LOG4J_CONFIG_NAME = "log4j.properties";

    public synchronized void load() {
        InputStream fis = null;

        try {
            fis = FileUtil.getInputStream(SystemManager.getConfigPath() + LOG4J_CONFIG_NAME);
            Properties props = new Properties();
            props.load(fis);
            try {
                //初始化文件
                File f = new File((String) props.get("log4j.appender.RootAppender.File"));
                if (!f.exists() && !f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            Log.init(props);
            Log.setLogDebug(this.configElement.selectSingleNode("debug") == null ? Log.isLogDebug() : "on".equalsIgnoreCase(((Element) this.configElement.selectSingleNode("debug")).getTextTrim()));
            Log.setLogWarn(this.configElement.selectSingleNode("warn") == null ? Log.isLogWarn() : "on".equalsIgnoreCase(((Element) this.configElement.selectSingleNode("warn")).getTextTrim()));
            Log.setLogException(this.configElement.selectSingleNode("exception") == null ? Log.isLogException() : "on".equalsIgnoreCase(((Element) this.configElement.selectSingleNode("exception")).getTextTrim()));
            Log.setLogException(this.configElement.selectSingleNode("biz") == null ? Log.isLogBiz() : "on".equalsIgnoreCase(((Element) this.configElement.selectSingleNode("biz")).getTextTrim()));
            Log.setMaxLongTimeMilliseconds(StringUtil.parseLong(this.configElement.attributeValue("logMaxLongTimeMilliseconds"), Log.getMaxLongTimeMilliseconds()));
            Log.setMaxBigDataSize(StringUtil.parseInt(this.configElement.attributeValue("logMaxBigDataSize"), Log.getMaxBigDataSize()));
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException exception) {

                }
            }

        }

    }

    @Override
    public boolean checkFilename(String fileName) {
        return false;
    }

    @Override
    protected void loadFromFile(String fileName) {

    }
}
