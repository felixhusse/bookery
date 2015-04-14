/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.cdi.CDIUI;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import de.fatalix.bookery.view.home.HomeView;
import de.fatalix.bookery.view.login.LoginView;
import de.fatalix.bookery.view.login.UserLoggedInEvent;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.vaadin.cdiviewmenu.ViewMenuUI;
import static org.vaadin.cdiviewmenu.ViewMenuUI.getMenu;

 /*
 *
 * @author Fatalix
 */
@CDIUI("")
@Theme("mytheme")
@Title("Bookery")
public class App extends ViewMenuUI{

    private Button logout;
    
    private final Button.ClickListener logoutClickListener = new Button.ClickListener() {
        private static final long serialVersionUID = -1545988729141348821L;

        @Override
        public void buttonClick(Button.ClickEvent event) {
            SecurityUtils.getSubject().logout();
            VaadinSession.getCurrent().close();
            Page.getCurrent().setLocation("");
        }
    };

    @Override
    protected void init(VaadinRequest request) {
        super.init(request);
        logout = new Button("Logout", logoutClickListener);
        logout.setIcon(FontAwesome.SIGN_OUT);
        logout.addStyleName("user-menu");
        getNavigator().addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeListener.ViewChangeEvent event) {
               if (!isLoggedIn()) {
                    getMenu().setVisible(false);
                    if (!event.getViewName().equals(LoginView.id)) {
                        getNavigator().navigateTo(LoginView.id);
                        return false;
                    }
                    return true;
                } else {
                    getMenu().setVisible(isLoggedIn());
                    getMenu().addMenuItem(logout);
                    if(event.getViewName().equals("")) {
                        getNavigator().navigateTo(HomeView.id);
                        return false;
                    }
                    return true;
                }
            }

            @Override
            public void afterViewChange(ViewChangeListener.ViewChangeEvent event) {
                
            }
        });
        

    }

    private boolean isLoggedIn() {
        Subject subject = SecurityUtils.getSubject();
        if (subject == null) {
            System.err.println("Could not find subject");
            return false;
        }

        return subject.isAuthenticated();
    }

    public void userLoggedIn(@Observes(notifyObserver = Reception.IF_EXISTS) UserLoggedInEvent event) {
        Notification.show("Welcome back " + event.getUsername());
        getMenu().navigateTo(HomeView.id);
        getMenu().addMenuItem(logout);
        getMenu().setVisible(isLoggedIn());
    }

    
}
