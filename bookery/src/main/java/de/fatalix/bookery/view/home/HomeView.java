/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.home;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.SolrSearchUtil;
import de.fatalix.bookery.bl.model.WatchList;
import de.fatalix.bookery.solr.model.BookEntry;
import de.fatalix.bookery.view.common.AbstractView;
import de.fatalix.bookery.view.common.SuggestLaneLayout;
import de.fatalix.bookery.view.search.SearchView;
import de.fatalix.bookery.view.watchlist.WatchListView;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.solr.client.solrj.SolrQuery;
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
    
    @Inject private SuggestLaneLayout newBooksLane;
    @Inject private SuggestLaneLayout watchListLane;
    @Inject private SuggestLaneLayout mostLikedLane;
    @Inject private SuggestLaneLayout mostLoadedLane;
    @Inject private Logger logger;
    
    private Label bookCount = new Label("Zur zeit gibt es 0 Bücher in der Bookery");

    @PostConstruct
    private void postInit() {
        
        VerticalLayout root = new VerticalLayout();
        root.setSpacing(true);
        root.setMargin(true);
        root.addComponents(bookCount,watchListLane,newBooksLane,mostLikedLane,mostLoadedLane);
        root.addStyleName("bookery-view");
        bookCount.addStyleName(ValoTheme.LABEL_BOLD);
        this.setCompositionRoot(root);
    }
    
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        try {
            bookCount.setValue("Aktuell sind "+presenter.getBookCount()+" Bücher in der Bookery");
            
            String searchString = SolrSearchUtil.generateSearchString("");
            
            SolrQuery query = new SolrQuery();
            query.setRows(6);
            query.setStart(0);
            query.setQuery(searchString);
            query.setSort(SolrQuery.SortClause.desc("likes"));
            query.setFields(SolrSearchUtil.DEFAULT_FIELDS);
            
            QueryResponse response = presenter.searchBooks(query);
            mostLikedLane.loadLane("Beliebteste Bücher", response.getBeans(BookEntry.class),SearchView.id + "/likes");
            
            query.setSort(SolrQuery.SortClause.desc("downloadcount"));
            response = presenter.searchBooks(query);
            mostLoadedLane.loadLane("Meist geladene Bücher", response.getBeans(BookEntry.class),SearchView.id + "/downloads");
            
            searchString = SolrSearchUtil.addNewBooksSearchString(SecurityUtils.getSubject().getPrincipal().toString(), searchString);
            query.setQuery(searchString);
            query.setSort(SolrQuery.SortClause.asc("author"));
            response = presenter.searchBooks(query);
            newBooksLane.loadLane("Neue Bücher", response.getBeans(BookEntry.class),SearchView.id + "/auhtor/true");
            
            query = presenter.getWatchListQuery(SecurityUtils.getSubject().getPrincipal().toString());
            if (query != null) {
                query.setRows(6);
                response = presenter.searchBooks(query);
                watchListLane.loadLane("Deine Merkliste", response.getBeans(BookEntry.class), WatchListView.id);
            }
            
        } catch (SolrServerException ex) {
            logger.error(ex,ex);
        }
    }
    

   
}
