import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.info.common.Constants;

import com.info.source.JDBCSource;
import com.info.source.util.SQLCommand;
import com.info.standard.StandardSource;
import org.apache.http.client.utils.CloneUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * @author liulh
 * @decription
 * @create 2018-05-21 12:40
 **/
public class JDBCSoureTest {

    @Test
    public void JDBCTest() {

        Settings settings1 = Settings.builder()
                .put(Constants.JDBC_URL, "jdbc:oracle:thin:@localhost:1521:XE")
                .put(Constants.JDBC_USER, "aialm")
                .put(Constants.JDBC_PASSWORD, "aialm")
                .put(Constants.JDBC_SQL, "select * from sys_staff")
                .put("index", "sys_staff")
                .put("type", "jdbc")
                .build();
        JDBCSource source = new StandardSource();
        String url = settings1.get("url");
        String user = settings1.get("user");
        String password = settings1.get("password");
        source.setUrl(url)
                .setUser(user)
                .setPassword(password)
                .setStatements(SQLCommand.parse(settings1))
                .setRetries(1)
                .setQueryTimeout(1800)
                .setAutoCommit(true)
                .setMaxRows(20)
                .setFetchSize(10)
                .setMaxRetryWait(TimeValue.timeValueSeconds(30));

        try {
            source.fetch();
            source.shutdown();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void JDBCTest2() {
        String json = "[{\"colName\":\"template_id\",\"colChineseName\":\"模版ID\",\"isId\":true}," +
                "{\"colName\":\"template_name\",\"colChineseName\":\"模版名称\",\"isId\":false}," +
                "{\"colName\":\"template_type\",\"colChineseName\":\"模版类型\",\"isId\":false}," +
                "{\"colName\":\"upload_date\",\"colChineseName\":\"上传时间\",\"isId\":false}]";
        Settings settings = Settings.builder()
                .put(Constants.JDBC_URL, "jdbc:mysql://localhost:3306/report?characterEncoding=utf8&useSSL=true")
                .put(Constants.JDBC_USER, "root")
                .put(Constants.JDBC_PASSWORD, "root")
                .put(Constants.JDBC_SQL, "select * from sa_report_template")
                .put(Constants.JDBC_COLS, json)
                .put(Constants.ES_INDEX_DEFAULT_NAME, "version")
                .put(Constants.ES_INDEX_DEFAULT_TYPE, "jdbc")
                .build();
        JDBCSource source = new StandardSource();
        try {
            source.configure(settings).fetch().shutdown();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parsingJSON () {
        String json = "[{\"colName\":\"xxx\",\"colChineseName\":\"xxxx\",\"isId\":true}," +
                "{\"colName\":\"CCC\",\"colChineseName\":\"CCCCC\",\"isId\":false}]";
        JSONArray jsonArr = JSONArray.parseArray(json);

        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject object = jsonArr.getJSONObject(i);
            for (Map.Entry<String, Object> entry : object.entrySet()) {
                System.out.println((entry.getKey() + " " + entry.getValue()));
            }
        }

        List<Map<String, Object>> chCols = new ArrayList<>();
        Map<String, Object> column;
        JSONArray jsonArr1 = JSONArray.parseArray(json);

        for (int i = 0; i < jsonArr1.size(); i++) {
            JSONObject object = jsonArr1.getJSONObject(i);
            column = new HashMap<>();
            for (Map.Entry<String, Object> entry : object.entrySet()) {
                column.put(entry.getKey(), entry.getValue());
            }
            chCols.add(column);
        }
        System.out.println(chCols.toString());

    }

    @Test
    public void testJava8foreach() {
        List<Map<String, Object>> lists = new ArrayList<>();
        Map<String, Object> map;
        map = new HashMap<>();
        map.put("name", "John");
        map.put("age", "12");
        lists.add(map);
        map = new HashMap<>();
        map.put("name", "Peter");
        map.put("age", "16");
        lists.add(map);

        lists.forEach(list ->
                list.forEach((k, v) -> System.out.println("Item : " + k + " Count : " + v)
                )
        );

        lists.stream()
                .filter(s -> s.containsValue("John"))
                .forEach(m ->
                        System.out.println(m.get("age"))
                );
        Object a = lists.stream()
                .filter(s -> s.containsValue("John")).findFirst().get().get("age");

        String columnName = "John";

        System.out.println(String.valueOf(lists.stream()
                .filter(s -> s.containsValue(columnName)).findFirst().get().get("age")));

        System.out.println(a);
    }
}
