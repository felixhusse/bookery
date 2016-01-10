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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.App;
import de.fatalix.bookery.solr.model.BookEntry;
import de.fatalix.bookery.view.search.SearchView;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author Fatalix
 */
public class SuggestLaneLayout extends VerticalLayout{
    
    private Label titleLabel;
    private Button moreLink;
    private HorizontalLayout layout;
    private String searchLink;
    @Inject private BookDetailDialog bookDetail;
    
    
    @PostConstruct
    private void postInit() {
        setSpacing(true);
        addStyleName("bookery-lane");
        searchLink = SearchView.id;
        titleLabel = new Label("Your Title here..");
        titleLabel.addStyleName(ValoTheme.LABEL_H2);
        
        moreLink = new Button("mehr", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ((App)UI.getCurrent()).getNavigator().navigateTo(searchLink);
            }
        });
        moreLink.addStyleName(ValoTheme.BUTTON_LINK);
        moreLink.addStyleName(ValoTheme.BUTTON_LARGE);
        
        //topLayout.setComponentAlignment(moreLink, Alignment.BOTTOM_CENTER);
        layout = new HorizontalLayout();
        layout.setSpacing(true);
        addComponents(titleLabel,layout);
    }
    
    public void loadLane(String title, List<BookEntry> bookEntries, String searchLink) {
        layout.removeAllComponents();
        titleLabel.setValue(title);
        this.searchLink = searchLink;
        if (bookEntries.isEmpty()) {
            layout.addComponent(createEmptyDummy());
        }
        else {
            for (BookEntry bookEntry : bookEntries) {
                layout.addComponent(createBookCoverLayout(bookEntry));
            }
            layout.addComponent(moreLink);
            layout.setComponentAlignment(moreLink, Alignment.MIDDLE_CENTER);
        }
    }
    
    private VerticalLayout createBookCoverLayout(final BookEntry bookEntry) {
        Image image = new Image();
        image.setDescription(bookEntry.getTitle() + " von " + bookEntry.getAuthor());
        image.setHeight("200px");
        image.setImmediate(true);
        if(bookEntry.getThumbnail() != null) {
            StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getThumbnail());
            image.setSource(new StreamResource(source, bookEntry.getId() + "_thumb.png"));
        } else if(bookEntry.getCover() != null) {
            StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getCover());
            image.setSource(new StreamResource(source, bookEntry.getId() + ".png"));
        }
        
        VerticalLayout result = new VerticalLayout(image);
        result.setHeight("200px");
        result.addStyleName("pointer-cursor");
        result.addStyleName("book-cover");
        result.setComponentAlignment(image, Alignment.MIDDLE_CENTER);

        result.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {

            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                bookDetail.loadData(bookEntry);
                UI.getCurrent().addWindow(bookDetail);
            }
        });
        return result;
    }
    
    
    private VerticalLayout createEmptyDummy() {
        Label label = new Label("hab nix gfundn");
        label.addStyleName(ValoTheme.LABEL_H2);
        label.addStyleName(ValoTheme.LABEL_COLORED);
        label.setSizeUndefined();
        VerticalLayout dummyLayout = new VerticalLayout(label);
        dummyLayout.setHeight("150px");
        dummyLayout.setWidth("800px");
        dummyLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        return dummyLayout;
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
