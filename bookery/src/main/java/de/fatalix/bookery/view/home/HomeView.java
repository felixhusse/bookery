/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.home;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.VerticalLayout;
import de.fatalix.bookery.AppHeader;
import de.fatalix.bookery.solr.model.BookEntry;
import de.fatalix.bookery.view.AbstractView;
import de.fatalix.bookery.view.BookSearchLayout;
import de.fatalix.bookery.view.SuggestLaneLayout;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.vaadin.cdiviewmenu.ViewMenuItem;

/**
 *
 * @author Fatalix
 */
@CDIView(HomeView.id)
@ViewMenuItem(title = "Home", icon = FontAwesome.HOME, order = ViewMenuItem.BEGINNING)
public class HomeView extends AbstractView implements View {

    public static final String id = "home";
    @Inject private HomePresenter presenter;
    @Inject private AppHeader appHeader;
    @Inject private SuggestLaneLayout newBooksLane;
    @Inject private SuggestLaneLayout mostLikedLane;
    @Inject private SuggestLaneLayout mostLoadedLane;

    @PostConstruct
    private void postInit() {
        
        VerticalLayout root = new VerticalLayout();
        root.setSpacing(true);
        root.setMargin(true);
        root.addComponents(newBooksLane,mostLikedLane,mostLoadedLane);
        
        this.setCompositionRoot(root);
    }
    
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        try {
            QueryResponse response = presenter.searchBooks("", 6);
            newBooksLane.loadLane("Neue Bücher", response.getBeans(BookEntry.class));
            response = presenter.searchMostLikedBooks("", 6);
            mostLikedLane.loadLane("Beliebteste Bücher", response.getBeans(BookEntry.class));
            response = presenter.searchMostLoadedBooks("", 6);
            mostLoadedLane.loadLane("Meist geladene Bücher", response.getBeans(BookEntry.class));
            
        } catch (SolrServerException | IOException ex) {
            Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

   
}
