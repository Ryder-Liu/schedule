import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.common.Constants;

import com.asiainfo.source.JDBCSource;
import com.asiainfo.source.util.SQLCommand;
import com.asiainfo.standard.StandardSource;
import org.apache.http.client.utils.CloneUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Settings settings = Settings.builder()
                .put(Constants.JDBC_URL, "jdbc:mysql://localhost:3306/report?characterEncoding=utf8&useSSL=true")
                .put(Constants.JDBC_USER, "root")
                .put(Constants.JDBC_PASSWORD, "root")
                .put(Constants.JDBC_SQL, "select * from sa_report_template")
                .put(Constants.JDBC_COLS, "[{\"colName\":\"xxx\",\"colChineseName\":\"xxxx\",\"isId\":true}]")
                .put("index", "sa_report_template")
                .put("type", "jdbc")
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
}
