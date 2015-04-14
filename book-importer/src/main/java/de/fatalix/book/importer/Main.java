/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.book.importer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.joda.time.DateTimeZone;

/**
 *
 * @author felix.husse
 */
public class Main {

    
    public static void main(String[] args) {
        try {
            
            DateTimeZone.setDefault(DateTimeZone.UTC);
            //BookExporterOld.exportBooks("http://jboss.fatalix.de/solr-4.10.4", "bookery", 20, "E:\\dumps\\bookery-web");
            //BookMigrator.exportBooks("http://jboss.fatalix.de/solr-4.10.4", "bookery", 20, "E:\\dumps\\bookery-web");
            //BookMigrator.importBooks("http://jboss.fatalix.de/solr-4.10.4", "bookery", 10, "C:\\export");
            
            //BookMigrator.exportBooks("http://localhost:8080/solr-4.10.3", "bookery", 20, "C:\\export2");
            BookMigrator.importBooks("http://localhost:8080/solr-4.10.4", "bookery", 10, "E:\\dumps\\bookery-web");
            
        } catch(SolrServerException | IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
}
