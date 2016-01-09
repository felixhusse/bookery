/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery;

import com.vaadin.cdi.UIScoped;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.ShortCutConstants;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.view.login.LoginView;
import de.fatalix.bookery.view.search.SearchView;
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
        add(createSearchBar());
        setSpacing(true);
    }
    
    private HorizontalLayout createTop() {
        Label header = new Label("Bookerys");
        header.addStyleName(ValoTheme.LABEL_BOLD);
        //header.addStyleName(ValoTheme.LABEL_H3);
        header.setSizeUndefined();
        logoutButton = new Button("Hallo", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                ((App)UI.getCurrent()).logout();
            }
        });
        logoutButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        
        MHorizontalLayout layout = new MHorizontalLayout(header,logoutButton);
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setExpandRatio(header, 1.0f);
        layout.setComponentAlignment(header, Alignment.MIDDLE_LEFT);
        layout.setComponentAlignment(logoutButton, Alignment.BOTTOM_RIGHT);
        return layout;
    }
    
    private MHorizontalLayout createSearchBar() {
        searchText = new TextField();
        searchText.setIcon(FontAwesome.SEARCH);
        searchText.addStyleName(ValoTheme.TEXTFIELD_LARGE);
        searchText.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        searchText.setWidth(100, Unit.PERCENTAGE);
        searchText.setInputPrompt("hier einfach suchen..");
        Button searchButton = new Button("such!", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ((App)UI.getCurrent()).getNavigator().navigateTo(SearchView.id);
            }
        });
        searchButton.addStyleName(ValoTheme.BUTTON_LARGE);
        searchText.addShortcutListener(new Button.ClickShortcut(searchButton, ShortcutAction.KeyCode.ENTER));

        MHorizontalLayout layout = new MHorizontalLayout(searchText,searchButton);
        layout.addStyleName("v-component-group");
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setExpandRatio(searchText, 1.0f);
        return layout;
    }
    
    public void setLoginName(String loginName) {
        logoutButton.setCaption("Hallo, " + loginName);
    }
    
    public String getSearchText() {
        return searchText.getValue();
    }
    
    
}
