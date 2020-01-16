package com.dragon.web.core.config.db;

import com.dragon.web.common.security.Log;
import com.dragon.web.common.util.FileUtil;
import com.dragon.web.core.SystemManager;
import com.dragon.web.core.config.BaseConfig;
import com.dragon.web.db.util.C3P0Factory;
import com.dragon.web.db.util.DataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * TODO: 2020-01-15  后续除支持C3P0还需要支持druid
 * 加载datasource 初始化连接池 可配置多数据池
 * <p>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <datasources>
 * 数据源配置
 * <datasource name="core" type="c3p0">
 * <property name="jdbc.driver">com.mysql.jdbc.Driver</property>
 * <property name="jdbc.url">jdbc:mysql://localhost:3306/emoss
 * </property>
 * <property name="jdbc.username">root</property>
 * <property name="jdbc.password">888888</property>
 * <property name="maxPoolSize">100</property> 连接池中保留的最大连接数 -
 * <property name="initPoolSize">3</property> 初始化时创建的连接数
 * <property name="waitTimeout">20000</property>等待获取新连接的时间，如设为0则无限期等待。单位毫秒
 * <property name="maxIdleTime">0</property>最大空闲时间，超过空闲时间的连接将被丢弃。为0或负数则永不丢弃。单位毫秒
 * </datasource>
 * </datasources>
 * </p>
 *
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-07 12:00
 */
public class DataSourceConfig extends BaseConfig {

    public static final String DATASOURCES_DATASOURCE = "/datasources/datasource";
    public static final String DATASOURCE_CONFIG_NAME = "datasource.xml";


    @Override
    public boolean checkFilename(String fileName) {
        return false;
    }

    @Override
    protected void loadFromFile(String fileName) {

    }

    @Override
    public synchronized void load() {

        if (DataSource.getDbm() == null) {
            DataSource.setDbm(new DBMethod());
        }
        Log.debug("Datasource init...");

        try {
            List<Element> list = loadDatasourceXML();
            for (Element datasource : list) {
                Properties properties = new Properties();
                List<Element> lst = datasource.selectNodes(PROPERTY_EXPRESSION);
                for (Element property : lst) {
                    properties.setProperty(property.attributeValue(NAME_EXPRESSION), property.getTextTrim());
                }
                Log.debug("DataSource:" + datasource.attributeValue(NAME_EXPRESSION));
                //初始化连接池 TODO: 2020-01-15  后续除支持C3P0还需要支持druid
                if ("c3p0".equals(datasource.attributeValue("type"))) {
                    initC3P0Factory(datasource, properties);

                }

            }
            Log.debug("DataSource init ok");
        } catch (DocumentException e) {
            Log.exception(e);
        }
    }

    /**
     * 加载datasource.xml
     *
     * @return
     * @throws DocumentException
     */
    private List<Element> loadDatasourceXML() throws DocumentException {
        InputStream inputStream = null;
        Document document;
        try {
            inputStream = FileUtil.getInputStream(SystemManager.getConfigPath() + DATASOURCE_CONFIG_NAME);
            SAXReader reader = new SAXReader(false);
            document = reader.read(inputStream);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return (List<Element>) document.selectNodes(DATASOURCES_DATASOURCE);
    }

    /**
     * 初始化C3P0连接池
     *
     * @param datasource
     * @param properties
     */
    private void initC3P0Factory(Element datasource, Properties properties) {
        C3P0Factory f = new C3P0Factory();
        f.init(properties, datasource.attributeValue(NAME_EXPRESSION));
        DataSource.addDBConnectionFactory(f.getDsName(), f);
    }
}
