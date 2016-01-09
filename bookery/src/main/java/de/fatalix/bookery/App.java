/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import de.fatalix.bookery.bl.AppUserService;
import de.fatalix.bookery.bl.model.AppUser;
import de.fatalix.bookery.view.home.HomeView;
import de.fatalix.bookery.view.login.LoginView;
import de.fatalix.bookery.view.login.UserLoggedInEvent;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.joda.time.DateTime;
import org.joda.time.Duration;

 /*
 *
 * @author Fatalix
 */
@CDIUI("")
@Theme("bluebooks")
@Title("Bookery")
public class App extends UI{

    
    @Inject
    private AppUserService userService;
    
    @Inject
    private AppLayout appLayout;
    @Inject
    private CDIViewProvider viewProvider;
    
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
        
        Navigator navigator = new Navigator(this, appLayout.getMainContent());
        
        navigator.addProvider(viewProvider);
        setContent(appLayout);

        getNavigator().addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeListener.ViewChangeEvent event) {
               if (!isLoggedIn()) {
                    appLayout.getAppHeader().setVisible(false);
                    if (!event.getViewName().equals(LoginView.id)) {
                        getNavigator().navigateTo(LoginView.id);
                        return false;
                    }
                    return true;
                } else {
                    appLayout.getAppHeader().setVisible(isLoggedIn());
                    appLayout.getAppHeader().setLoginName(SecurityUtils.getSubject().toString());
                    //getMenu().addMenuItem(logout);
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
        
        if (!isLoggedIn()) {
            appLayout.getAppHeader().setVisible(false);
            getNavigator().navigateTo(LoginView.id);
        }
        else {
            appLayout.getAppHeader().setVisible(isLoggedIn());
            appLayout.getAppHeader().setLoginName(SecurityUtils.getSubject().toString());
            if (getNavigator().getState().isEmpty()) {
                
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
        AppUser user = userService.updateLastLogin(event.getUsername());
        
        if (user.getLastLogin() != null) {
            DateTime dtLastLogin = new DateTime(user.getLastLogin());
            DateTime dtCurrentLogin = new DateTime(user.getCurrentLogin());
            Duration duration = new Duration(dtLastLogin, dtCurrentLogin);
            String sinceLastLogin = "";
            if (duration.getStandardDays()>0) {
                long days = duration.getStandardDays();
                if (days == 1) {
                    sinceLastLogin = days + " day";
                }
                else {
                    sinceLastLogin = days + " days";
                }
            }
            else if (duration.getStandardHours()> 0) {
                long hours = duration.getStandardHours();
                if (hours == 1) {
                    sinceLastLogin = hours + " hour";
                }
                else {
                    sinceLastLogin = hours + " hours";
                }
            }
            else if (duration.getStandardMinutes() > 0) {
                long minutes = duration.getStandardMinutes();
                if (minutes == 1) {
                    sinceLastLogin = minutes + " minute";
                }
                else {
                    sinceLastLogin = minutes + " minutes";
                }
            }
            else {
                long seconds = duration.getStandardSeconds();
                if (seconds == 1) {
                    sinceLastLogin = seconds + " second";
                }
                else {
                    sinceLastLogin = seconds + " seconds";
                }
            }
            Notification.show("Welcome back " + event.getUsername() + " after " + sinceLastLogin + ".");
        }
        else {
            Notification.show("Welcome " + event.getUsername());
        }
        
        
        getNavigator().navigateTo(HomeView.id);
        appLayout.getAppHeader().setLoginName(SecurityUtils.getSubject().getPrincipal().toString());
        appLayout.getAppHeader().setVisible(isLoggedIn());
        
    }

    
    public void logout() {
        SecurityUtils.getSubject().logout();
        getSession().close();
        close();
        getPage().setLocation("");
    }
    
}
