/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.cdi.CDIView;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import de.fatalix.bookery.bl.solr.BookEntry;
import de.fatalix.bookery.view.AbstractView;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrServerException;
import org.vaadin.addons.lazyquerycontainer.EntityContainer;
import org.vaadin.cdiviewmenu.ViewMenuItem;

/**
 *
 * @author Fatalix
 */
@CDIView(HomeView.id)
@ViewMenuItem(title = "Home", icon = FontAwesome.HOME, order = ViewMenuItem.BEGINNING)
public class HomeView extends AbstractView implements View {

    public static final String id = "home";

    @Inject
    private HomePresenter presenter;

    @Inject
    private Instance<BookDetailLayout> bookDetailLayoutInstances;
    private Label resultLabel;
    private VerticalLayout resultLayout;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        resultLayout = new VerticalLayout();
        resultLayout.setSpacing(true);
        VerticalLayout root = new VerticalLayout();
        //root.addStyleName("bookery-screen");
        root.setSpacing(true);
        root.setMargin(true);
        root.addComponents(createSearchLayout(), resultLayout);
        
        this.setCompositionRoot(root);
    }

    private VerticalLayout createSearchLayout() {
        final TextField searchText = new TextField();
        searchText.setImmediate(true);
        searchText.addTextChangeListener(new FieldEvents.TextChangeListener() {

            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                searchBooks(event.getText());
            }
        });
        
        resultLabel = new Label("(0)");
        searchBooks("");

        HorizontalLayout topSearchLayout = new HorizontalLayout(searchText, resultLabel);

        VerticalLayout searchLayout = new VerticalLayout(topSearchLayout);
        searchLayout.setWidth(100, Unit.PERCENTAGE);
        searchLayout.setMargin(true);
        searchLayout.addStyleName("bookery-content");
        return searchLayout;
    }

    private void searchBooks(String searchWord) {

        try {
            List<BookEntry> bookEntries = presenter.searchBooks(searchWord);
            resultLabel.setValue("(" + bookEntries.size() + ")");
            resultLayout.removeAllComponents();
            for (BookEntry bookEntry : bookEntries) {
                BookDetailLayout detailLayout = bookDetailLayoutInstances.get();
                detailLayout.loadData(bookEntry);
                resultLayout.addComponent(detailLayout);
            }
        } catch(SolrServerException ex) {
            Notification.show(ex.getMessage(), Notification.Type.WARNING_MESSAGE);
            Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
