/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.app.view.home;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import de.fatalix.app.view.AbstractView;
import org.vaadin.cdiviewmenu.ViewMenuItem;

/**
 *
 * @author Fatalix
 */
@CDIView(HomeView.id)
@ViewMenuItem(title = "Home",icon = FontAwesome.HOME)
public class HomeView extends AbstractView implements View{
    public static final String id = "home";

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        VerticalLayout root = new VerticalLayout();
        
        root.addComponent(new Label("Home"));
        
        this.setCompositionRoot(root);
    }
}
