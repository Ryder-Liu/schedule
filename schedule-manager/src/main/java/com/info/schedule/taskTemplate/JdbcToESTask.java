package com.info.schedule.taskTemplate;

import com.alibaba.fastjson.JSON;
import com.info.common.Constants;
import com.info.source.JDBCSource;
import com.info.standard.StandardSource;
import org.elasticsearch.common.settings.Settings;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author liulh
 * @decription 数据采集任务模板
 * @create 2018-05-21 12:40
 **/
@Component
public class JdbcToESTask {

    public static final String BEAN_NAME = "jdbcToESTask";
    public static final String METHOD_NAME = "jdbcESSink";

    public void jdbcESSink(String param) {
        Settings settings = Settings.builder()
                .put(Constants.JDBC_URL, "jdbc:mysql://localhost:3306/report?characterEncoding=utf8&useSSL=true")
                .put(Constants.JDBC_USER, "root")
                .put(Constants.JDBC_PASSWORD, "root")
                .put(Constants.JDBC_SQL, "select * from sa_report_template")
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

    public void before(){
        System.err.println("before====================================================");
    }

    public void runing(String str){
        System.err.println(str);
    }

    public void after(){
        System.err.println("after====================================================");
    }


}
