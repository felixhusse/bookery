/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.admin;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.AppUserService;
import de.fatalix.bookery.bl.BookeryMailService;
import de.fatalix.bookery.bl.BookeryService;
import de.fatalix.bookery.bl.background.BatchJobService;
import de.fatalix.bookery.bl.background.BatchJobType;
import de.fatalix.bookery.bl.background.thumbnail.ThumbnailBatchConfiguration;
import de.fatalix.bookery.bl.dao.AppSettingDAO;
import de.fatalix.bookery.bl.solr.SolrHandler;
import de.fatalix.bookery.bl.fileimport.FileImportService;
import de.fatalix.bookery.bl.model.AppSetting;
import de.fatalix.bookery.bl.model.AppUser;
import de.fatalix.bookery.bl.model.BatchJobConfiguration;
import de.fatalix.bookery.bl.model.SettingKey;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author felix
 */
@UIScoped
public class AdminPresenter {
    @Inject private BookeryService bookeryService;
    @Inject private AppUserService service;
    @Inject private AppSettingDAO settingDAO;
    @Inject private FileImportService fileImportService;
    @Inject private SolrHandler solrHandler;
    @Inject private BookeryMailService mailService;
    @Inject private BatchJobService batchJobService;
    
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
        setting = settingDAO.update(setting);
        try {
            bookeryService.updateConfiguration();
        } catch(SolrServerException | IOException ex) {
            Logger.getLogger(AdminPresenter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return setting;
    }
    
    public void resetIndex() throws IOException, SolrServerException {
        solrHandler.resetSolrIndex();
    }
    
    public void doFileImport(String folder, String username) {
        fileImportService.invoke(folder, username);
    }
    
    public void sendEmail(String receiver) throws MessagingException {
        //mailService.sendTestMail(receiver);
    }
    
    public long getSolrInfo() throws SolrServerException, IOException {
        return solrHandler.checkSolr();
    }
    
    public List<BatchJobConfiguration> getAllJobs() {
        return batchJobService.getAllJobs();
    }
    
    public BatchJobConfiguration updateBatchJob(BatchJobConfiguration jobConfig) {
        return batchJobService.updateJob(jobConfig);
    }
    
    public BatchJobConfiguration createBatchJob() {
        BatchJobConfiguration jobConfig = new BatchJobConfiguration();
        jobConfig.setType(BatchJobType.THUMBNAIL);
        jobConfig.setCronJobExpression("0 */5 * * * * *");
        jobConfig.setActive(false);
        jobConfig.setConfigurationXML(BatchJobType.THUMBNAIL.getDefaultConfig());
        return batchJobService.saveJob(jobConfig);
    }
    
    public void deleteBatchJob(BatchJobConfiguration jobConfig) {
        batchJobService.deleteJob(jobConfig);
    }
    
}
