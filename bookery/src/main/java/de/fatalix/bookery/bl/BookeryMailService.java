/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl;

import de.fatalix.bookery.bl.dao.AppSettingDAO;
import de.fatalix.bookery.bl.model.AppUser;
import de.fatalix.bookery.bl.model.SettingKey;
import de.fatalix.bookery.bl.solr.SolrHandler;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author Fatalix
 */
@Stateless
public class BookeryMailService {

    @Resource(lookup = "java:/mail/bookeryMail")
    private Session mailSession;
    @Resource
    private TimerService timerService;

    @Inject
    private SolrHandler solrHandler;
    @Inject
    private AppSettingDAO settings;
    
    public void scheduleKindleMail(BookEntry book, AppUser user) {
        Payload payload = new Payload(book, user);
        TimerConfig timerConfig = new TimerConfig(payload, false);
        timerService.createSingleActionTimer(100l, timerConfig);
    }
    
    @Timeout
    public void createKindleMail(Timer timer) {
        try {
            Payload payload = (Payload)timer.getInfo();
            BookEntry book = payload.getBook();
            AppUser user = payload.getUser();
            byte[] attachment = solrHandler.getMobiFormat(book.getId()).get(0).getMobi();
            //1.Step Check if convert
            if (attachment == null) {
                convertEPubToMobi(book);
                attachment = solrHandler.getMobiFormat(book.getId()).get(0).getMobi();
            }
            //2. Step Send Email
            String filename = book.getTitle() + "-" + book.getAuthor();
            sendKindleMail(user, attachment, filename);
        } catch (SolrServerException | MessagingException | IOException | InterruptedException ex) {
            Logger.getLogger(BookeryMailService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendKindleMail(AppUser user, byte[] attachment, String filename) throws MessagingException {
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("Bookery delivery, frei haus!");

        BodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setFileName(filename + ".mobi");
        attachmentPart.setContent(attachment, "application/octet-stream");
        //attachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(attachment, "application/x-mobipocket-ebook")));

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachmentPart);

        MimeMessage message = new MimeMessage(mailSession);
        InternetAddress[] address = {new InternetAddress(user.geteMail())};
        message.setRecipients(Message.RecipientType.TO, address);
        message.setSubject("Bookery delivery");
        message.setSentDate(new Date());
        message.setContent(multipart);

        Transport.send(message);
    }

    private void convertEPubToMobi(BookEntry book) throws SolrServerException, IOException, InterruptedException {
        String EBOOK_CONVERT = settings.findByKey(SettingKey.CALIBRE_PATH).getConfigurationValue() + "/ebook-convert";
        String filename = book.getTitle() + "-" + book.getAuthor() + ".epub";
        byte[] data = solrHandler.getEpubBook(book.getId()).get(0).getEpub();
        File epubFile = new File(settings.findByKey(SettingKey.CALIBRE_WORK).getConfigurationValue(), filename);
        Files.write(epubFile.toPath(), data, StandardOpenOption.CREATE);

        String mobiBook = epubFile.getAbsolutePath();
        mobiBook = mobiBook.substring(0, mobiBook.length() - 4);
        mobiBook = mobiBook + "mobi";
        String[] cmds = {EBOOK_CONVERT, epubFile.getAbsolutePath(), mobiBook};
        Process p = Runtime.getRuntime().exec(cmds);
        p.waitFor();
        File mobiFile = new File(mobiBook);
        byte[] mobiData = Files.readAllBytes(mobiFile.toPath());
        updateBookEntry(book.getId(), mobiData);
        epubFile.delete();
        mobiFile.delete();
        System.out.println("Converted EPub");
    }

    private void updateBookEntry(String bookID, byte[] data) throws SolrServerException, IOException {
        List<SolrInputDocument> solrDocs = new ArrayList<>();
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", bookID);

        Map<String, Object> mobiData = new HashMap<>();
        mobiData.put("set", data);
        doc.addField("mobi", mobiData);
        solrDocs.add(doc);
        solrHandler.updateDocument(solrDocs);
    }

    public class Payload implements Serializable{
        private final BookEntry book;
        private final AppUser user;

        public Payload(BookEntry book, AppUser user) {
            this.book = book;
            this.user = user;
        }

        public BookEntry getBook() {
            return book;
        }

        public AppUser getUser() {
            return user;
        }
    }

}
