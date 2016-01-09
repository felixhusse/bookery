/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.AppHeader;
import de.fatalix.bookery.solr.model.BookEntry;
import de.fatalix.bookery.view.home.HomeView;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 *
 * @author felix.husse
 */
@Default
public class BookSearchLayout extends CustomComponent {

    protected Label resultText;

    @Inject
    protected AppHeader appHeader;
    @Inject
    protected BookSearchPresenter presenter;
    @Inject
    protected Instance<BookDetailLayout> bookDetailLayoutInstances;
    protected HorizontalLayout resultLayout;
    protected Button showMore;

    @PostConstruct
    private void postInit() {
        VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.setWidth(100, Unit.PERCENTAGE);
        rootLayout.setMargin(true);
        rootLayout.addStyleName("bookery-content");
        resultText = new Label(" 0 Ergebnisse gefunden");
        rootLayout.addComponents(resultText,createSearchResultLayout());
        setCompositionRoot(rootLayout);
    }

    private VerticalLayout createSearchResultLayout() {
        resultLayout = new HorizontalLayout();
        resultLayout.setSpacing(true);
        resultLayout.addStyleName("wrapping");
        showMore = new Button("show more", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                searchBooks(appHeader.getSearchText(), false);

            }
        });
        showMore.setWidth(100, Unit.PERCENTAGE);
        showMore.addStyleName(ValoTheme.BUTTON_HUGE);
        showMore.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        VerticalLayout root = new VerticalLayout();
        root.setSpacing(true);
        root.setMargin(true);
        root.addComponents(resultLayout, showMore);
        return root;
    }

    public void searchBooks(String searchWord, boolean reset) {
        try {
            if (reset) {
                resultLayout.removeAllComponents();
            }
            QueryResponse queryResponse = presenter.searchBooks(searchWord, 20, resultLayout.getComponentCount(), SecurityUtils.getSubject().getPrincipal().toString());
            
            if (reset) {
                resultText.setValue(queryResponse.getResults().getNumFound() + " Ergebnisse mit \"" + searchWord +"\" gefunden.");
            }
            
            List<BookEntry> bookEntries = queryResponse.getBeans(BookEntry.class);
            
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
