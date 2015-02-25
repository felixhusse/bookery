/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.login;

/**
 *
 * @author felix.husse
 */
public class UserLoggedInEvent {
    private final String username;

    public UserLoggedInEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
