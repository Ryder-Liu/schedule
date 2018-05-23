/*
 * Copyright (C) 2015 Jörg Prante
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
package com.asiainfo.source;

import com.asiainfo.source.util.SQLCommand;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * The source models the data producing side
 */
public interface JDBCSource {

    /**
     * Set JDBC config
     *
     * @param url the JDBC connection URL
     * @return this source
     */
    JDBCSource configure(Settings settings);

    /**
     * Set JDBC connection URL
     *
     * @param url the JDBC connection URL
     * @return this source
     */
    JDBCSource setUrl(String url);

    /**
     * Set the user authentication
     *
     * @param user the user
     * @return this source
     */
    JDBCSource setUser(String user);

    /**
     * Set the password authentication
     *
     * @param password the password
     * @return this source
     */
    JDBCSource setPassword(String password);

    /**
     * Set scale of big decimal values.  See java.math.BigDecimal#setScale
     *
     * @param scale the scale of big decimal values
     * @return this context
     */
    JDBCSource setScale(int scale);

    /**
     * Set rounding of big decimal values. See java.math.BigDecimal#setScale
     *
     * @param rounding the rounding of big decimal values
     * @return this context
     */
    JDBCSource setRounding(String rounding);

    /**
     * Set the list of SQL statements
     *
     * @param sql the list of SQL statements
     * @return this context
     */
    JDBCSource setStatements(List<SQLCommand> sql);

    /**
     * Set auto commit
     *
     * @param autocommit true if automatic commit should be performed
     * @return this context
     */
    JDBCSource setAutoCommit(boolean autocommit);

    /**
     * Set max rows
     *
     * @param maxRows max rows
     * @return this context
     */
    JDBCSource setMaxRows(int maxRows);

    /**
     * Set fetch size
     *
     * @param fetchSize fetch size
     * @return this context
     */
    JDBCSource setFetchSize(int fetchSize);

    /**
     * Set retries
     *
     * @param retries number of retries
     * @return this context
     */
    JDBCSource setRetries(int retries);

    /**
     * Set maximum count of retries
     *
     * @param maxretrywait maximum count of retries
     * @return this context
     */
    JDBCSource setMaxRetryWait(TimeValue maxretrywait);
    
    /**
     * Set result set query timeout
     *
     * @param queryTimeout the query timeout in seconds
     * @return this context
     */
    JDBCSource setQueryTimeout(int queryTimeout);

    /**
     * Get a connection for reading data
     *
     * @return connection
     * @throws SQLException when SQL execution gives an error
     */
    Connection getConnectionForReading() throws SQLException;

    /**
     * Get a connection for writing data
     *
     * @return connection
     * @throws SQLException when SQL execution gives an error
     */
    Connection getConnectionForWriting() throws SQLException;


    /**
     * Execute query without binding parameters
     *
     * @param statement the SQL statement
     * @param sql       the SQL query
     * @return the result set
     * @throws SQLException when SQL execution gives an error
     */
    ResultSet executeQuery(Statement statement, String sql) throws SQLException;

    /**
     * Execute insert update
     *
     * @param statement statement
     * @param sql       SQL query
     * @return this source
     * @throws SQLException when SQL execution fails
     */
    JDBCSource executeUpdate(Statement statement, String sql) throws SQLException;


    /**
     * Set the locale
     *
     * @param locale locale
     * @return this source
     */
    JDBCSource setLocale(Locale locale);

    /**
     * Set the timezone for setTimestamp() calls with calendar object.
     *
     * @param timeZone the time zone
     * @return this source
     */
    JDBCSource setTimeZone(TimeZone timeZone);

    /**
     * Close result set
     *
     * @param result result set
     * @return this source
     * @throws SQLException when SQL execution gives an error
     */
    JDBCSource close(ResultSet result) throws SQLException;

    /**
     * Close statement
     *
     * @param statement statement
     * @return this source
     * @throws SQLException when SQL execution gives an error
     */
    JDBCSource close(Statement statement) throws SQLException;

    /**
     * Close reading from this source
     *
     * @return this source
     */
    JDBCSource closeReading();

    /**
     * Close writing to this source
     *
     * @return this source
     */
    JDBCSource closeWriting();

    /**
     * Fetch a data portion from the database and pass it to the task
     * for further processing.
     *
     * @throws Exception when execution gives an error
     */
    JDBCSource fetch() throws SQLException, IOException ;

    /**
     * Shutdown source
     *
     * @throws IOException when shutdown fails
     */
    void shutdown();

}
