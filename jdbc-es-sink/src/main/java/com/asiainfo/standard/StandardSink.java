package com.asiainfo.standard;

import com.asiainfo.client.BulkProcessorBuilder;
import com.asiainfo.client.ElasticSearchClientBuilder;
import com.asiainfo.common.Constants;
import com.asiainfo.common.Utils;
import com.asiainfo.source.Sink;
import com.asiainfo.source.util.IndexableObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.VersionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author liulh
 * @decription 数据插入ES
 * @create 2018-05-21 17:38
 **/
public class StandardSink implements Sink {

    private static final Logger logger = LoggerFactory.getLogger(StandardSink.class);

    private String index;

    private String type;

    private TransportClient client;

    private BulkProcessor bulkProcessor;

    public BulkProcessor getBulkProcessor() {
        return bulkProcessor;
    }

    public TransportClient getClient() {
        return client;
    }

    public void configure(Settings settings) {
        this.index = settings.get(Constants.ES_INDEX_DEFAULT_NAME, Constants.DEFAULT_ES_INDEX);
        this.type = settings.get(Constants.ES_INDEX_DEFAULT_TYPE, Constants.DEFAULT_ES_TYPE);
        //TODO default value should be saved in the contant class.
        String[] hosts = getHosts(settings);
        if(ArrayUtils.isNotEmpty(hosts)) {
            client = new ElasticSearchClientBuilder(
                    settings.get(Constants.ES_CLUSTER_NAME, Constants.DEFAULT_ES_CLUSTER_NAME), hosts)
                    .setTransportSniff(settings.getAsBoolean(
                            Constants.ES_TRANSPORT_SNIFF, Constants.DEFAULT_ES_TRANSPORT_SNIFF))
                    .setIgnoreClusterName(settings.getAsBoolean(
                            Constants.ES_IGNORE_CLUSTER_NAME, Constants.DEFAULT_ES_IGNORE_CLUSTER_NAME))
                    .setTransportPingTimeout(Utils.getTimeValue(settings.get(
                            Constants.ES_TRANSPORT_PING_TIMEOUT), Constants.DEFAULT_ES_TRANSPORT_PING_TIMEOUT))
                    .setNodeSamplerInterval(Utils.getTimeValue(settings.get(
                            Constants.ES_TRANSPORT_NODE_SAMPLER_INTERVAL), Constants.DEFAULT_ES_TRANSPORT_NODE_SAMPLER_INTERVAL))
                    .build();

            bulkProcessor = new BulkProcessorBuilder().buildBulkProcessor(settings, client);
        } else {
            logger.error("Could not create transport client, No host exist");
        }
    }

    public void process(IndexableObject object) {
        if (bulkProcessor == null) {
            return;
        }
        object.index(this.index).type(this.type);

        try {
            UpdateRequest updateRequest = new UpdateRequest(object.index(), object.type(), object.id())
                    .doc(object.build()).upsert(object.build());
            UpdateRequest updateRequestZh = new UpdateRequest(object.index(true), object.type(), object.id())
                    .doc(object.build()).upsert(object.build());
            bulkProcessor.add(updateRequest);
            bulkProcessor.add(updateRequestZh);
        } catch (IOException e) {
            logger.error("es sink is error", e);
        }
    }

    public void shutdown () {
        try {
            if (bulkProcessor != null) {
                bulkProcessor.flush();
                bulkProcessor.close();
            }
            if (client != null) {
                client.close();
            }
        }catch(Throwable t){
            logger.error("close bluk error:" + t.getMessage(), t);
        }
    }

    private String[] getHosts(Settings settings) {
        String[] hosts = null;
        if (StringUtils.isNotBlank(settings.get(Constants.ES_HOSTS, Constants.DEFAULT_ES_HOSTS))) {
            hosts = settings.get(Constants.ES_HOSTS, Constants.DEFAULT_ES_HOSTS).split(",");
        }
        return hosts;
    }
}
