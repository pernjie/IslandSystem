/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Customer;
import entity.Facility;
import entity.Staff;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import util.SMTPAuthenticator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.RequestScoped;
 
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import session.stateless.OpCrmBean;
import org.primefaces.model.UploadedFile;
/**
 *
 * @author Anna
 */
@ManagedBean(name = "marketingMailMB")
@RequestScoped
public class MarketingMailMB implements Serializable {
    private String emailSubject;
    private String emailBody;
    private UploadedFile file;
    private String statusMessage;
    
    String emailServerName = "mailauth.comp.nus.edu.sg";
// Replace with your real name and unix email address
    String emailFromAddress = "Island Furniture <natalie.goh93@gmail.com>";
// Replace with your real name and NUSNET email address
    String mailer = "JavaMailer";
    //private static final File LOCATION = new File("../resources/images/");
    
    @EJB
    OpCrmBean opcrmBean;

    @PostConstruct
    public void init() {

    } 
    
     public void sendMarketingEmail(ActionEvent event){
        System.out.println(emailSubject);
        System.out.println(emailBody);
        System.out.println(file);
        
        System.out.println("UPLOAD: "+ file.getFileName());     
        
        statusMessage = "Image Uploaded Successfully.";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,  statusMessage, ""));
        
         if(file!=null){
            try {
               String tDir = System.getProperty("java.io.tmpdir");
                System.out.println("File path: "+tDir);
                String prefix = FilenameUtils.getBaseName(file.getFileName());
                String suffix = FilenameUtils.getExtension(file.getFileName());
                System.out.println(prefix);
                System.out.println(suffix);
                File save = File.createTempFile(prefix, "." + suffix);
               
                System.out.println("File path: "+ save.getAbsolutePath());
               
                byte[] bytes = IOUtils.toByteArray(file.getInputstream());
                
                System.out.println(bytes);
                
                FileOutputStream fos = new FileOutputStream(save.getAbsolutePath());
                fos.write(bytes);
                fos.close();
                //Files.write(save.toPath(), bytes);
                
                Path path = Paths.get(save.getAbsolutePath());
                byte[] data = Files.readAllBytes(path);
                System.out.println(data);
               loopCustomers(save, emailSubject, emailBody);
            statusMessage = "New Marketing Email Sent Successfully.";
               FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,  statusMessage, ""));
           } catch (Exception ex) {
               statusMessage = "Send New Marketing Email Failed.";
               FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
                       statusMessage, ""));
               ex.printStackTrace();
           }
       
     }
     }
     
     public void loopCustomers(File savedFile,String emailSubject, String emailBody) {
         System.err.println("sendEmailFunction");

        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        List<Customer> subscribers = opcrmBean.getAllCustomers();
        System.err.println("Number of customers = " + subscribers.size());

        for (int i = 0; i < subscribers.size(); i++) {

            String custEmail = subscribers.get(i).getEmail();
            Boolean unsubValue = subscribers.get(i).getUnsubscribed();
            String custName = subscribers.get(i).getName();
            Integer points = subscribers.get(i).getPoints();
            Boolean isPlus = subscribers.get(i).getPlus();
            System.err.println("Subscriber email: " + custEmail);
            System.err.println("Subscriber unsubscribed status: " + unsubValue);

            
            if (unsubValue != true) {
                
                sendEmail(custEmail, custName, points, isPlus, savedFile, emailSubject, emailBody);
                
            }

        }
        savedFile.deleteOnExit();
        
    }
     
    public void upload() throws IOException {
        if(file != null) {
            FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
         
            
        }
    } 
    
    public void sendEmail(String custEmail, String custName, Integer points, Boolean isPlus, File savedFile, String emailSubject, String emailBody){
            System.out.println("SAVED FILE: "+savedFile);     
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
                        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(custEmail, false));
                        msg.setSubject(emailSubject + "\n\n");
                        
                        String status;
                         if (isPlus) {
                           status = "Island Plus Member";
                        } else {
                           status = "Island Member";
                        }
                       
                        //This mail has 2 part, the BODY  and the embedded image
                        MimeMultipart mp = new MimeMultipart("related"); 
                        
                       
                        
                        BodyPart messageBodyPart5 = new MimeBodyPart();
                        String htmlText6 = "<H1> Hey " + custName + "</H1> <h1>"+ emailBody+" </h1> Status: "+status+"</h2> <p> Current Points: "+points+"</p>"+"<img src=\"cid:image\">" +"<a href=\"../opcrm/Opcrm_unsubscribe_customer.xhtml\"><p>Click to Unsubscribe</p></a>";
                        messageBodyPart5.setContent(htmlText6, "text/html");
                        // add it
                        mp.addBodyPart(messageBodyPart5);
                         // second part (the image)
                        messageBodyPart5 = new MimeBodyPart();
                        DataSource fds = new FileDataSource((String) savedFile.getAbsolutePath());
                        messageBodyPart5.setDataHandler(new DataHandler(fds));
                        messageBodyPart5.setHeader("Content-ID", "<image>");
                        
                        // add image to the multipart
                        mp.addBodyPart(messageBodyPart5);
                        
                        msg.setContent(mp);
                         
                        //Send message
                        Transport.send(msg);

                        System.out.println("Sent message successfully....");

                    }
  
        } catch (Exception e) {
                    e.printStackTrace();
                    throw new EJBException(e.getMessage());
                }
    }
   
      public MarketingMailMB() {

    }
      
      
    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }
 
    public UploadedFile getFile() {
        return file;
    }
 
    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public OpCrmBean getOpcrmBean() {
        return opcrmBean;
    }

    public void setOpcrmBean(OpCrmBean opcrmBean) {
        this.opcrmBean = opcrmBean;
    }
    
    
}


    

