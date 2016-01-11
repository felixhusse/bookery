/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.AppHeader;
import de.fatalix.bookery.solr.model.BookEntry;
import de.fatalix.bookery.view.home.HomeView;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.solr.client.solrj.SolrQuery;
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
    private Logger logger;
    
    @Inject
    protected AppHeader appHeader;
    @Inject
    protected BookSearchPresenter presenter;
    
    @Inject private Instance<BookDetailDialog> bookDetail;
    
    @Inject
    protected BookDetailLayout bookDetailLayout;

    protected HorizontalLayout resultLayout;
    protected Button showMore;
    protected SolrQuery query;
    
    @PostConstruct
    private void postInit() {
        
        CssLayout rootLayout = new CssLayout();
        rootLayout.setSizeFull();
        //rootLayout.addStyleName("crud-view");
        rootLayout.addComponents(createSearchResultLayout());
        bookDetailLayout.setLayoutVisible(false);
        setCompositionRoot(rootLayout);
    }

    private VerticalLayout createSearchResultLayout() {
        resultText = new Label(" 0 Ergebnisse gefunden");
        resultLayout = new HorizontalLayout();
        resultLayout.setSpacing(true);
        resultLayout.addStyleName("wrapping");
        showMore = new Button("show more", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                searchBooks(query, false);

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

    public void searchBooks(SolrQuery query, boolean reset) {
        try {
            String user = SecurityUtils.getSubject().getPrincipals().toString();
            this.query = query;
            if (reset) {
                resultLayout.removeAllComponents();
            }
            query.setStart(resultLayout.getComponentCount());
            QueryResponse queryResponse = presenter.searchBooks(query,user);

            if (reset) {
                resultText.setValue(queryResponse.getResults().getNumFound() + " Ergebnisse mit \"" + appHeader.getSearchText() +"\" gefunden.");
            }
            
            List<BookEntry> bookEntries = queryResponse.getBeans(BookEntry.class);
            
            for (BookEntry bookEntry : bookEntries) {
//                BookDetailLayout detailLayout = bookDetailLayoutInstances.get();
//                detailLayout.loadData(bookEntry);
                resultLayout.addComponent(createBookCoverLayout(bookEntry));
            }
            showMore.setEnabled(queryResponse.getResults().getNumFound() > resultLayout.getComponentCount());

        } catch (SolrServerException ex) {
            Notification.show(ex.getMessage(), Notification.Type.WARNING_MESSAGE);
            logger.error(ex, ex);
        }

    }
    
    private VerticalLayout createBookCoverLayout(final BookEntry bookEntry) {
        Image image = new Image();
        image.setDescription(bookEntry.getTitle() + " von " + bookEntry.getAuthor());
        image.setHeight("200px");
        image.setWidth("130px");
        image.setImmediate(true);
        if(bookEntry.getThumbnail() != null) {
            StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getThumbnail());
            image.setSource(new StreamResource(source, bookEntry.getId() + "_thumb.png"));
        } else if(bookEntry.getCover() != null) {
            StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getCover());
            image.setSource(new StreamResource(source, bookEntry.getId() + ".png"));
        }
        
        VerticalLayout result = new VerticalLayout(image);
        result.setHeight("210px");
        result.setWidth("140px");
        result.addStyleName("pointer-cursor");
        result.addStyleName("book-cover");
        result.setComponentAlignment(image, Alignment.MIDDLE_CENTER);

        result.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {

            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                bookDetailLayout.loadData(bookEntry);
                bookDetailLayout.setLayoutVisible(true);
                //BookDetailDialog dialogInstance = bookDetail.get();
                //dialogInstance.loadData(bookEntry);
                //UI.getCurrent().addWindow(dialogInstance);
            }
        });
        return result;
    }
    
    protected class ByteStreamResource implements StreamResource.StreamSource {

        private final ByteArrayInputStream imageBuffer;

        public ByteStreamResource(byte[] data) {
            imageBuffer = new ByteArrayInputStream(data);
        }

        @Override
        public InputStream getStream() {
            return imageBuffer;
        }

    }

}
