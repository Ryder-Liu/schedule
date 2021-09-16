package com.info.source;

import com.info.client.BulkProcessorBuilder;
import com.info.client.ElasticSearchClientBuilder;
import com.info.common.Constants;
import com.info.common.Utils;
import com.info.source.util.IndexableObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;

/**
 * @author liulh
 * @decription
 * @create 2018-05-24 16:51
 **/
public interface Sink {

    BulkProcessor getBulkProcessor();

    TransportClient getClient();

    void configure(Settings settings);

    void process(IndexableObject object);

    void shutdown ();

}
