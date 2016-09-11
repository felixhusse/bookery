/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.login;

import com.vaadin.cdi.CDIView;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.view.common.AbstractView;
import javax.inject.Inject;
import org.apache.shiro.authc.AuthenticationException;
import org.vaadin.cdiviewmenu.ViewMenuItem;

/**
 *
 * @author felix.husse
 */
@CDIView(LoginView.id)
@ViewMenuItem(enabled = false)
public class LoginView extends AbstractView implements View{
    
    public static final String id = "";
    
    @Inject
    private LoginViewPresenter presenter;
    
    private TextField username;
    private PasswordField password;
    private Button login;
    private Button forgotPassword;
    
    public LoginView() {
        setSizeFull();
        CssLayout rootLayout = new CssLayout();
        rootLayout.addStyleName("login-screen");

        Component loginForm = buildLoginForm();
        
        VerticalLayout centeringLayout = new VerticalLayout();
        centeringLayout.setStyleName("centering-layout");
        centeringLayout.addComponent(loginForm);
        centeringLayout.setComponentAlignment(loginForm,Alignment.MIDDLE_CENTER);
        
        CssLayout loginInformation = buildLoginInformation();
        
        rootLayout.addComponent(centeringLayout);
        rootLayout.addComponent(loginInformation);
        
        setCompositionRoot(rootLayout);
        
    }
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        
    }
    
    private Component buildLoginForm() {
        FormLayout loginForm = new FormLayout();

        loginForm.addStyleName("login-form");
        loginForm.setSizeUndefined();
        loginForm.setMargin(false);
        
        loginForm.addComponent(username = new TextField("Username", "admin"));
        username.setWidth(15, Unit.EM);
        loginForm.addComponent(password = new PasswordField("Password"));
        password.setWidth(15, Unit.EM);
        password.setDescription("");
        
        
        CssLayout buttons = new CssLayout();
        buttons.setStyleName("buttons");
        loginForm.addComponent(buttons);
        login = new Button("login");
        buttons.addComponent(login);
        login.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    presenter.doLogin(username.getValue(), password.getValue());
                } catch (AuthenticationException ex) {
                    LoginView.this.showNotification(new Notification("Wrong login",Notification.Type.ERROR_MESSAGE),ValoTheme.NOTIFICATION_FAILURE);
                } finally {
                    login.setEnabled(true);
                }
            }
        });
        login.addStyleName(ValoTheme.BUTTON_FRIENDLY);

        buttons.addComponent(forgotPassword = new Button("Forgot password?"));
        forgotPassword.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                showNotification(new Notification("Hint: Ask me",Notification.Type.HUMANIZED_MESSAGE),ValoTheme.NOTIFICATION_SUCCESS);
            }
        });
        forgotPassword.addStyleName(ValoTheme.BUTTON_LINK);
        Panel loginPanel = new Panel(loginForm);
        loginPanel.setWidthUndefined();
        loginPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        loginPanel.addAction(new ShortcutListener("commit", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                try {
                    presenter.doLogin(username.getValue(), password.getValue());
                } catch (AuthenticationException ex) {
                    LoginView.this.showNotification(new Notification("Wrong login",Notification.Type.ERROR_MESSAGE),ValoTheme.NOTIFICATION_FAILURE);
                } 
            }
        });
        return loginPanel;
    }
    
    private CssLayout buildLoginInformation() {
        CssLayout loginInformation = new CssLayout();
        loginInformation.setStyleName("login-information");
        Label loginInfoText = new Label(
                "<h1>Welcome to Bookery</h1>"
                        + "Please provide your login to access your library. If you have problems logging in, please contact your administrator.",
                ContentMode.HTML);
        loginInformation.addComponent(loginInfoText);
        return loginInformation;
    }
    
    private void showNotification(Notification notification, String style) {
        // keep the notification visible a little while after moving the
        // mouse, or until clicked
        notification.setPosition(Position.TOP_CENTER);
        notification.setStyleName(ValoTheme.NOTIFICATION_BAR);
        notification.setDelayMsec(2000);
        notification.show(Page.getCurrent());
    }
    
}
