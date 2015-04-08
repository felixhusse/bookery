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
    private BookDetailLayout detailLayout;
    private BeanItemContainer<BookEntry> beanContainer;
    private Label resultLabel;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        VerticalLayout root = new VerticalLayout();
        //root.addStyleName("bookery-screen");
        root.setSpacing(true);
        root.setMargin(true);
        root.addComponents(createSearchLayout(), detailLayout);

        this.setCompositionRoot(root);
    }

    private VerticalLayout createSearchLayout() {
        final TextField searchText = new TextField();
        beanContainer = new BeanItemContainer<>(BookEntry.class);
        resultLabel = new Label("(0)");
        final Table table = new Table();
        table.setContainerDataSource(beanContainer);
        table.setVisibleColumns("author", "title", "releaseDate", "isbn", "uploader");
        table.setColumnHeaders("Author", "Title", "Release", "ISBN", "Uploader");
        table.setSelectable(true);
        table.setSizeFull();
        table.setPageLength(10);

        searchBooks("");
        

        table.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                try {

                    BookEntry bookEntry = presenter.getBookDetail(((BookEntry) table.getValue()).getId());
                    detailLayout.loadData(bookEntry);
                } catch (SolrServerException ex) {
                    Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        searchText.setImmediate(true);
        searchText.addTextChangeListener(new FieldEvents.TextChangeListener() {

            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                searchBooks(event.getText());

            }
        });
        HorizontalLayout topSearchLayout = new HorizontalLayout(searchText, resultLabel);

        VerticalLayout searchLayout = new VerticalLayout(topSearchLayout, table);
        searchLayout.setWidth(100, Unit.PERCENTAGE);
        searchLayout.setMargin(true);
        searchLayout.addStyleName("bookery-content");
        return searchLayout;
    }

    private void searchBooks(String searchWord) {

        try {
            List<BookEntry> bookEntries = presenter.searchBooks(searchWord);
            resultLabel.setValue("(" + bookEntries.size() + ")");
            beanContainer.removeAllItems();
            beanContainer.addAll(bookEntries);
        } catch(SolrServerException ex) {
            Notification.show(ex.getMessage(), Notification.Type.WARNING_MESSAGE);
            Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
