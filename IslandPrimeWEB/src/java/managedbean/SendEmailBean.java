/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managedbean;

import entity.Staff;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import session.stateless.CIBeanLocal;
import util.SMTPAuthenticator;

@ManagedBean
@SessionScoped
public class SendEmailBean implements Serializable {
    
    @EJB
    private CIBeanLocal cib;
   
   String emailServerName = "mailauth.comp.nus.edu.sg";
// Replace with your real name and unix email address
    String emailFromAddress = "Island Furniture System Administrator <a0101309@u.nus.edu>";
// Replace with your real name and NUSNET email address
    String mailer = "JavaMailer";

    public SendEmailBean() {
    }

    public void emailMyEvents(String name, String email, String password) {
        String toEmailAddress = email;
        
        try {
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", emailServerName);
            props.put("mail.smtp.port", "25");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.debug", "true");
            javax.mail.Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth); 
            session.setDebug(true);
            Message msg = new MimeMessage(session);
            if (msg != null) {
                msg.setFrom(InternetAddress.parse(emailFromAddress, false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress, false));
                msg.setSubject("Welcome to Island Furniture!"+"\n\n");
                
                //Create and fill first part
                MimeBodyPart header = new MimeBodyPart();
                header.setText("Welcome to Island Furniture, " +name+ ".\n\n");
                
                //Create and fill second part
                MimeBodyPart body = new MimeBodyPart();
                body.setText("Here's the autogenerated password: " + password +"\n\n");
                
                //Create and fill third part
                MimeBodyPart linkText = new MimeBodyPart();
                linkText.setText("Log in and go to View My Profile to change your password.");
                
                //Create the Multipart
                Multipart mp = new MimeMultipart();
                mp.addBodyPart(header);
                mp.addBodyPart(body);
                mp.addBodyPart(linkText);
                
                //Set Message Content
                msg.setContent(mp);
                
                //String messageText = "Welcome to Island Furniture Family, " +name+ ".\n\n Here's the autogenerated password: " + password +"\n\n";
                //msg.setText(messageText);
                //msg.setDisposition(Part.INLINE);
                
                msg.setHeader("X-Mailer", mailer);
                Date timeStamp = new Date();
                msg.setSentDate(timeStamp);
                Transport.send(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new EJBException(e.getMessage());
        }
    }
    
    public void emailMyAnnouncements(String text) {
        
        try {
            
            List<Staff> staffAcc = cib.getAllAcounts();
            
            for(int i=0; i< staffAcc.size(); ++i ){
                
                String email = staffAcc.get(i).getEmail();
                String toEmailAddress = email;
                Properties props = new Properties();
                props.put("mail.transport.protocol", "smtp");
                props.put("mail.smtp.host", emailServerName);
                props.put("mail.smtp.port", "25");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.debug", "true");
                javax.mail.Authenticator auth = new SMTPAuthenticator();
                Session session = Session.getInstance(props, auth); 
                session.setDebug(true);
                Message msg = new MimeMessage(session);
                if (msg != null) {
                    msg.setFrom(InternetAddress.parse(emailFromAddress, false)[0]);
                    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress, false));
                    msg.setSubject("Announcement"+"\n\n");
                
                    //Create and fill first part
                    MimeBodyPart header = new MimeBodyPart();
                    header.setText(text+ ".\n\n");
                
                    //Create and fill second part
                    MimeBodyPart linkText = new MimeBodyPart();
                    linkText.setText("This is an autogenerated email from Island Furniture Admin System.");
                
                   //Create the Multipart
                   Multipart mp = new MimeMultipart();
                   mp.addBodyPart(header);
                   mp.addBodyPart(linkText);
                
                   //Set Message Content
                  msg.setContent(mp);
                
                
                  msg.setHeader("X-Mailer", mailer);
                  Date timeStamp = new Date();
                  msg.setSentDate(timeStamp);
                  Transport.send(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new EJBException(e.getMessage());
        }
    }
}