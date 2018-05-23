import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.junit.Assert.fail;

/**
 * @author liulh
 * @decription
 * @create 2018-05-23 10:48
 **/
public class ESClientTest {

    private static final Logger logger = LoggerFactory.getLogger(ESClientTest.class);
    private static TransportClient client;

    @BeforeClass
    public static void Setup() throws UnknownHostException {
        // on startup, create a client.
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
    }

    @AfterClass
    public static void tearDown() {
        if (client != null) {
            // on shutdown
            client.close();
        }
    }



    @Test
    public void testInsertOneDocument() throws UnknownHostException {

        logger.info("start");
        logger.debug(client.settings().toString());

        String json = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";

        IndexResponse response = client.prepareIndex("test", "one", "1")
                .setSource(json, XContentType.JSON)
                .get();

        // Index name
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
        logger.debug("status: {}", status);
    }

    @Test
    public void testGetDocument(){
        logger.debug("start");

        GetResponse response = client.prepareGet("req_link_weekly", "doc", "981").get();
        logger.debug(response.getId());
        logger.debug(response.getSourceAsString());
    }

    @Test
    public void testUpdateDocument() throws IOException {
        logger.debug("start");
        Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, "ctx._source.user = \"new_user\"", new HashMap<>());
        UpdateResponse updateResponse = client.prepareUpdate("test", "one", "1")
                .setScript(script)
                .get();

        client.prepareUpdate("test", "one", "1")
                .setDoc(jsonBuilder()
                        .startObject()
                        .field("gender", "male")
                        .endObject())
                .get();


        IndexRequest indexRequest = new IndexRequest("test", "one", "2")
                .source(jsonBuilder()
                        .startObject()
                        .field("name", "Joe Smith")
                        .field("gender", "male")
                        .endObject());

        client.prepareUpdate("test", "one", "2")
                .setDoc(indexRequest)
                .setUpsert(indexRequest)
                .get();
    }

    @Test
    public void updateNewValueAllInIndex() throws ExecutionException, InterruptedException {

        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
        updateByQuery.source("test")
                .abortOnVersionConflict(false)
                .size(500)
                .script(new Script(ScriptType.INLINE, "painless", "ctx._source.same_name = 'abc'", Collections.emptyMap()));
        BulkByScrollResponse response = updateByQuery.get();

        logger.debug(String.valueOf(response.getStatus()));

    }


    @Test
    public void bulkInertIndexDocument(){

        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

        bulkRequestBuilder.add(
                client.prepareIndex("test2", "doc", "1")
                        .setSource("{\"user\": \"a\"}", XContentType.JSON)
        );

        bulkRequestBuilder.add(
                client.prepareIndex("test2", "doc", "2")
                        .setSource("{\"user\": \"a\"}", XContentType.JSON)
        );

        bulkRequestBuilder.add(
                client.prepareIndex("test2", "doc", "3")
                        .setSource("{\"user\": \"a\"}", XContentType.JSON)
        );

        BulkResponse bulkResponse = bulkRequestBuilder.get();
        if(bulkResponse.hasFailures()){
            logger.error("error");
            fail("the bulk task has failures");
        }

    }

}
