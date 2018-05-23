package com.asiainfo.client;

import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.asiainfo.common.Constants.*;


/**
 * @ProjectName: flumesinkes
 * @Package: com.ai.renzq.flume.sink.es.client
 * @ClassName: ${TYPE_NAME}
 * @Description:
 * @Author: Renzq
 * @CreateDate: 2018/4/10 23:00
 * @UpdateUser: Renzq
 * @UpdateDate: 2018/4/10 23:00
 * @UpdateRemark:
 * @Version: 1.0
 * Copyright: Copyright (c) 2018
 **/
public class BulkProcessorBuilder {

    private static final Logger logger = LoggerFactory.getLogger(BulkProcessorBuilder.class);

    private Settings settings;


    public BulkProcessor buildBulkProcessor(Settings context, TransportClient client) {
        this.settings = context;
        return this.build(client);
    }


    private BulkProcessor build(TransportClient client) {

        BulkProcessor bulkProcessor = BulkProcessor.builder(
                client,
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId,
                                           BulkRequest request) {
                        logger.debug("Bulk Execution [" + executionId + "]\n" +
                                "No of actions " + request.numberOfActions());
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          BulkResponse response) {
                        logger.debug("Bulk execution completed [" + executionId + "]\n" +
                                "Took (ms): " + response.getIngestTookInMillis() + "\n" +
                                "Failures: " + response.hasFailures() + "\n" +
                                "Failures Message: " + response.buildFailureMessage() + "\n" +
                                "Count: " + response.getItems().length);

                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          Throwable failure) {
                        logger.error("Bulk execution failed [" + executionId + "]" +
                                failure.toString());
                        logger.error(failure.getMessage(), failure);
                    }
                })
                .setBulkActions(settings.getAsInt(ES_BULK_ACTIONS, DEFAULT_ES_BULK_ACTIONS))
                .setBulkSize(new ByteSizeValue(settings.getAsInt(ES_BULK_SIZE, DEFAULT_ES_BULK_SIZE), ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(settings.getAsInt(ES_FLUSH_INTERVAL_TIME, DEFAULT_ES_FLUSH_INTERVAL_TIME)))
                .setConcurrentRequests(settings.getAsInt(ES_CONCURRENT_REQUEST, DEFAULT_ES_CONCURRENT_REQUEST))
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(settings.getAsInt(ES_BACKOFF_POLICY_TIME_INTERVAL, DEFAULT_ES_BACKOFF_POLICY_START_DELAY)),
                                settings.getAsInt(ES_BACKOFF_POLICY_RETRIES, DEFAULT_ES_BACKOFF_POLICY_RETRIES)))
                .build();
        return bulkProcessor;
    }
}
