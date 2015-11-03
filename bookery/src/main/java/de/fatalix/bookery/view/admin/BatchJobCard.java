/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.admin;

import com.vaadin.data.Property;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.bl.background.BatchJobType;
import de.fatalix.bookery.bl.model.BatchJobConfiguration;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author felix.husse
 */
public class BatchJobCard extends CssLayout{
    
    private final List<Listener> listeners = new ArrayList<>();
    
    @Inject
    private AdminPresenter presenter;
    
    private BatchJobConfiguration jobConfig;
    
    private Label captionLabel;
        
    private Label description;
    private Label nextRuntime;
    private ComboBox batchJobTypeCombo;
    private CheckBox batchJobActive;
    private TextField cronjobExpression;
    private TextArea batchJobConfiguration;
    private boolean noUpdate=false;
    
    @PostConstruct
    private void postInit() {
        addStyleName("bookery-content");
        addComponents(createHeader(), createContent());
        setWidth(380, Unit.PIXELS);
        //setHeight(240, Unit.PIXELS);
    }
    
    private HorizontalLayout createHeader() {
        captionLabel = new Label("some.batchjob");
        
        Button deleteBatchJob = new Button(null, new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.deleteBatchJob(jobConfig);
                for (BatchJobCard.Listener listener : listeners) {
                    listener.jobDeleted(BatchJobCard.this);
                }
            }
        });
        deleteBatchJob.setIcon(FontAwesome.TIMES_CIRCLE);
        deleteBatchJob.addStyleName("borderless");
        deleteBatchJob.addStyleName("icon-only");
        
        HorizontalLayout captionLayout = new HorizontalLayout();
        captionLayout.addStyleName("v-panel-caption");
        captionLayout.setWidth("100%");
        captionLayout.addComponents(captionLabel,deleteBatchJob);
        captionLayout.setExpandRatio(captionLabel, 1);
        
        return captionLayout;
    }
    
    private FormLayout createContent() {
        batchJobTypeCombo = new ComboBox("Batch Type");
        for (BatchJobType type : BatchJobType.values()) {
            batchJobTypeCombo.addItem(type);
        }
        batchJobTypeCombo.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (!noUpdate) {
                    BatchJobType newType = ((BatchJobType)batchJobTypeCombo.getValue());
                    
                        batchJobConfiguration.setValue(newType.getDefaultConfig());
                    
                    updateBean();
                    setFields();
                }
            }
        });
        
        description = new Label("description");
        nextRuntime = new Label("---");
        batchJobActive = new CheckBox("active", false);
        cronjobExpression = new TextField("Cronjob", "*******");
        batchJobConfiguration = new TextArea("Configuration");
        
        Button updateButton = new Button("update", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                updateBean();
                jobConfig = presenter.updateBatchJob(jobConfig);
                setFields();
            }
        });
        updateButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        
        FormLayout batchJobCardContent = new FormLayout(batchJobTypeCombo,description,cronjobExpression,batchJobActive,batchJobConfiguration,nextRuntime,updateButton);
        batchJobCardContent.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        batchJobCardContent.setMargin(true);
        return batchJobCardContent;
    }
    
    public void load(BatchJobConfiguration jobConfig) {
        this.jobConfig = jobConfig;
        setFields();
    }
    
    private void updateBean() {
        jobConfig.setType((BatchJobType) batchJobTypeCombo.getValue());
        jobConfig.setCronJobExpression(cronjobExpression.getValue());
        jobConfig.setActive(batchJobActive.getValue());
        jobConfig.setConfigurationXML(batchJobConfiguration.getValue());
    }
    
    private void setFields() {
        noUpdate = true;
        captionLabel.setValue(jobConfig.getType().getDisplayName());
        batchJobTypeCombo.setValue(jobConfig.getType());
        description.setValue(jobConfig.getType().getDescription());
        cronjobExpression.setValue(jobConfig.getCronJobExpression());
        if (jobConfig.getNextTimeout() == null) {
            nextRuntime.setValue("---");
        }
        else {
            nextRuntime.setValue(jobConfig.getNextTimeout().toString());
        }
        batchJobConfiguration.setValue(jobConfig.getConfigurationXML());
        batchJobActive.setValue(jobConfig.isActive());
        noUpdate = false;
    }
    
    
    public void addBatchJobCardListener(BatchJobCard.Listener listener) {
        listeners.add(listener);
    }
    
    public interface Listener {
        void jobDeleted(BatchJobCard batchJobCard);
    }
    
}
