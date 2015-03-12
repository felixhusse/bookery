/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.admin;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.AppUserService;
import de.fatalix.bookery.bl.dao.AppSettingDAO;
import de.fatalix.bookery.bl.elasticsearch.SolrHandler;
import de.fatalix.bookery.bl.fileimport.FileImportService;
import de.fatalix.bookery.bl.model.AppSetting;
import de.fatalix.bookery.bl.model.AppUser;
import de.fatalix.bookery.bl.model.SettingKey;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author felix
 */
@UIScoped
public class AdminPresenter {
    
    @Inject private AppUserService service;
    @Inject private AppSettingDAO settingDAO;
    @Inject private FileImportService fileImportService;
    @Inject private SolrHandler solrHandler;
    
    public List<AppUser> loadUserList() {
        return service.getAllAppUser();
    }
    
    public AppUser updateUser(AppUser user) {
        return service.updateUser(user);
    }
    
    public AppUser updatePassword(AppUser user, String password) {
        return service.updateUserPassword(user, password);
    }
    
    public AppUser createNewUser() {
        AppUser user = new AppUser();
        user.setUsername("newuser");
        user.setPassword("password");
        return service.createUser(user);
    }
    
    public void deleteUser(AppUser user) {
        service.deleteUser(user);
    }

    public AppSetting loadSetting(SettingKey key) {
        return settingDAO.findByKey(key);
    }
    
    public AppSetting updateSetting(SettingKey key, String value) {
        AppSetting setting = settingDAO.findByKey(key);
        setting.setConfigurationValue(value);
        return settingDAO.update(setting);
    }
    
    public void resetIndex() throws IOException, SolrServerException {
        solrHandler.resetSolrIndex();
    }
    
    public void doFileImport(String folder, String username) {
        fileImportService.invoke(folder, username);
    }
}
