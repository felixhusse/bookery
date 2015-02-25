/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.app;

import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import de.fatalix.app.view.home.HomeView;
import de.fatalix.app.view.login.LoginView;
import de.fatalix.app.view.login.UserLoggedInEvent;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.vaadin.cdiviewmenu.ViewMenuUI;

 /*
 *
 * @author Fatalix
 */
@CDIUI("")
@Theme("mytheme")
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
        if (!isLoggedIn()) {
            getMenu().setVisible(false);
            getNavigator().navigateTo(LoginView.id);
        } else {
            getMenu().setVisible(isLoggedIn());
            getMenu().addMenuItem(logout);
            if(getNavigator().getState().isEmpty()) {
                getNavigator().navigateTo(HomeView.id);
            }
        }

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
