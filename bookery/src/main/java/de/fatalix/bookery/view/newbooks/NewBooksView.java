/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.newbooks;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.VerticalLayout;
import de.fatalix.bookery.AppHeader;
import de.fatalix.bookery.SolrSearchUtil;
import de.fatalix.bookery.view.AbstractView;
import de.fatalix.bookery.view.BookSearchLayout;
import de.fatalix.bookery.view.ComponentType;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.vaadin.cdiviewmenu.ViewMenuItem;

/**
 *
 * @author felixhusse1
 */
@CDIView(NewBooksView.id)
@ViewMenuItem(title = "New Books", icon = FontAwesome.BOOK, order = 2)
public class NewBooksView extends AbstractView implements View {
    public static final String id = "newbooks";
    
    @Inject private AppHeader appHeader;
    
    @Inject 
    @ComponentType.NewBooksSearch
    private BookSearchLayout searchLayout;

    
    @PostConstruct
    private void postInit() {
        VerticalLayout root = new VerticalLayout();
        root.setSpacing(true);
        root.setMargin(true);
        root.addComponents(searchLayout);
        
        this.setCompositionRoot(root);
    }
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        String viewer = SecurityUtils.getSubject().getPrincipal().toString();
        String searchString = SolrSearchUtil.generateSearchString(appHeader.getSearchText());
        searchString = SolrSearchUtil.addNewBooksSearchString(viewer, searchString);
        SolrQuery query = new SolrQuery();
        query.setRows(20);
        query.setStart(0);
        query.setQuery(searchString);
        query.setSort(SolrQuery.SortClause.asc("author"));
        query.setFields(SolrSearchUtil.DEFAULT_FIELDS);
        searchLayout.searchBooks(query,true);
    }
    
}
