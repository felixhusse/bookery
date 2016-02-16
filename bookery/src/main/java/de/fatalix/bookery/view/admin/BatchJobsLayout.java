/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.admin;

import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.bl.model.BatchJobConfiguration;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author felixhusse1
 */
public class BatchJobsLayout extends CustomComponent implements BatchJobCard.Listener{
    
    @Inject private AdminPresenter presenter;
    
    @Inject private Instance<BatchJobCard> batchJobCardInstances;
    private HorizontalLayout batchJobLayout;

    @PostConstruct
    private void postInit() {
        batchJobLayout = new HorizontalLayout();
        batchJobLayout.addStyleName("wrapping"); 
        batchJobLayout.setSpacing(true);
        batchJobLayout.setMargin(true);

        this.setCompositionRoot(batchJobLayout);
    }
    
    
    private Layout createEmptyLayout() {
        Label label = new Label("Add new job...");
        label.setSizeUndefined();
        label.addStyleName(ValoTheme.LABEL_LARGE);
        
        VerticalLayout  emptyLayout = new VerticalLayout(label);
        emptyLayout.addStyleName("dashed-border");
        emptyLayout.setWidth(380, Unit.PIXELS);
        emptyLayout.setHeight(220, Unit.PIXELS);
        emptyLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        emptyLayout.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {

            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                BatchJobConfiguration jobConfig = presenter.createBatchJob();
                BatchJobCard batchJobCard = batchJobCardInstances.get();
                batchJobCard.load(jobConfig);
                batchJobCard.addBatchJobCardListener(BatchJobsLayout.this);
                batchJobLayout.addComponent(batchJobCard, batchJobLayout.getComponentCount()-1);
            }
        });
        return emptyLayout;
    }
    
    public void enter() {
        batchJobLayout.removeAllComponents();
        batchJobLayout.addComponent(createEmptyLayout());
        List<BatchJobConfiguration> jobConfigs = presenter.getAllJobs();
        for (BatchJobConfiguration jobConfig : jobConfigs) {
            BatchJobCard batchJobCard = batchJobCardInstances.get();
            batchJobCard.load(jobConfig);
            batchJobCard.addBatchJobCardListener(BatchJobsLayout.this);
            batchJobLayout.addComponent(batchJobCard, batchJobLayout.getComponentCount()-1);
        }
    }

    @Override
    public void jobDeleted(BatchJobCard batchJobCard) {
        batchJobLayout.removeComponent(batchJobCard);
    }
    
}
