/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl;


import de.fatalix.bookery.bl.solr.SolrHandler;
import de.fatalix.bookery.bl.model.AppUser;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.apache.shiro.SecurityUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author felix.husse
 */
@Stateless
public class BookService {

    @Inject
    private SolrHandler solrHandler;
    @Inject
    private BookeryMailService mailService;
    @Inject
    private AppUserService userService;

    public void addBooks(List<BookEntry> bookEntries) throws SolrServerException, IOException {
        solrHandler.addBeans(bookEntries);
    }
    
    public long getTotalCount(String searchword,TimeRange timeRange) throws SolrServerException {
        String queryString = "*:*";
        if(searchword != null && !searchword.isEmpty()) {
            queryString = "author:*" + searchword + "* OR title:*" + searchword + "*";
        }
        
        String fields = "id";
        return solrHandler.searchSolrIndex(queryString, fields, 1, 0).getResults().getNumFound();
    }

    public QueryResponse searchBooks(String searchword, int rows, int startOffset, TimeRange timeRange) throws SolrServerException {
        String queryString = "*:*";
        if(searchword != null && !searchword.isEmpty()) {
            queryString = "author:*" + searchword + "* OR title:*" + searchword + "*";
        }
        
        String timeRangeQueryPart = getTimeRangeQuery(timeRange);
        if (!timeRangeQueryPart.isEmpty()) {
            queryString = queryString + " AND " + timeRangeQueryPart;
        }
        
        String fields = "id,author,title,isbn,publisher,description,language,releaseDate,rating,uploader,cover,reader,shared,thumbnail";

        return solrHandler.searchSolrIndex(queryString, fields, rows, startOffset);
    }
    
    private String getTimeRangeQuery(TimeRange timeRange) {
        
        String result = "";
        switch (timeRange) {
            case LASTMONTH:
                DateTime dtLastMonth = new DateTime(DateTimeZone.UTC).minusMonths(1);
                result = "uploadDate:["+dtLastMonth+" TO NOW]";
                break;
            case LASTWEEK:
                DateTime dtLastWeek = new DateTime(DateTimeZone.UTC).minusWeeks(1);
                result = "uploadDate:["+dtLastWeek+" TO NOW]";
                break;
            case SINCELASTLOGIN:
                AppUser user = userService.getAppUser(SecurityUtils.getSubject().getPrincipal().toString());
                if (user.getLastLogin()!=null) {
                    DateTime dtLastLogin = new DateTime(user.getLastLogin(), DateTimeZone.UTC);
                    result = "uploadDate:["+dtLastLogin+" TO NOW]";
                }
                break;
            default:
                break;
        }
        
        return result;
    }
    
    public BookEntry updateShared(String bookId, String shared) throws SolrServerException {
        
        QueryResponse response = solrHandler.searchSolrIndex("id:" + bookId, "", 1, 0);

        if(response.getResults().getNumFound() == 1) {
            BookEntry bookEntry = response.getBeans(BookEntry.class).get(0);
            ArrayList<String> sharedList = new ArrayList<>();
            if (bookEntry.getShared()!= null) {
                for(String existingReader : bookEntry.getShared()) {
                    if(!existingReader.equals(shared)) {
                        sharedList.add(existingReader);
                    }
                }    
            }
            
            sharedList.add(shared);

            bookEntry.setShared(sharedList.toArray(new String[sharedList.size()]));
            try {
                solrHandler.updateBean(bookEntry);
            } catch(IOException ex) {
                throw new SolrServerException("Could not update Book Entry with ID: " + bookId + " - " + ex.getMessage());
            }
            String fields = "id,author,title,isbn,publisher,description,language,releaseDate,rating,uploader,cover,reader,shared,thumbnail,thumbnailgenerated";
            QueryResponse result = solrHandler.searchSolrIndex("id:" + bookId, fields, 1, 0);
            if(result.getResults().getNumFound() == 1) {
                return result.getBeans(BookEntry.class).get(0);
            } else {
                throw new SolrServerException("Book Entry is not UNIQUE with ID: " + bookId);
            }
        } else {
            throw new SolrServerException("Book Entry is not UNIQUE with ID: " + bookId);
        }
    }
    
    public BookEntry updateViewed(String bookId, String viewer) throws SolrServerException {
        
        QueryResponse response = solrHandler.searchSolrIndex("id:" + bookId, "", 1, 0);

        if(response.getResults().getNumFound() == 1) {
            BookEntry bookEntry = response.getBeans(BookEntry.class).get(0);
            ArrayList<String> viewerList = new ArrayList<>();
            if (bookEntry.getViewed()!= null) {
                for(String existingViewed : bookEntry.getViewed()) {
                    if(!existingViewed.equals(viewer)) {
                        viewerList.add(existingViewed);
                    }
                }    
            }
            
            viewerList.add(viewer);

            bookEntry.setShared(viewerList.toArray(new String[viewerList.size()]));
            try {
                solrHandler.updateBean(bookEntry);
            } catch(IOException ex) {
                throw new SolrServerException("Could not update Book Entry with ID: " + bookId + " - " + ex.getMessage());
            }
            String fields = "id,author,title,isbn,publisher,description,language,releaseDate,rating,uploader,cover,reader,shared,thumbnail,thumbnailgenerated";
            QueryResponse result = solrHandler.searchSolrIndex("id:" + bookId, fields, 1, 0);
            if(result.getResults().getNumFound() == 1) {
                return result.getBeans(BookEntry.class).get(0);
            } else {
                throw new SolrServerException("Book Entry is not UNIQUE with ID: " + bookId);
            }
        } else {
            throw new SolrServerException("Book Entry is not UNIQUE with ID: " + bookId);
        }
    }

    public BookEntry getBookDetail(String id) throws SolrServerException {
        return solrHandler.getBookDetail(id).get(0);
    }
    
    public byte[] getEBookFile(String bookId) throws SolrServerException {
        BookEntry bookFileName = solrHandler.getEpubBook(bookId).get(0);
        return bookFileName.getEpub();
    }

    public void sendBookToKindle(String bookId, AppUser user) throws SolrServerException, MessagingException {
        BookEntry bookFileName = solrHandler.getBookDetail(bookId).get(0);
        String filename = bookFileName.getTitle() + "-" + bookFileName.getAuthor();
        BookEntry bookEntry = solrHandler.getBookData(bookId).get(0);
        mailService.sendKindleMail(user, bookEntry.getMobi(), filename);
    }
}
