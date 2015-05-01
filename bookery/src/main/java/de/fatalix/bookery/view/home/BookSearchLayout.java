/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.event.FieldEvents;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import de.fatalix.bookery.bl.TimeRange;
import javax.annotation.PostConstruct;

/**
 *
 * @author felix.husse
 */
public class BookSearchLayout extends CustomComponent {
    
    private TextField searchText;
    private Label resultLabel;
    private OptionGroup timeRangeGroup;
    
    @PostConstruct
    private void postInit() {
        VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.setWidth(100, Unit.PERCENTAGE);
        rootLayout.setMargin(true);
        rootLayout.addStyleName("bookery-content");
        
        timeRangeGroup = new OptionGroup();
        for (TimeRange timeRange : TimeRange.values()) {
            timeRangeGroup.addItem(timeRange);
            timeRangeGroup.setItemCaption(timeRange, timeRange.getCaption());
        }
        rootLayout.addComponent(createTopSearchLayout());

        setCompositionRoot(rootLayout);
    }
    
    private HorizontalLayout createTopSearchLayout() {
        searchText = new TextField();
        searchText.setImmediate(true);
        searchText.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                //searchBooks(event.getText(),true);
            }
        });
        
        resultLabel = new Label("(0)");
        
        return new HorizontalLayout(searchText, resultLabel);
    }
    
//    private void searchBooks(String searchWord,boolean reset) {
//        try {
//            if (reset) {
//                resultLayout.removeAllComponents();
//            }
//            QueryResponse queryResponse = presenter.searchBooks(searchWord,10,resultLayout.getComponentCount(),TimeRange.NONE);
//            List<BookEntry> bookEntries = queryResponse.getBeans(BookEntry.class);
//            resultLabel.setValue("(" + queryResponse.getResults().getNumFound()+ ")");
//            
//            for (BookEntry bookEntry : bookEntries) {
//                BookDetailLayout detailLayout = bookDetailLayoutInstances.get();
//                detailLayout.loadData(bookEntry);
//                resultLayout.addComponent(detailLayout);
//            }
//            showMore.setEnabled(queryResponse.getResults().getNumFound() > resultLayout.getComponentCount());
//            
//        } catch(SolrServerException ex) {
//            Notification.show(ex.getMessage(), Notification.Type.WARNING_MESSAGE);
//            Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//    }
    
}
