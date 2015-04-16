/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import javax.annotation.PostConstruct;

/**
 *
 * @author felix.husse
 */
public class BookSearchLayout extends CustomComponent {
    
    @PostConstruct
    private void postInit() {
        VerticalLayout rootLayout = new VerticalLayout();
        
        rootLayout.setWidth(100, Unit.PERCENTAGE);
        rootLayout.setMargin(true);
        rootLayout.addStyleName("bookery-content");
        
        setCompositionRoot(rootLayout);
    }
    
}
