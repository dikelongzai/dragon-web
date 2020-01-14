package com.dragon.web.core;

import com.dragon.web.core.config.BaseConfig;

/**
 * 重新加载配置 Runnable
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-02 18:45
 */
public class Reloader implements Runnable {
    private BaseConfig config;
    public Reloader(BaseConfig baseConfig) {
        this.config = baseConfig;
    }
    @Override
    public void run() {
        long ms=this.config.getLastLoadTimeMillis();
        if(ms>this.config.getLastLoadTimeMillis()){
            this.config.load();
        }
    }








}
