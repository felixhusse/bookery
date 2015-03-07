/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.data.Property;
import com.vaadin.server.StreamVariable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import java.io.File;
import java.io.OutputStream;
import java.util.Collection;
import javax.annotation.PostConstruct;
import org.vaadin.easyuploads.FileFactory;
import org.vaadin.easyuploads.MultiUpload;
import org.vaadin.easyuploads.MultiUploadHandler;
import org.vaadin.easyuploads.UploadField;

/**
 *
 * @author Fatalix
 */
public class UploadLayout extends VerticalLayout{
    
    @PostConstruct
    private void postInit() {
        this.addStyleName("bookery-content");
        final UploadField uploadField = new UploadField();
        uploadField.setButtonCaption("Choose your book");
        uploadField.setFieldType(UploadField.FieldType.BYTE_ARRAY);
        uploadField.setMaxFileSize(1024*1024*50);
        uploadField.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                byte[] data = (byte[]) uploadField.getValue();
                double fileSize = (double)data.length/1024d/1024d;
                Notification.show("DataSize: "+ fileSize + " mb");
            }
        });
        
        MultiUpload multiUpload = new MultiUpload();
        
        
        this.addComponents(uploadField,multiUpload);
    }
    
}
