package com.dragon.web.core.config.db;

import com.dragon.web.common.security.Log;
import com.dragon.web.common.util.FileUtil;
import com.dragon.web.core.SystemManager;
import com.dragon.web.core.config.BaseConfig;
import com.dragon.web.db.util.DataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-07 12:00
 */
public class DataSourceConfig extends BaseConfig {

    public static final String DATASOURCES_DATASOURCE = "/datasources/datasource";

    @Override
    public boolean checkFilename(String fileName) {
        return false;
    }

    @Override
    protected void loadFromFile(String fileName) {

    }

    @Override
    public synchronized void load() {
        InputStream inputStream = null;
        if (DataSource.getDbm() == null) {
            DataSource.setDbm(new DBMethod());
        }
        Log.debug("Datasource init...");

        try {
            inputStream=FileUtil.getInputStream(SystemManager.getConfigPath()+ "datasource.xml");
            SAXReader reader=new SAXReader(false);
            Document document=reader.read(inputStream);
            List<Element> list=document.selectNodes(DATASOURCES_DATASOURCE);
            Properties properties = new Properties();
            for(Element ele:list){
                properties.setProperty(ele.attributeValue("name"), ele.getTextTrim());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
