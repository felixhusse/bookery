/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.server.StreamResource;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author felix.husse
 */
public class EbookStreamSource implements StreamResource.StreamSource{

    private final HomePresenter presenter;
    private final BookEntry bookEntry;
    
    public EbookStreamSource(HomePresenter presenter, BookEntry bookEntry) {
        this.presenter = presenter;
        this.bookEntry = bookEntry;
    }
    
    @Override
    public InputStream getStream() { 
        try {
            return new ByteArrayInputStream(presenter.getEbookFile(bookEntry.getId()));
        } catch(SolrServerException ex) {
            Logger.getLogger(EbookStreamSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
