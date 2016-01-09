/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view;

import com.vaadin.ui.Notification;
import de.fatalix.bookery.solr.model.BookEntry;
import de.fatalix.bookery.view.home.HomeView;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 *
 * @author Fatalix
 */
@ComponentType.NewBooksSearch
public class NewBooksSearchLayout extends BookSearchLayout{

    @Override
    public void searchBooks(String searchWord, boolean reset) {
        try {
            if (reset) {
                resultLayout.removeAllComponents();
            }
            String user = SecurityUtils.getSubject().getPrincipal().toString();
            QueryResponse queryResponse = presenter.searchNewBooks(searchWord, 20, resultLayout.getComponentCount(), user);
            
            if (reset) {
                resultText.setValue(queryResponse.getResults().getNumFound() + " Ergebnisse mit \"" + searchWord +"\" gefunden.");
            }
            List<BookEntry> bookEntries = queryResponse.getBeans(BookEntry.class);
            presenter.updateViewed(bookEntries, user);
            
            for (BookEntry bookEntry : bookEntries) {
                BookDetailLayout detailLayout = bookDetailLayoutInstances.get();
                detailLayout.loadData(bookEntry);
                resultLayout.addComponent(detailLayout);
            }
            showMore.setEnabled(queryResponse.getResults().getNumFound() > resultLayout.getComponentCount());

        } catch (SolrServerException ex) {
            Notification.show(ex.getMessage(), Notification.Type.WARNING_MESSAGE);
            Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
