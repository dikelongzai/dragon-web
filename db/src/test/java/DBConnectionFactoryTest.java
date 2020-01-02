import com.dragon.web.common.security.Log;
import com.dragon.web.db.util.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * {aaa} 表示字符串参数aaa；
 * {n:aaa} 表示数值参数aaa；
 * {dt:aaa} 表示时间日期参数aaa；
 * {dt:'2012-12-21'} 表示固定日期：2012-12-21；
 * {dt[3]:aaa} 表示时间日期参数aaa的日期+3天；
 * {dt[2]:'2012-12-21'} 表示固定日期：2012-12-23；
 * {p:aaa} 表示没有''引起来的字符串参数aaa；
 * {dac:TAB1 a,TAB2 b} 表示控制TAB1和TAB2两个表的数据权限(在SQL中，TAB1的别名为a，TAB2的别名为b)
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-30 17:10
 */
public class DBConnectionFactoryTest {
    public Properties propertiesInitDB = null;
    public DBConnection dbConnection;
    public static final String sqlN = "select * FROM PO_MENU WHERE menuid={n:LAST_UPDATED_BY} ";
    public static final String sqlP = "select * FROM PO_MENU WHERE menuname={} ";
    public static final String sql_MAPPARAM = "select * FROM PO_MENU WHERE menuname={menuname}";
    public static final String sqlP_N = "select * FROM PO_MENU WHERE menuname={menuname} AND menuid={n:menuid}";
    public static final String sqlP_SYSDATE = "select * FROM PO_NEWS WHERE date<{dt:newsdate}";
    public static final String sqlP_SYSDAT = "select * FROM PO_NEWS WHERE date<{dt:sysdate}";

    public static final String sqlP_SYSTIME = "select * FROM PO_NEWS WHERE date<{dt:sysdatetime}";
    public static final String CREATE_SQL="(`runoob_id` INT UNSIGNED AUTO_INCREMENT,\n" +
            "   `runoob_title` VARCHAR(100) NOT NULL,\n" +
            "   `runoob_author` VARCHAR(40) NOT NULL,\n" +
            "   `submission_date` DATE,\n" +
            "   PRIMARY KEY ( `runoob_id` ))";

    public static final String INSERT_SQL="INSERT INTO TEMP_po_tmp_table" +
            "    (runoob_title, runoob_author, submission_date)" +
            "    VALUES" +
            "    ({runoob_title}, {runoob_author}, NOW());";
    public static final String UPDATE_sQL="update TEMP_po_tmp_table set runoob_title={runoob_title},runoob_author={runoob_author} WHERE RUNOOB_ID={n:RUNOOB_ID}";
    //    public ;
    @Before
    public void testInit() {
        propertiesInitDB = new Properties();
        propertiesInitDB.setProperty(DBConstant.JDBC_DRIVER, "com.mysql.jdbc.Driver");
        propertiesInitDB.setProperty(DBConstant.JDBC_URL, "jdbc:mysql://localhost:3306/oa?useUnicode=true&characterEncoding=utf8");
        propertiesInitDB.setProperty(DBConstant.JDBC_USERNAME, "root");
        propertiesInitDB.setProperty(DBConstant.JDBC_PWD, "888888");
        propertiesInitDB.setProperty(DBConstant.CONNECTION_POOL_PARAM_INITPOOLSIZE, "5");
        propertiesInitDB.setProperty(DBConstant.CONNECTION_POOL_PARAM_MAXPOOLSIZE, "5");
        propertiesInitDB.setProperty(DBConstant.CONNECTION_POOL_PARAM_MINPOOLSIZE, "1");
        DBConnectionFactory dbConnectionFactory = new C3P0Factory();
        dbConnectionFactory.init(propertiesInitDB, DBConstant.DEFAULT_DSNAME);
        DataSource.addDBConnectionFactory(DBConstant.DEFAULT_DSNAME, dbConnectionFactory);
        dbConnection = DataSource.getDBConnection(DBConstant.DEFAULT_DSNAME);
    }

    @Test
    public void testGetConnection() {
        dbConnection = DataSource.getDBConnection(DBConstant.DEFAULT_DSNAME);
        try {
            dbConnection.queryBySql("select 1 from dual");
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataSource.removeUsingDBConnection(dbConnection);

    }

    @Test
    public void testQuery() throws Exception {
        dbConnection = DataSource.getDBConnection(DBConstant.DEFAULT_DSNAME);
        Assert.assertEquals(dbConnection.queryDataBySql("SELECT MENUNAME FROM PO_MENU").size(), 85);
        Assert.assertEquals(dbConnection.queryBySql("SELECT * FROM PO_MENU").size(), 85);
        Map map = new HashMap();
        map.put("LAST_UPDATED_BY", 3);
        Assert.assertEquals(dbConnection.queryBySql(sqlN, map).size(), 1);
        List list = new ArrayList();
        list.add("个人办公");
        Assert.assertEquals(dbConnection.queryBySql(sqlP, list).size(), 1);
        map = new HashMap();
        map.put("menuname", "个人办公");
        map.put("menuid", 3);
        Assert.assertEquals(dbConnection.queryBySql(sql_MAPPARAM, map).size(), 1);
        map = new HashMap();
        map.put("menuname", "个人办公");
        map.put("menuid", 3);
        map.put("newsdate", "2019-12-31");
//        Assert.assertEquals(dbConnection.queryBySql(sqlP_N, map).size(), 1);
        map = new HashMap();
        map.put("menuname", "个人办公");
        map.put("menuid", 3);
        map.put("newsdate", "2019-12-31");
        Assert.assertEquals(dbConnection.queryBySql(sqlP_SYSDATE, map).size(), 1);
        Assert.assertEquals(dbConnection.queryBySql(sqlP_SYSDAT, map).size(), 1);
        Assert.assertEquals(dbConnection.queryBySql(sqlP_SYSTIME, null).size(), 1);

    }
    @Test
    public void testCreateDropTable()throws Exception{
        dbConnection.dropTempTable("TEMP_po_tmp_table");
        String tableName=dbConnection.createTempTable("po_tmp_table",CREATE_SQL,false,false);
        Log.debug("success create table "+tableName);

    }
    @Test
    public void testUpdate()throws Exception{
        Map map = new HashMap();
        map.put("runoob_title", "runoob_title");
        map.put("runoob_author", "runoob_author");
        map.put("RUNOOB_ID", 1);

//
//        dbConnection.updateBySql(INSERT_SQL,map);
        List<Map<String,String>> list=dbConnection.queryBySql("SELECT * FROM TEMP_po_tmp_table");

        list.forEach(maptmp->{
            for(Map.Entry entry:maptmp.entrySet()){
                System.out.print("K:"+entry.getKey().toString()+";V:"+entry.getValue().toString());
            }
            System.out.println("");
        });
        map.put("runoob_title", "runoob_title_update");
        map.put("runoob_author", "runoob_author");
        map.put("RUNOOB_ID", 1);
        dbConnection.updateBySql(UPDATE_sQL,map);
    }
}
