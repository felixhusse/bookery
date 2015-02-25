/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.app;

import javax.servlet.annotation.WebServlet;
import org.vaadin.viewportservlet.ViewPortCDIServlet;

/**
 *
 * @author Fatalix
 */
@WebServlet(urlPatterns = "/*")
public class AppServlet extends ViewPortCDIServlet{
    
}
