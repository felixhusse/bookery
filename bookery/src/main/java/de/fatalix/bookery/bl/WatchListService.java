/*
 * Copyright (c) 2016 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl;

import de.fatalix.bookery.SolrSearchUtil;
import de.fatalix.bookery.bl.dao.WatchListDAO;
import de.fatalix.bookery.bl.model.AppUser;
import de.fatalix.bookery.bl.model.WatchList;
import de.fatalix.bookery.solr.model.BookEntry;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author felix.husse
 */
@Stateless
public class WatchListService {
    
    @Inject private WatchListDAO watchListDAO;
    @Inject private AppUserService userService;
    @Inject private BookService bookService;
    
    public List<WatchList> getUserWatchList(String username) {
        return watchListDAO.findByUserName(username);
    }
    
    public void addToWatchList(String username, String bookID) {
        AppUser appUser = userService.getAppUser(username);
        
        WatchList watchList = new WatchList();
        watchList.setBookId(bookID);
        watchList.setUser(appUser);
        watchList.setWatchDate(new Date());
        
        watchListDAO.save(watchList);
    }
    
    public boolean isOnWatchList(String username, String bookID) {
        return watchListDAO.findByUserNameAndBookID(username, bookID)!=null;
    }
    
    public void removeFromWatchList(String username, String bookID) {
        if (isOnWatchList(username, bookID)) {
            removeFromWatchList(watchListDAO.findByUserNameAndBookID(username, bookID));
        }
    }
    
    public void removeFromWatchList(WatchList watchList) {
        watchListDAO.delete(watchList.getId());
    }
    
    public List<BookEntry> getAllBooks(List<WatchList> watchList) throws SolrServerException {
        if (watchList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        String searchString = "id:(";
        for (WatchList watchListItem : watchList) {
            searchString = searchString + "\"" +watchListItem.getBookId() +"\",";
        }
        searchString = searchString.substring(0, searchString.length()-1) + ")";
        
        SolrQuery query = new SolrQuery();
        query.setRows(20);
        query.setStart(0);
        query.setQuery(searchString);
        query.setSort(SolrQuery.SortClause.asc("author"));
        query.setFields(SolrSearchUtil.DEFAULT_FIELDS);
        
        return bookService.searchBooks(query).getBeans(BookEntry.class);
        
    }
    
    public SolrQuery getSolrQuery(List<WatchList> watchList) {
        if (watchList.isEmpty()) {
            return null;
        }
        String searchString = "id:(";
        for (WatchList watchListItem : watchList) {
            searchString = searchString + "\"" +watchListItem.getBookId() +"\",";
        }
        searchString = searchString.substring(0, searchString.length()-1) + ")";
        
        SolrQuery query = new SolrQuery();
        query.setRows(20);
        query.setStart(0);
        query.setQuery(searchString);
        query.setSort(SolrQuery.SortClause.asc("author"));
        query.setFields(SolrSearchUtil.DEFAULT_FIELDS);
        return query;
    }
}
