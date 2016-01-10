/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.book.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.solr.client.solrj.SolrServerException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author felix.husse
 */
public class Main {

    
    public static void main(String[] args) throws IOException, SolrServerException {
        //parseDateTime();
//        try {
//            
//            
//            //BookExporterOld.exportBooks("http://jboss.fatalix.de/solr-4.10.4", "bookery", 20, "E:\\dumps\\bookery-web");
//            //BookMigrator.exportBooks("http://jboss.fatalix.de/solr-4.10.4", "bookery", 20, "E:\\dumps\\bookery-web");
            DateTimeZone.setDefault(DateTimeZone.UTC);
            BookMigrator.clearDB("http://localhost:8080/solr-4.10.4", "bookery");
            //List<File> bookFolder = BookMigrator.findAllBooks("E:\\import\\prepared-books");
            //BookMigrator.filterBooks(bookFolder, new File("E:\\import\\fabian-incomplete"));
            //BookMigrator.importBooks("http://localhost:8080/solr-4.10.4", "bookery", 20, bookFolder,true);
            //ThumbnailConvert.createThumbnailForBook("http://localhost:8080/solr-4.10.3", "bookery", "1377ce69-ab5c-40d1-a47c-c71aae8eeace");
 //           ThumbnailConvert.createThumbnails("http://localhost:8080/solr-4.10.3", "bookery", 20);
//            
            //BookMigrator.exportBooks("http://jboss.fatalix.de/solr-4.10.4", "bookery", 40, "C:\\server\\bookery-backup");
//            BookMigrator.importBooks("http://localhost:8080/solr-4.10.4", "bookery", 10, "E:\\dumps\\bookery-web");
//            
//        } catch(SolrServerException | IOException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }
    
    private static void parseDateTime() {
        DateTime dtTime = new DateTime();
        String timeString = dtTime.toString();
        System.out.println(timeString);
        DateTime dtTimeParsed = new DateTime(timeString);
        
        System.out.println(dtTimeParsed);
    }
    
    
}
