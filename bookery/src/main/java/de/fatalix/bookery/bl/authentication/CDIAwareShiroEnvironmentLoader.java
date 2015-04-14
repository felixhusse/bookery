/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.authentication;

import java.util.Collection;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.env.DefaultWebEnvironment;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

/**
 *
 * @author felix.husse
 */
public class CDIAwareShiroEnvironmentLoader extends EnvironmentLoaderListener{
    private final static String HASHING_ALGORITHM = "SHA-512";

    @Inject
    private JPARealm jpaRealm;

    @Override
    protected WebEnvironment createEnvironment(ServletContext sc) {
        System.out.println("Loading JPA REALM");
        WebEnvironment webEnvironment = super.createEnvironment(sc);

        RealmSecurityManager rsm = (RealmSecurityManager) webEnvironment.getSecurityManager();
        
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher(HASHING_ALGORITHM);
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        jpaRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        
        Collection<Realm> realms = rsm.getRealms();
        realms.add(jpaRealm);
        rsm.setRealms(realms);

        ((DefaultWebEnvironment) webEnvironment).setSecurityManager(rsm);
        return webEnvironment;
    }
}
