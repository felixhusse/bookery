/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.authentication;

import java.util.Collection;
import javax.inject.Inject;
import javax.servlet.ServletContext;
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
        Collection<Realm> realms = rsm.getRealms();
        realms.add(jpaRealm);
        rsm.setRealms(realms);

        ((DefaultWebEnvironment) webEnvironment).setSecurityManager(rsm);
        return webEnvironment;
    }
}
