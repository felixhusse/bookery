/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.book.importer;

import com.google.gson.Gson;
import de.fatalix.book.importer.old.BookEntry;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

/**
 *
 * @author Fatalix
 */
public class BookExporterOld {
    public static void exportBooks(String solrURL, String solrCore, int batchSize, String exportPath) throws SolrServerException, IOException {
        File exportFolder = new File(exportPath);
        if(!exportFolder.isDirectory()) {
            throw new IOException(exportFolder.getAbsolutePath() + " is not a folder!");
        }

        SolrServer server = SolrHandler.createConnection(solrURL, solrCore);
        System.out.println("Connection established");
        Gson gson = new Gson();

        exportBatchWise(server, exportFolder, batchSize, 0, gson);
    }
    
    private static void exportBatchWise(SolrServer server, File exportFolder, int batchSize, int offset, Gson gson) throws SolrServerException, IOException {

        QueryResponse response = SolrHandler.searchSolrIndex(server, "*:*", batchSize, offset);
        List<BookEntry> bookEntries = response.getBeans(BookEntry.class);
        System.out.println("Retrieved " + (bookEntries.size() + offset) + " of " + response.getResults().getNumFound());
        for(BookEntry bookEntry : bookEntries) {
            String bookTitle = bookEntry.getTitle();
            bookTitle = bookTitle.replaceAll(":", " ");
            File bookFolder = new File(exportFolder, bookEntry.getAuthor() + "-" + bookTitle);
            bookFolder.mkdirs();
            if(bookEntry.getFile() != null && bookEntry.getCover() != null) {
                File bookData = new File(bookFolder, bookEntry.getAuthor() + "-" + bookTitle + ".mobi");
                Files.write(bookData.toPath(), bookEntry.getFile(), StandardOpenOption.CREATE_NEW);

                File coverData = new File(bookFolder, bookEntry.getAuthor() + "-" + bookTitle + ".jpg");
                Files.write(coverData.toPath(), bookEntry.getCover(), StandardOpenOption.CREATE_NEW);
                Date dreleaseDate = null;
                if (bookEntry.getReleaseDate()!=null) {
                    DateTime dtReleaseDate = DateTime.parse(bookEntry.getReleaseDate(), DateTimeFormat.forPattern("YYYY-MM-dd"));
                    dtReleaseDate = new DateTime(dtReleaseDate, DateTimeZone.UTC);
                    dreleaseDate = dtReleaseDate.toDate();
                }
                DateTime dtUploadDate = new DateTime(DateTimeZone.UTC);
                
                File metaDataFile = new File(bookFolder, bookEntry.getAuthor() + "-" + bookTitle + ".json");
                BookMetaData metaData = new BookMetaData(bookEntry.getAuthor(), bookEntry.getTitle(), bookEntry.getIsbn(), 
                                                        bookEntry.getPublisher(), bookEntry.getDescription(), bookEntry.getLanguage(),
                                                        dreleaseDate, bookEntry.getMimeType(),dtUploadDate.toDate());
                gson.toJson(metaData);
                Files.write(metaDataFile.toPath(), gson.toJson(metaData).getBytes(), StandardOpenOption.CREATE_NEW);
            }

        }

        if(response.getResults().getNumFound() > offset) {
            exportBatchWise(server, exportFolder, batchSize, offset + batchSize, gson);
        }

    }
}
