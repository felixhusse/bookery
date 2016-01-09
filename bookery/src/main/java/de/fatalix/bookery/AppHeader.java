/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery;

import com.vaadin.cdi.UIScoped;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import javax.annotation.PostConstruct;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 *
 * @author Fatalix
 */
@UIScoped
public class AppHeader extends MVerticalLayout{
    
    private TextField searchText;
    private Button logoutButton;
    
    @PostConstruct
    private void postInit() {
        addStyleName("bookery-header");
        setWidth(100, Unit.PERCENTAGE);
        add(createTop());
        searchText = new TextField();
        searchText.setIcon(FontAwesome.SEARCH);
        searchText.addStyleName(ValoTheme.TEXTFIELD_LARGE);
        searchText.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        searchText.setWidth(100, Unit.PERCENTAGE);
        add(searchText);
        setSpacing(true);
    }
    
    private HorizontalLayout createTop() {
        Label header = new Label("<h3>Bookerys</h3>", ContentMode.HTML);
        header.setSizeUndefined();
        logoutButton = new Button("Hallo", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                ((App)UI.getCurrent()).logout();
            }
        });
        
        MHorizontalLayout layout = new MHorizontalLayout(header,logoutButton);
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setExpandRatio(header, 1.0f);
        layout.setComponentAlignment(header, Alignment.MIDDLE_LEFT);
        layout.setComponentAlignment(logoutButton, Alignment.BOTTOM_RIGHT);
        return layout;
    }
    
    public void setLoginName(String loginName) {
        
        logoutButton.setCaption("Hallo, " + loginName);
    }
    
    
}
