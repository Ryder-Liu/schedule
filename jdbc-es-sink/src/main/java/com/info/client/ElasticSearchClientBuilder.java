package com.asiainfo.client;

import com.asiainfo.common.Constants;
import com.google.common.base.Throwables;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: flumesinkes
 * @Package: com.ai.renzq.flume.sink.es.client
 * @ClassName: ${TYPE_NAME}
 * @Description:
 * @Author: Renzq
 * @CreateDate: 2018/4/10 22:22
 * @UpdateUser: Renzq
 * @UpdateDate: 2018/4/10 22:22
 * @UpdateRemark:
 * @Version: 1.0
 * Copyright: Copyright (c) 2018
 **/
public class ElasticSearchClientBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchClientBuilder.class);

    private String clusterName;

    private boolean transportSniff;
    private boolean ignoreClusterName;
    private TimeValue transportPingTimeout;
    private TimeValue nodeSamplerInterval;

    private List<TransportAddress> transportAddresses;


    public ElasticSearchClientBuilder(String clusterName, String[] hostname){
        this.clusterName = clusterName;
        setTransportAddresses(hostname);
    }

    public String getClusterName() {
        return clusterName;
    }

    public ElasticSearchClientBuilder setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public boolean isTransportSniff() {
        return transportSniff;
    }

    public ElasticSearchClientBuilder setTransportSniff(boolean transportSniff) {
        this.transportSniff = transportSniff;
        return this;
    }

    public boolean isIgnoreClusterName() {
        return ignoreClusterName;
    }

    public ElasticSearchClientBuilder setIgnoreClusterName(boolean ignoreClusterName) {
        this.ignoreClusterName = ignoreClusterName;
        return this;
    }

    public TimeValue getTransportPingTimeout() {
        return transportPingTimeout;
    }

    public ElasticSearchClientBuilder setTransportPingTimeout(TimeValue transportPingTimeout) {
        this.transportPingTimeout = transportPingTimeout;
        return this;
    }

    public TimeValue getNodeSamplerInterval() {
        return nodeSamplerInterval;
    }

    public ElasticSearchClientBuilder setNodeSamplerInterval(TimeValue nodeSamplerInterval) {
        this.nodeSamplerInterval = nodeSamplerInterval;
        return this;
    }

    public TransportClient build(){
        TransportClient client;

        Settings settings = Settings.builder()
                .put(Constants.ES_ORIGIN_INGORE_CLUSTER_NAME, this.ignoreClusterName)
                .put(Constants.ES_ORIGIN_CLUSTER_NAME, this.clusterName)
                .put(Constants.ES_ORIGIN_TRANSPORT_SNIFF, this.transportSniff)
                .put(Constants.ES_ORIGIN_PING_TIMEOUT, this.transportPingTimeout)
                .put(Constants.ES_ORIGIN_NODE_SAMPLER_INTERVAL, this.nodeSamplerInterval)
                .build();

        client = new PreBuiltTransportClient(settings);
        for (TransportAddress inetSocketTransportAddress : transportAddresses) {
            client.addTransportAddress(inetSocketTransportAddress);
        }
        return client;
    }


    private void setTransportAddresses(String[] transportAddresses) {
        try {
            this.transportAddresses = new ArrayList<TransportAddress>(transportAddresses.length);
            for (String transportAddress : transportAddresses) {
                String hostName = transportAddress.split(":")[0];
                Integer port = transportAddress.split(":").length > 1 ?
                        Integer.parseInt(transportAddress.split(":")[1]) : Constants.DEFAULT_PORT;
                this.transportAddresses.add(new TransportAddress(InetAddress.getByName(hostName), port));
            }
        } catch (Exception e) {
            logger.error("Error in creating the InetAddress for elastic search " + e.getMessage(), e);
            Throwables.propagate(e);
        }
    }
}
