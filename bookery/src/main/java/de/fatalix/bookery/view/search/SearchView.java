/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.search;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import de.fatalix.bookery.AppHeader;
import de.fatalix.bookery.SolrSearchUtil;
import de.fatalix.bookery.view.AbstractView;
import de.fatalix.bookery.view.BookSearchLayout;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.vaadin.cdiviewmenu.ViewMenuItem;

/**
 *
 * @author Fatalix
 */
@CDIView(value = SearchView.id,supportsParameters = true)
@ViewMenuItem(title = "Search", icon = FontAwesome.SEARCH, order = ViewMenuItem.DEFAULT)
public class SearchView extends AbstractView implements View {

    public static final String id = "search";
    
    @Inject private AppHeader appHeader;
    @Inject private BookSearchLayout searchLayout;

    
    @PostConstruct
    private void postInit() {
        this.setCompositionRoot(searchLayout);
    }
    
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        SolrQuery.SortClause sortClause = SolrQuery.SortClause.asc("author");
        
        String searchString = SolrSearchUtil.generateSearchString(appHeader.getSearchText());
        
        String[] params = event.getParameters().split("/");
        if (params.length > 0) {
            String sortParameter = params[0];
            switch(sortParameter) {
                case "author":
                    sortClause = SolrQuery.SortClause.asc("author");
                    break;
                case "likes":
                    sortClause = SolrQuery.SortClause.desc("likes");
                    break;
                case "downloads":
                    sortClause = SolrQuery.SortClause.desc("downloadcount");
                    break;
            }
            
            if (params.length > 1) {
                String viewer = SecurityUtils.getSubject().getPrincipal().toString();
                searchString = SolrSearchUtil.addNewBooksSearchString(viewer, searchString);
            }
        }
        SolrQuery query = new SolrQuery();
        query.setRows(20);
        query.setStart(0);
        query.setQuery(searchString);
        query.setSort(sortClause);
        query.setFields(SolrSearchUtil.DEFAULT_FIELDS);
        searchLayout.searchBooks(query,true);
    }
    
   
}
