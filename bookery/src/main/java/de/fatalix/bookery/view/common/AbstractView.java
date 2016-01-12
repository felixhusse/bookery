/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.common;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.ui.CustomComponent;

/**
 *
 * @author Fatalix
 */
public abstract class AbstractView extends CustomComponent implements View{
    
    public AbstractView() {
        //setSizeFull();
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
