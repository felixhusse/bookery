/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.elasticsearch;

import static org.elasticsearch.node.NodeBuilder.*;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;

import org.elasticsearch.node.Node;

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
        
        client.admin().indices().delete(new DeleteIndexRequest("bookery"));
        
        CreateIndexRequest createIndex = new CreateIndexRequest("bookery");
        
        client.admin().indices().create(new CreateIndexRequest("bookery"), new ActionListener<CreateIndexResponse>() {

            @Override
            public void onResponse(CreateIndexResponse response) {
                
            }

            @Override
            public void onFailure(Throwable e) {
                
            }
        });
        //PutMappingRequest mappingRequest = new PutMappingRequest("bookery").type("book").
        //client.admin().indices().putMapping(null);
    }
    
    @PreDestroy
    private void onDestroy() {
        System.out.println("Destroying Elasticsearch node...");
        node.close();
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
