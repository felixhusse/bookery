/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.common;

import com.vaadin.cdi.UIScoped;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.log4j.Logger;

/**
 *
 * @author felix.husse
 */
@UIScoped
public class BookMenuLayout extends CssLayout{
    @Inject private Logger logger;
    
    @PostConstruct
    private void postInit() {
        addStyleName("bookery-menu-wrapper");
        addStyleName("bookery-menu");
        Label titleLabel = new Label("Bookery Menu");
        titleLabel.addStyleName(ValoTheme.LABEL_COLORED);
        titleLabel.addStyleName(ValoTheme.LABEL_BOLD);
        
        
        Button cancelButton = new Button("close", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                setLayoutVisible(false);
            }
        });
        cancelButton.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
        cancelButton.addStyleName(ValoTheme.BUTTON_DANGER);
        
        
        VerticalLayout rootLayout = new VerticalLayout(titleLabel,cancelButton);
        rootLayout.setSpacing(true);
        addComponent(rootLayout);   
    }
    
    public void setLayoutVisible(boolean visible) {
        if (visible) {
            addStyleName("visible");
        }
        else {
            removeStyleName("visible");
        }
        setEnabled(visible);
    }
    
}
