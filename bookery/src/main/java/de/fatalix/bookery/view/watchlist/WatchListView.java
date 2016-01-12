/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.watchlist;


import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import de.fatalix.bookery.AppHeader;
import de.fatalix.bookery.SolrSearchUtil;
import de.fatalix.bookery.view.common.AbstractView;
import de.fatalix.bookery.view.common.BookSearchLayout;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.vaadin.cdiviewmenu.ViewMenuItem;

/**
 *
 * @author Fatalix
 */
@CDIView(value = WatchListView.id,supportsParameters = true)
@ViewMenuItem(title = "Watchlist", icon = FontAwesome.STAR, order = ViewMenuItem.DEFAULT)
public class WatchListView extends AbstractView implements View {

    public static final String id = "watch";
    
    @Inject private AppHeader appHeader;
    @Inject private BookSearchLayout searchLayout;
    @Inject private WatchListPresenter presenter;
    
    @PostConstruct
    private void postInit() {
        this.setCompositionRoot(searchLayout);
    }
    
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        SolrQuery query = presenter.getWatchListQuery(SecurityUtils.getSubject().getPrincipal().toString());
        if (query != null) {
            searchLayout.searchBooks(query, true);
        }
    }
    
   
}
