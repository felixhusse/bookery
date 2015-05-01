/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.home;

import com.vaadin.cdi.CDIView;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.bl.TimeRange;
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
    private OptionGroup timeRangeGroup;
    
    @PostConstruct
    private void postInit() {
        resultLayout = new VerticalLayout();
        resultLayout.setSpacing(true);
        showMore = new Button("show more", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                searchBooks(searchText.getValue(), false,(TimeRange)timeRangeGroup.getValue());
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
                searchBooks(event.getText(),true,(TimeRange)timeRangeGroup.getValue());
            }
        });
        
        resultLabel = new Label("(0 Books)");
        //resultLabel.addStyleName(ValoTheme.LABEL_HUGE);
        
        HorizontalLayout topSearchLayout = new HorizontalLayout(searchText, resultLabel);
        topSearchLayout.setSpacing(true);
        timeRangeGroup = new OptionGroup();
        timeRangeGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        for (TimeRange timeRange : TimeRange.values()) {
            timeRangeGroup.addItem(timeRange);
            timeRangeGroup.setItemCaption(timeRange, timeRange.getCaption());
        }
        timeRangeGroup.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                searchBooks(searchText.getValue(), true, (TimeRange)timeRangeGroup.getValue());
            }
        });
        VerticalLayout searchLayout = new VerticalLayout(topSearchLayout,timeRangeGroup);
        searchLayout.setWidth(100, Unit.PERCENTAGE);
        searchLayout.setMargin(true);
        searchLayout.addStyleName("bookery-content");
        return searchLayout;
    }
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        searchText.setValue("");
        timeRangeGroup.setValue(TimeRange.NONE);
        searchBooks(searchText.getValue(),true, (TimeRange)timeRangeGroup.getValue());
    }

    private void searchBooks(String searchWord,boolean reset,TimeRange timeRange) {
        try {
            if (reset) {
                resultLayout.removeAllComponents();
            }
            QueryResponse queryResponse = presenter.searchBooks(searchWord,10,resultLayout.getComponentCount(),timeRange);
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
