/*
 * Copyright (C) 2014 Jörg Prante
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.asiainfo.source.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.common.Constants;
import org.elasticsearch.common.io.Streams;
import org.elasticsearch.common.settings.Settings;
import org.h2.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.regex.Pattern;

/**
 * The SQL command
 */
public class SQLCommand {

    private String sql;

    private List<Map<String, Object>> chCols;

    private static final Pattern STATEMENT_PATTERN = Pattern.compile("^\\s*(update|insert)", Pattern.CASE_INSENSITIVE);

    private List<Object> params = new LinkedList<>();

    private boolean write;

    public SQLCommand setSQL(String sql) throws IOException {
        if (sql.endsWith(".sql")) {
            Reader r = new InputStreamReader(new FileInputStream(sql), "UTF-8");
            sql = Streams.copyToString(r);
            r.close();
        }
        this.sql = sql;
        return this;
    }

    public String getSQL() {
        return sql;
    }

    public void setChCols(String cols) {
        this.chCols = parseChCols(cols);
    }

    public List<Map<String, Object>> getChCols() {
        return chCols;
    }

    public boolean isWrite() {
        return write;
    }

    public boolean isQuery() {
        if (sql == null) {
            throw new IllegalArgumentException("no SQL found");
        }
        if (write) {
            return false;
        }
        if (STATEMENT_PATTERN.matcher(sql).find()) {
            return false;
        }
        int p1 = sql.toLowerCase().indexOf("select");
        if (p1 < 0) {
            return false;
        }
        int p2 = sql.toLowerCase().indexOf("update");
        if (p2 < 0) {
            return true;
        }
        int p3 = sql.toLowerCase().indexOf("insert");
        return p3 < 0 || p1 < p2 && p1 < p3;
    }


    @SuppressWarnings({"unchecked"})
    public static List<SQLCommand> parse(Settings settings) {
        List<SQLCommand> sqls = new LinkedList<SQLCommand>();
        String sqlStr = settings.get(Constants.JDBC_SQL);
        String cols = settings.get(Constants.JDBC_COLS, "");
        if (StringUtils.isNullOrEmpty(sqlStr)) {
            return sqls;
        }
        List<String> list = Arrays.asList(sqlStr.split(Constants.JDBC_SQL_SEPARATED));
        for (String sql : list) {
            SQLCommand command = new SQLCommand();
            try {
                command.setSQL(sql);
                command.setChCols(cols);
                sqls.add(command);
            } catch (IOException e) {
                throw new IllegalArgumentException("SQL command not found", e);
            }
        }
        return sqls;
    }


    private List<Map<String, Object>> parseChCols (String cols) {
        if (StringUtils.isNullOrEmpty(cols)) {
            return null;
        }
        List<Map<String, Object>> chCols = new ArrayList<>();
        Map<String, Object> column;
        JSONArray jsonArr = JSONArray.parseArray(cols);

        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject object = jsonArr.getJSONObject(i);
            column = new HashMap<>();
            for (Map.Entry<String, Object> entry : object.entrySet()) {
                column.put(entry.getKey(), entry.getValue());
            }
            chCols.add(column);
        }

        return chCols;
    }

    public String toString() {
        return "statement=" + sql + " parameter=" + params + " write=" + write;
    }

}
