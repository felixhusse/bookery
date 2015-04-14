/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

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

    public void addBooks(List<BookEntry> bookEntries) throws SolrServerException, IOException {
        solrHandler.addBeans(bookEntries);
    }

    public QueryResponse searchBooks(String searchword, int rows, int startOffset) throws SolrServerException {
        String queryString = "*:*";
        if(searchword != null && !searchword.isEmpty()) {
            queryString = "author:*" + searchword + "* OR title:*" + searchword + "*";
        }
        String fields = "id,author,title,isbn,publisher,description,language,releaseDate,rating,uploader,cover,reader,shared";

        return solrHandler.searchSolrIndex(queryString, fields, rows, startOffset);
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
            String fields = "id,author,title,isbn,publisher,description,language,releaseDate,rating,uploader,cover,reader,shared";
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
    
    public BookEntry updateReader(String bookId, String reader) throws SolrServerException {
        String fields = "id,author,title,isbn,publisher,description,language,releaseDate,rating,uploader,cover,reader,mimetype,shared,file";
        QueryResponse response = solrHandler.searchSolrIndex("id:" + bookId, "", 1, 0);

        if(response.getResults().getNumFound() == 1) {
            BookEntry bookEntry = response.getBeans(BookEntry.class).get(0);
            ArrayList<String> readerList = new ArrayList<>();
            if (bookEntry.getReader() != null) {
                for(String existingReader : bookEntry.getReader()) {
                    if(!existingReader.equals(reader)) {
                        readerList.add(existingReader);
                    }
                }    
            }
            
            readerList.add(reader);

            bookEntry.setReader(readerList.toArray(new String[readerList.size()]));
            try {
                solrHandler.updateBean(bookEntry);
            } catch(IOException ex) {
                throw new SolrServerException("Could not update Book Entry with ID: " + bookId + " - " + ex.getMessage());
            }
            fields = "id,author,title,isbn,publisher,description,language,releaseDate,rating,uploader,cover,reader";
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

    public void sendBookToKindle(String bookId, AppUser user) throws SolrServerException, MessagingException {
        BookEntry bookFileName = solrHandler.getBookDetail(bookId).get(0);
        String filename = bookFileName.getTitle() + "-" + bookFileName.getAuthor();
        BookEntry bookEntry = solrHandler.getBookData(bookId).get(0);
        mailService.sendKindleMail(user, bookEntry.getFile(), filename);
    }
}
