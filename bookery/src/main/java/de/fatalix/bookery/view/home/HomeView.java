/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.cdi.CDIView;
import com.vaadin.event.FieldEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.solr.model.BookEntry;
import de.fatalix.bookery.view.AbstractView;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
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

    @Inject
    private HomePresenter presenter;

    @Inject
    private Instance<BookDetailLayout> bookDetailLayoutInstances;
    private Label resultLabel;
    private VerticalLayout resultLayout;
    private TextField searchText;
    private Button showMore;
    
    @PostConstruct
    private void postInit() {
        resultLayout = new VerticalLayout();
        resultLayout.setSpacing(true);
        showMore = new Button("show more", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                searchBooks(searchText.getValue(), false);
            }
        });
        showMore.setWidth(100, Unit.PERCENTAGE);
        showMore.addStyleName(ValoTheme.BUTTON_HUGE);
        showMore.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        VerticalLayout root = new VerticalLayout();
        root.setSpacing(true);
        root.setMargin(true);
        root.addComponents(createSearchLayout(), resultLayout,showMore);
        
        this.setCompositionRoot(root);
    }
    
    private VerticalLayout createSearchLayout() {
        searchText = new TextField();
        searchText.setImmediate(true);
        searchText.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                searchBooks(event.getText(),true);
            }
        });
        
        resultLabel = new Label("(0)");
        
        HorizontalLayout topSearchLayout = new HorizontalLayout(searchText, resultLabel);
        VerticalLayout searchLayout = new VerticalLayout(topSearchLayout);
        searchLayout.setWidth(100, Unit.PERCENTAGE);
        searchLayout.setMargin(true);
        searchLayout.addStyleName("bookery-content");
        return searchLayout;
    }
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        searchText.setValue("");
        searchBooks(searchText.getValue(),true);
    }

    private void searchBooks(String searchWord,boolean reset) {
        try {
            if (reset) {
                resultLayout.removeAllComponents();
            }
            QueryResponse queryResponse = presenter.searchBooks(searchWord,10,resultLayout.getComponentCount());
            List<BookEntry> bookEntries = queryResponse.getBeans(BookEntry.class);
            resultLabel.setValue("(" + queryResponse.getResults().getNumFound()+ ")");
            
            for (BookEntry bookEntry : bookEntries) {
                BookDetailLayout detailLayout = bookDetailLayoutInstances.get();
                detailLayout.loadData(bookEntry);
                resultLayout.addComponent(detailLayout);
            }
            showMore.setEnabled(queryResponse.getResults().getNumFound() > resultLayout.getComponentCount());
            
        } catch(SolrServerException ex) {
            Notification.show(ex.getMessage(), Notification.Type.WARNING_MESSAGE);
            Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
