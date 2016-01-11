/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view;

import com.vaadin.server.StreamResource;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author felix.husse
 */
public class EbookStreamSource implements StreamResource.StreamSource{
    
    @Inject private Logger logger;
    private final BookDetailPresenter presenter;
    private final BookEntry bookEntry;
    
    public EbookStreamSource(BookDetailPresenter presenter, BookEntry bookEntry) {
        this.presenter = presenter;
        this.bookEntry = bookEntry;
    }
    
    @Override
    public InputStream getStream() { 
        try {
            return new ByteArrayInputStream(presenter.getEbookFile(bookEntry.getId()));
        } catch(SolrServerException ex) {
            logger.error(ex, ex);
        }
        return null;
    }
    
}
