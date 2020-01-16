package com.dragon.web.core.config;

import com.dragon.web.core.SystemManager;
import com.dragon.web.core.config.db.DataSourceConfig;
import com.dragon.web.db.util.DBConnection;
import com.dragon.web.db.util.DataSource;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-15 13:01
 */
public class DatasourceConfigTest {
    public DBConnection dbConnection;
    @Before
    public void init(){
        SystemManager.setConfigPath("D:\\workspace\\dragon-web\\core\\src\\test\\resources/");
    }
    @Test
    public void testLoadDataSource(){
        DataSourceConfig dataSource=new DataSourceConfig();
        dataSource.load();
        dbConnection = DataSource.getDBConnection("core2");
        try {
            dbConnection.queryBySql("select 1 from dual");
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataSource.removeUsingDBConnection(dbConnection);


    }
}
