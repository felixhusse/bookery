/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.app.view;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.ui.CustomComponent;

/**
 *
 * @author Fatalix
 */
public abstract class AbstractView extends CustomComponent implements View{
    
    public AbstractView() {
        setSizeFull();
    }

    @Override
    public String getId() {
        // Use view id/address as the id by default
        CDIView annotation = getClass().getAnnotation(CDIView.class);
        if (annotation != null) {
            return annotation.value();
        }
        return super.getId();
    }
    
}
