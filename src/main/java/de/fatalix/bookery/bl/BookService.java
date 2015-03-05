/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl;

import com.google.gson.Gson;
import de.fatalix.bookery.bl.elasticsearch.BookEntry;
import de.fatalix.bookery.bl.elasticsearch.ElasticsearchNodeHandler;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;

/**
 *
 * @author felix.husse
 */
@Stateless
public class BookService {
    
    @Inject private ElasticsearchNodeHandler nodeHandler;
    
    public void addBook(BookEntry book) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(book);
        IndexResponse response = nodeHandler.getClient().prepareIndex("bookery", "book").setSource(jsonString).execute().actionGet();
        
        if (response.isCreated()) {
            System.out.println("Book created with ID " + response.getId());
        }
        else {
            System.out.println("Book not created");
        }
    }
    
}
