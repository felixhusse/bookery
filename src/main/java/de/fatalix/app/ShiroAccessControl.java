/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.app;

import com.vaadin.cdi.access.JaasAccessControl;
import javax.enterprise.inject.Specializes;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @author felix.husse
 */
@Specializes
public class ShiroAccessControl extends JaasAccessControl{

    @Override
    public String getPrincipalName() {
        Object principal = SecurityUtils.getSubject().getPrincipal();

        return principal != null ? principal.toString() : null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return SecurityUtils.getSubject().hasRole(role);
    }

    @Override
    public boolean isUserSignedIn() {
        return SecurityUtils.getSubject().isAuthenticated();
    }
    
    
    
}
