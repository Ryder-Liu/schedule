import com.asiainfo.common.Constants;
import com.asiainfo.standard.StandardSink;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author liulh
 * @decription
 * @create 2018-05-23 11:00
 **/
public class ESClientTest2 {
    private static final Logger logger = LoggerFactory.getLogger(ESClientTest2.class);
    private static StandardSink sink;

    @BeforeClass
    public static void Setup() throws UnknownHostException {
        sink = new StandardSink();
        Settings settings = Settings.builder().put(Constants.ES_HOSTS, "localhost:9300").build();
        sink.configure(settings);
    }

    @AfterClass
    public static void tearDown() {
//        sink.shutdown();
    }

    @Test
    public void testInsertOneDocument() {

        logger.info("start");

        String json = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        long time = System.currentTimeMillis();
        try {
            for (int i = 0; i < 2000; i ++) {
                logger.info(i + "=================================================");
                sink.getBulkProcessor().add(new IndexRequest("bulk_processor_test" + time, "doc", i + "")
                        .source(jsonBuilder()
                                .startObject()
                                .field("name", "Joe Smith")
                                .field("gender", "male")
                                .endObject()));
            }
        } catch (IOException e) {
            logger.error("es sink is error", e);
        }
        sink.shutdown();
    }

    @Test
    public void testInsertADocument() throws UnknownHostException {

        logger.info("start");
        logger.debug(sink.getClient().settings().toString());

        String json = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        for (int i = 0; i < 20; i ++) {
            IndexResponse response = sink.getClient().prepareIndex("bulk_processor_test2", "doc", i + "")
                    .setSource(json, XContentType.JSON)
                    .get();
        }

/*        // Index name
        String _index = response.getIndex();
        // Type name
        String _type = response.getType();
        // Document ID (generated or not)
        String _id = response.getId();
        // Version (if it's the first time you index this document, you will get: 1)
        long _version = response.getVersion();
        // status has stored current instance statement.
        RestStatus status = response.status();

        logger.debug("_id: {}", _id);
        logger.debug("status: {}", status);*/
    }

}
