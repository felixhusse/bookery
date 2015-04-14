/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery;

import javax.servlet.annotation.WebServlet;
import org.vaadin.viewportservlet.ViewPortCDIServlet;

/**
 *
 * @author Fatalix
 */
@WebServlet(urlPatterns = "/*")
public class AppServlet extends ViewPortCDIServlet{
    
}
