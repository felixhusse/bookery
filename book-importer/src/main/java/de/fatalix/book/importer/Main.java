/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.book.importer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author felix.husse
 */
public class Main {
    public static void main(String[] args) {
        try {
            //BookMigrator.exportBooks("http://jboss.fatalix.de/solr-4.10.4", "bookery", 20, "C:\\export");
            //http://localhost:8080/solr-4.10.3
            //http://jboss.fatalix.de/solr-4.10.4
            BookMigrator.importBooks("http://localhost:8080/solr-4.10.3", "bookery", 10, "C:\\export");
        } catch(SolrServerException | IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
}
