package com.asiainfo.source;

import com.asiainfo.client.BulkProcessorBuilder;
import com.asiainfo.client.ElasticSearchClientBuilder;
import com.asiainfo.common.Constants;
import com.asiainfo.common.Utils;
import com.asiainfo.source.util.IndexableObject;
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
