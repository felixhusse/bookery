/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery;

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
