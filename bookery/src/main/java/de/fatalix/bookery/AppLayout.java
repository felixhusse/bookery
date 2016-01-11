/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery;

import com.vaadin.cdi.UIScoped;
import com.vaadin.server.Responsive;
import com.vaadin.ui.CssLayout;
import de.fatalix.bookery.view.BookDetailLayout;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 *
 * @author Fatalix
 */
@UIScoped
public class AppLayout extends MVerticalLayout{
    @Inject private AppHeader appHeader;
    private CssLayout content;

    
    @PostConstruct
    private void postInit() {
        setSpacing(false);
        setMargin(false);
        setSizeFull();

        content = new CssLayout();
        content.setPrimaryStyleName("valo-content");
        content.addStyleName("v-scrollable");
        content.setWidth(100, Unit.PERCENTAGE);
        
        addComponents(appHeader,content);
        expand(content);
        addAttachListener(new AttachListener() {

            @Override
            public void attach(AttachEvent event) {
                Responsive.makeResponsive(getUI());
            }
        });
    }
    
    public CssLayout getMainContent() {
        return content;
    }
    
    public AppHeader getAppHeader() {
        return appHeader;
    }
    
}
