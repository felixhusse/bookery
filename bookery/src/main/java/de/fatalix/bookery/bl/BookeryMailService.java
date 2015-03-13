/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl;

import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
    
}
