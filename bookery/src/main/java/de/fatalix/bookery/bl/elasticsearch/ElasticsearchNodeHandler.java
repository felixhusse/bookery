/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.elasticsearch;
import de.fatalix.bookery.bl.dao.AppSettingDAO;
import de.fatalix.bookery.bl.model.SettingKey;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.*;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;

import org.elasticsearch.node.Node;

/**
 * Singleton
 * @author felix.husse
 */
@Singleton
public class ElasticsearchNodeHandler {
    
    private Node node;
    private Client client;
    
    @Inject
    private AppSettingDAO settingDAO;
    
    @PostConstruct
    private void postInit() {
        node = nodeBuilder().clusterName("bookery-cluster").node();
        client = node.client();
        if (!indexExists()) {
            try {
                createIndex();
            } catch(IOException ex) {
                Logger.getLogger(ElasticsearchNodeHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @PreDestroy
    private void onDestroy() {
        System.out.println("Destroying Elasticsearch node...");
        node.close();
    }
    
    public boolean indexExists() {
        String indexName = settingDAO.findByKey(SettingKey.EL_INDEX).getConfigurationValue();
        IndicesExistsResponse res = client.admin().indices().prepareExists(indexName).execute().actionGet();
        return res.isExists();
    }
    
    public boolean createIndex() throws IOException {
        String indexName = settingDAO.findByKey(SettingKey.EL_INDEX).getConfigurationValue();
        String documentType = settingDAO.findByKey(SettingKey.EL_TYPE).getConfigurationValue();
        
        
        if (indexExists()) {
            final DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(indexName);
            delIdx.execute().actionGet();
        }

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
                            .startObject("publisher").field("type", "string").endObject()
                            .startObject("description").field("type", "string").endObject()
                            .startObject("language").field("type", "string").endObject()
                            .startObject("releaseDate").field("type", "string").endObject()
                            .startObject("rating").field("type", "integer").endObject()
                            .startObject("uploader").field("type", "string").endObject()
                            .startObject("reader").field("type", "string").endObject()
                            .startObject("shared").field("type", "string").endObject()
                            .startObject("file").field("type", "binary").endObject()
                            .startObject("cover").field("type", "binary").endObject()
                        .endObject()
                    .endObject()
                .endObject();
        
        System.out.println(mappingBuilder.string());
        createIndexRequestBuilder.addMapping(documentType, mappingBuilder);
        // MAPPING DONE
        CreateIndexResponse response = createIndexRequestBuilder.execute().actionGet();
        return response.isAcknowledged();
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
