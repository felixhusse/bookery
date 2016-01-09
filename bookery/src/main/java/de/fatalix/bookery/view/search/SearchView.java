/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.search;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.VerticalLayout;
import de.fatalix.bookery.AppHeader;
import de.fatalix.bookery.view.AbstractView;
import de.fatalix.bookery.view.BookSearchLayout;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.vaadin.cdiviewmenu.ViewMenuItem;

/**
 *
 * @author Fatalix
 */
@CDIView(SearchView.id)
@ViewMenuItem(title = "Search", icon = FontAwesome.SEARCH, order = ViewMenuItem.DEFAULT)
public class SearchView extends AbstractView implements View {

    public static final String id = "search";
    
    @Inject private AppHeader appHeader;
    @Inject private BookSearchLayout searchLayout;

    
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
        searchLayout.searchBooks(appHeader.getSearchText(),true);
    }

   
}
