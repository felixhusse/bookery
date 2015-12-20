/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.newbooks;

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
import de.fatalix.bookery.view.home.BookDetailLayout;
import de.fatalix.bookery.view.home.HomeView;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.vaadin.cdiviewmenu.ViewMenuItem;

/**
 *
 * @author felixhusse1
 */
@CDIView(NewBooksView.id)
@ViewMenuItem(title = "New Books", icon = FontAwesome.BOOK, order = 2)
public class NewBooksView extends AbstractView implements View {
    public static final String id = "newbooks";
    
    @Inject private NewBooksPresenter presenter;
    
    @Inject
    private Instance<BookDetailLayout> bookDetailLayoutInstances;
    private Label resultLabel;
    
    private HorizontalLayout resultLayout;
    private TextField searchText;
    private Button showMore;
    
    private boolean initPhase;
    
    @PostConstruct
    private void postInit() {
        resultLayout = new HorizontalLayout();
        resultLayout.setSpacing(true);
        resultLayout.addStyleName("wrapping"); 
        showMore = new Button("show more", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (!initPhase) {
                    searchBooks(searchText.getValue(), false);
                }
                
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
        searchText.setIcon(FontAwesome.SEARCH);
        searchText.setImmediate(true);
        searchText.setColumns(20);
        //searchText.addStyleName(ValoTheme.TEXTFIELD_LARGE);
        searchText.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        searchText.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                if (!initPhase) {
                    searchBooks(event.getText(),true);
                }
                
            }
        });
        
        resultLabel = new Label("(0 Books)");
        //resultLabel.addStyleName(ValoTheme.LABEL_HUGE);
        
        HorizontalLayout topSearchLayout = new HorizontalLayout(searchText, resultLabel);
        topSearchLayout.setSpacing(true);
        
        VerticalLayout searchLayout = new VerticalLayout(topSearchLayout);
        searchLayout.setWidth(100, Unit.PERCENTAGE);
        searchLayout.setMargin(true);
        searchLayout.addStyleName("bookery-content");
        return searchLayout;
    }
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        initPhase = true;
        try {
            searchText.setValue("");
            searchBooks(searchText.getValue(),true);
        } catch (Exception ex) {
            Notification.show(ex.getMessage(), Notification.Type.WARNING_MESSAGE);
            Logger.getLogger(NewBooksView.class.getName()).log(Level.SEVERE, null, ex);
        }
        initPhase = false;
        
    }
    
    
    private void searchBooks(String searchWord,boolean reset) {
        try {
            if (reset) {
                resultLayout.removeAllComponents();
            }
            QueryResponse queryResponse = presenter.searchBooks(searchWord,20,resultLayout.getComponentCount(),SecurityUtils.getSubject().getPrincipal().toString());
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
        } catch (IOException ex) {
            Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
