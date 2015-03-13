/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl;

import de.fatalix.bookery.bl.model.AppUser;
import java.util.Date;
import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.ejb.Stateless;
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
import javax.mail.util.ByteArrayDataSource;

/**
 *
 * @author Fatalix
 */
@Stateless
public class BookeryMailService {
    
    @Resource(lookup = "java:/mail/bookeryMail")
    private Session mailSession;
    
    public void sendTestMail(String receiver) throws AddressException, MessagingException {
        MimeMessage message = new MimeMessage(mailSession);
        InternetAddress[] address = {new InternetAddress(receiver)};
        message.setRecipients(Message.RecipientType.TO, address);
        message.setSubject("Test Message");
        message.setSentDate(new Date());
        message.setText("This message was sent via wildfly!");
        Transport.send(message);
    }
    
    public void sendKindleMail(AppUser user, byte[] attachment) throws MessagingException {
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("Bookery delivery, frei haus!");
        
        BodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setFileName("book.mobi");
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
    
}
