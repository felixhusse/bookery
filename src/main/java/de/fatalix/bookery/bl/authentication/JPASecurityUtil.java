/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.authentication;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.crypto.hash.SimpleHash;

/**
 *
 * @author felix.husse
 */
public class JPASecurityUtil {

    public static final int SALT_LENGTH = 80;
    public static final int PASSWORD_LENGTH = 64;

    public static String getSalt() {
        return new SecureRandomNumberGenerator().nextBytes(60).toBase64();
    }

    public static String hashPassword(final String value, final String salt) {
        final Sha256Hash sha256Hash = new Sha256Hash(value, salt, JPARealm.HASH_ITERATIONS);
        return sha256Hash.toHex();
    }

    @Produces
    @Default
    public CredentialsMatcher getCredentialMatcher() {
        final HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName(Sha256Hash.ALGORITHM_NAME);
        credentialsMatcher.setHashIterations(JPARealm.HASH_ITERATIONS);
        return credentialsMatcher;
    }

    @Produces
    @Default
    public SimpleHash getHash() {
        final Sha256Hash sha256Hash = new Sha256Hash();
        sha256Hash.setIterations(JPARealm.HASH_ITERATIONS);
        return sha256Hash;
    }

}
