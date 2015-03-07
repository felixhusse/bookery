/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.annotation.PostConstruct;

/**
 *
 * @author Fatalix
 */
public class BookeryUploader extends CustomComponent{
    
    private final ArrayList<Listener> listeners = new ArrayList<>();
    
    private HorizontalLayout progressLayout;
    
    @PostConstruct
    private void postInit() {
        VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.addStyleName("bookery-content");
        rootLayout.setMargin(true);
        rootLayout.setSpacing(true);
        createUploader(rootLayout);
        this.setCompositionRoot(rootLayout);
    }
    
    private void createUploader(VerticalLayout layout) {
        final ProgressBar progressBar = new ProgressBar();
        
        UploadReceiver receiver = new UploadReceiver();
        final Upload upload = new Upload(null, receiver);
        upload.setEnabled(true);
        upload.addChangeListener(new Upload.ChangeListener() {

            @Override
            public void filenameChanged(Upload.ChangeEvent event) {
                event.getSource().submitUpload();
            }
        });
        upload.setButtonCaption(null);
        upload.addSucceededListener(receiver);
        upload.setWidth(100, Unit.PERCENTAGE);
        upload.addStartedListener(new Upload.StartedListener() {

            @Override
            public void uploadStarted(Upload.StartedEvent event) {
                progressLayout.setVisible(true);
                progressBar.setValue(0f);
                progressBar.setCaption(event.getFilename());
                upload.setVisible(false);
                
            }
        });
        
        upload.addProgressListener(new Upload.ProgressListener() {
            @Override
            public void updateProgress(long readBytes, long contentLength) {
                float progressValue = (float)readBytes/(float)contentLength;
                progressBar.setValue(progressValue);
            }
        });
        
        Button resetUpload = new Button(null,new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                upload.setVisible(true);
                progressLayout.setVisible(false);
            }
        });
        resetUpload.setIcon(FontAwesome.TIMES_CIRCLE);
        resetUpload.addStyleName("borderless");
        resetUpload.addStyleName("icon-only");
        
        progressLayout = new HorizontalLayout(progressBar,resetUpload);
        progressLayout.setSpacing(true);
        progressLayout.setVisible(false);
        layout.addComponents(upload,progressLayout);
    }
    
    public void addBookeryUploaderListener(Listener listener) {
        listeners.add(listener);
    }
    
    protected class UploadReceiver implements Upload.SucceededListener, Upload.Receiver {
        
        private ByteArrayOutputStream bos = new ByteArrayOutputStream(10240);
        
        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            for (Listener listener : listeners) {
                listener.onUploadFinished(bos.toByteArray());
            }
            bos.reset();
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            bos.reset();
            return bos;
        }
    }
    
    public interface Listener {
        void onUploadFinished(byte[] data);
    }
}
