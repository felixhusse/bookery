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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.apache.shiro.SecurityUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
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
    
    public QueryResponse searchBooks(SolrQuery solrQuery) throws SolrServerException {
        return solrHandler.searchSolr(solrQuery);
    }
    
    public void addBooks(List<BookEntry> bookEntries) throws SolrServerException, IOException {
        solrHandler.addBeans(bookEntries);
    }

    public long getTotalCount(String searchword, TimeRange timeRange) throws SolrServerException {
        String queryString = "*:*";
        if (searchword != null && !searchword.isEmpty()) {
            queryString = "author:*" + searchword + "* OR title:*" + searchword + "*";
        }

        String fields = "id";
        return solrHandler.searchSolrIndex(queryString, fields, 1, 0).getResults().getNumFound();
    }

    public QueryResponse searchBooks(String searchword, int rows, int startOffset, TimeRange timeRange) throws SolrServerException {
        String queryString = "*:*";
        if (searchword != null && !searchword.isEmpty()) {
            queryString = "author:*" + searchword + "* OR title:*" + searchword + "*";
        }

        String timeRangeQueryPart = getTimeRangeQuery(timeRange);
        if (!timeRangeQueryPart.isEmpty()) {
            queryString = queryString + " AND " + timeRangeQueryPart;
        }

        String fields = "id,author,title,isbn,publisher,description,language,releaseDate,likes,downloadcount,uploader,viewed,shared,cover,thumbnail,thumbnailgenerated,likedby";

        return solrHandler.searchSolrIndex(queryString, fields, rows, startOffset);
    }

    public QueryResponse newBooksSearch(String searchword, int rows, int startOffset, String viewer) throws SolrServerException {
        String queryString = "*:*";
        if (searchword != null && !searchword.isEmpty()) {
            queryString = "author:*" + searchword + "* OR title:*" + searchword + "*";
        }

        queryString = queryString + " AND -viewed:" + viewer;

        String fields = "id,author,title,isbn,publisher,description,language,releaseDate,likes,downloadcount,uploader,viewed,shared,cover,thumbnail,thumbnailgenerated,likedby";

        return solrHandler.searchSolrIndex(queryString, fields, rows, startOffset);
    }
    
    public QueryResponse searchBooksSorted(String searchword, int rows, int startOffset, String sortField) throws SolrServerException {
        String queryString = "*:*";
        if (searchword != null && !searchword.isEmpty()) {
            queryString = "author:*" + searchword + "* OR title:*" + searchword + "*";
        }

        String fields = "id,author,title,isbn,publisher,description,language,releaseDate,likes,downloadcount,uploader,viewed,shared,cover,thumbnail,thumbnailgenerated,likedby";

        return solrHandler.searchSolrIndexSorted(queryString, fields, rows, startOffset,sortField);
    }

    private String getTimeRangeQuery(TimeRange timeRange) {

        String result = "";
        switch (timeRange) {
            case LASTMONTH:
                DateTime dtLastMonth = new DateTime(DateTimeZone.UTC).minusMonths(1);
                result = "uploadDate:[" + dtLastMonth + " TO NOW]";
                break;
            case LASTWEEK:
                DateTime dtLastWeek = new DateTime(DateTimeZone.UTC).minusWeeks(1);
                result = "uploadDate:[" + dtLastWeek + " TO NOW]";
                break;
            case SINCELASTLOGIN:
                AppUser user = userService.getAppUser(SecurityUtils.getSubject().getPrincipal().toString());
                if (user.getLastLogin() != null) {
                    DateTime dtLastLogin = new DateTime(user.getLastLogin(), DateTimeZone.UTC);
                    result = "uploadDate:[" + dtLastLogin + " TO NOW]";
                }
                break;
            default:
                break;
        }

        return result;
    }

    public BookEntry updateShared(BookEntry bookEntry, String shared) throws SolrServerException, IOException {
        List<SolrInputDocument> solrDocs = new ArrayList<>();

        boolean newShare = true;
        if (bookEntry.getShared() != null) {
            for (String sharedBy : bookEntry.getShared()) {
                if (sharedBy.equals(shared)) {
                    newShare = false;
                }
            }
        }

        if (newShare) {
            int value = bookEntry.getDownloads() + 1;
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", bookEntry.getId());

            Map<String, Object> viewedData = new HashMap<>();
            viewedData.put("add", shared);
            doc.addField("shared", viewedData);

            Map<String, Object> downloadCount = new HashMap<>();
            downloadCount.put("set", value);
            doc.addField("downloadcount", downloadCount);

            solrDocs.add(doc);
        }
        if (solrDocs.size() > 0) {
            solrHandler.updateDocument(solrDocs);
        }
        return solrHandler.getBookDetail(bookEntry.getId()).get(0);
    }

    public void updateViewed(List<BookEntry> bookEntries, String viewer) throws SolrServerException, IOException {
        List<SolrInputDocument> solrDocs = new ArrayList<>();
        for (BookEntry book : bookEntries) {
            boolean newView = true;
            if (book.getViewed() != null) {
                for (String viewedBy : book.getViewed()) {
                    if (viewedBy.equals(viewer)) {
                        newView = false;
                    }
                }
            }

            if (newView) {
                SolrInputDocument doc = new SolrInputDocument();
                doc.addField("id", book.getId());

                Map<String, Object> viewedData = new HashMap<>();
                viewedData.put("add", viewer);
                doc.addField("viewed", viewedData);
                solrDocs.add(doc);
            }
        }
        if (solrDocs.size() > 0) {
            solrHandler.updateDocument(solrDocs);
        }
    }

    public BookEntry updateLike(BookEntry bookEntry, String viewer) throws SolrServerException, IOException {
                List<SolrInputDocument> solrDocs = new ArrayList<>();

        boolean newLike = true;
        if (bookEntry.getLikedby()!= null) {
            for (String likedBy : bookEntry.getLikedby()) {
                if (likedBy.equals(viewer)) {
                    newLike = false;
                }
            }
        }

        if (newLike) {
            int likes = bookEntry.getLikes()+ 1;
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", bookEntry.getId());

            Map<String, Object> viewedData = new HashMap<>();
            viewedData.put("add", viewer);
            doc.addField("likedby", viewedData);

            Map<String, Object> likeCount = new HashMap<>();
            likeCount.put("set", likes);
            doc.addField("likes", likeCount);

            solrDocs.add(doc);
        }
        else {
            int likes = bookEntry.getLikes()- 1;
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", bookEntry.getId());

            Map<String, Object> viewedData = new HashMap<>();
            viewedData.put("remove", viewer);
            doc.addField("likedby", viewedData);

            Map<String, Object> likeCount = new HashMap<>();
            likeCount.put("set", likes);
            doc.addField("likes", likeCount);

            solrDocs.add(doc);
        }
        
        if (solrDocs.size() > 0) {
            solrHandler.updateDocument(solrDocs);
        }
        return solrHandler.getBookDetail(bookEntry.getId()).get(0);
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
        mailService.scheduleKindleMail(bookFileName, user);
    }
}
