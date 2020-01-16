package com.dragon.web.core.config;

import com.dragon.web.common.security.Log;
import com.dragon.web.core.SystemManager;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-16 16:34
 */
public class LogConfig {
    @Before
    public void init(){
        SystemManager.setConfigPath("D:\\workspace\\dragon-web\\core\\src\\test\\resources/");
    }
    @Test
    public void testLog(){

        BaseConfig baseConfig=new com.dragon.web.core.config.security.LogConfig();
//        BaseConfig.createInstance()
        baseConfig.load();
        Log.debug("logDebugTest...");
    }
}
