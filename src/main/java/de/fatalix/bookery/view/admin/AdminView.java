/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.admin;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import de.fatalix.bookery.view.AbstractView;
import javax.annotation.security.RolesAllowed;
import org.vaadin.cdiviewmenu.ViewMenuItem;

/**
 *
 * @author felix.husse
 */
@CDIView(AdminView.id)
@RolesAllowed({"admin"})
@ViewMenuItem(title = "Admin",icon = FontAwesome.GEARS,order = ViewMenuItem.END)
public class AdminView extends AbstractView {
    
    public static final String id = "admin";

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        VerticalLayout root = new VerticalLayout();
        root.addComponent(new Label("Admin"));
        this.setCompositionRoot(root);
    }
    
    
    
}
