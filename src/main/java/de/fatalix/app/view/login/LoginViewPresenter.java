/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.app.view.login;

import com.vaadin.cdi.UIScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

/**
 *
 * @author felix.husse
 */
@UIScoped
public class LoginViewPresenter {
    
    @Inject
    private Event<UserLoggedInEvent> loggedInEvent;
    
    public void doLogin(String username, String password) throws AuthenticationException{
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        
        if (subject.isAuthenticated()) {
            loggedInEvent.fire(new UserLoggedInEvent(subject.getPrincipal().toString()));
        }
    }
    
    
}
