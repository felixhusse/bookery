/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl;

import com.google.gson.Gson;
import de.fatalix.bookery.bl.elasticsearch.BookEntry;
import de.fatalix.bookery.bl.elasticsearch.ElasticsearchNodeHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 *
 * @author felix.husse
 */
@Stateless
public class BookService {
    
    @Inject private ElasticsearchNodeHandler nodeHandler;
    
    public boolean addBook(BookEntry book) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(book);
        IndexResponse response = nodeHandler.getClient().prepareIndex("bookery", "book").setSource(jsonString).execute().actionGet();
        
        if (response.isCreated()) {
            System.out.println("Book created with ID " + response.getId());
        }
        else {
            System.out.println("Book not created");
        }
        return response.isCreated();
    }
    
    
    public List<BookEntry> searchBooks(String searchword) {
        System.out.println("Searchword: " + QueryBuilders.multiMatchQuery(searchword, "author","title").toString());
        SearchResponse response = null;
        if (searchword.isEmpty()) {
            response = nodeHandler.getClient()
                            .prepareSearch("bookery")
                            .addFields("author","title")
                            .execute().actionGet();
        }
        else {
            response = nodeHandler.getClient()
                            .prepareSearch("bookery")
                            .setQuery(QueryBuilders.multiMatchQuery(searchword, "author","title").fuzziness(5))
                            .addFields("author","title")
                            .execute().actionGet();
        }
        
        
        ArrayList<BookEntry> bookEntries = new ArrayList<>();
        
        for (SearchHit searchHit : response.getHits().hits()) {
            bookEntries.add(new BookEntry().setAuthor(searchHit.fields().get("author").getValue().toString()).setTitle(searchHit.fields().get("title").getValue().toString()));
        }
        
        return bookEntries;
    }
}
