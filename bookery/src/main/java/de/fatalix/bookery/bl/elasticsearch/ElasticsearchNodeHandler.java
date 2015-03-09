/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.elasticsearch;
import java.io.IOException;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.*;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;

import org.elasticsearch.node.Node;
import static org.elasticsearch.river.RiverIndexName.Conf.indexName;

/**
 * Singleton
 * @author felix.husse
 */
@Singleton
public class ElasticsearchNodeHandler {
    
    private Node node;
    private Client client;
    
    @PostConstruct
    private void postInit() {
        node = nodeBuilder().clusterName("bookery-cluster").node();
        client = node.client();
    }
    
    @PreDestroy
    private void onDestroy() {
        System.out.println("Destroying Elasticsearch node...");
        node.close();
    }
    
    public void createDefaultMapping() throws IOException {
        String indexName = "bookery";
        String documentType = "book";
        
        final IndicesExistsResponse res = client.admin().indices().prepareExists(indexName).execute().actionGet();
        if (res.isExists()) {
            final DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(indexName);
            delIdx.execute().actionGet();
        }
//        
//        XContentBuilder mapping = jsonBuilder()
//                              .startObject()
//                                   .startObject("general")
//                                        .startObject("properties")
//                                            .startObject("message")
//                                                .field("type", "string")
//                                                .field("index", "not_analyzed")
//                                             .endObject()
//                                             .startObject("source")
//                                                .field("type","string")
//                                             .endObject()
//                                        .endObject()
//                                    .endObject()
//                                 .endObject();
        
        // Create Index
        final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);
         // MAPPING GOES HERE
        final XContentBuilder mappingBuilder = jsonBuilder()
                .startObject()
                    .startObject(documentType)
                        .startObject("properties")
                            .startObject("author").field("type", "string").endObject()
                            .startObject("title").field("type", "string").endObject()
                            .startObject("isbn").field("type", "string").endObject()
                            .startObject("category").field("type", "string").endObject()
                            .startObject("rating").field("type", "integer").endObject()
                            .startObject("uploader").field("type", "string").endObject()
                            .startObject("reader").field("type", "string").endObject()
                            .startObject("shared").field("type", "string").endObject()
                            .startObject("file").field("type", "binary").endObject()
                        .endObject()
                    .endObject()
                .endObject();
        
        System.out.println(mappingBuilder.string());
        createIndexRequestBuilder.addMapping(documentType, mappingBuilder);
        // MAPPING DONE
        createIndexRequestBuilder.execute().actionGet();
        
    }
    
    public Client getClient() {
        return client;
    }
    
    public boolean isClosed() {
        return node.isClosed();
    }
    
    public Settings getSettings() {
        return node.settings();
    }
    
}
