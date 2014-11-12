/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import static com.sun.faces.facelets.util.Path.context;
import entity.Facility;
import entity.InventoryMaterial;
import entity.InventoryProduct;
import entity.Item;
import entity.Material;
import java.util.ArrayList;
import java.util.List;
import entity.Shelf;
import entity.ShelfSlot;
import entity.ShelfType;
import entity.Staff;
import enumerator.InvenLoc;
import java.awt.Event;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
 
import org.primefaces.event.SelectEvent;
import session.stateless.GlobalHqBean;
import session.stateless.InventoryBean;
import util.SMTPAuthenticator;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;
/**
 *
 * @author Anna
 */
@ManagedBean(name = "addAdHocFurnMB")
@ViewScoped
public class AddAdHocFurnMB implements Serializable{
    private Item furn;
    private Item tempFurn;

    private String statusMessage;
    
    private Integer quantity;
    private String reason;
    private String recipientEmail;
    
    @EJB
    private InventoryBean inventoryBean;
    
    String emailServerName = "mailauth.comp.nus.edu.sg";
// Replace with your real name and unix email address
    String emailFromAddress = "Island Furniture System Administrator <a0101309@u.nus.edu>";
// Replace with your real name and NUSNET email address
    String mailer = "JavaMailer";

    
    public AddAdHocFurnMB() {
        furn = null;
    }
    
    
    @PostConstruct
    public void init() {

    } 
    
    public List<Item> completeText(String query) {
        
        List<Item> allRawMats = inventoryBean.getFurnitures();
        List<Item> filteredResults = new ArrayList<Item>();
       
        for (Item indiv : allRawMats) {
            if(indiv.getName().toLowerCase().contains(query)) {
                filteredResults.add(indiv);
            }
        }

        return filteredResults;
    }
    
   
   public void handleSelect(SelectEvent event) {
         tempFurn = (Item) event.getObject();
         System.out.println("HANDLE_SELECTED: "+tempFurn.getId());
         FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("furn", tempFurn);
         System.out.println("HANDLE_SELECTED 2: "+ tempFurn);
    }
    
    public void sendAdHocProduction(ActionEvent event) {
        System.out.println(furn);
        System.out.println(quantity);
        System.out.println(reason);
        System.out.println(recipientEmail);

        Integer quantityInt = Integer.valueOf(quantity);
        
        try {
            sendAdHocProductionEmail(furn, quantity, reason, recipientEmail);
            statusMessage = "New Ad Hoc Production Plan Sent Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,  statusMessage, ""));
        } catch (Exception ex) {
            statusMessage = "New Ad Hoc Production Plan Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Ad Hoc Purchase Plan Failed: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    } 
    
    public void sendAdHocProductionEmail(Item furn, Integer quantity, String reason, String recipientEmail){
         try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            String loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
            Query q = em.createNamedQuery("Staff.findByEmail");
            q.setParameter("email", loggedInEmail);

            System.out.println("email: " + loggedInEmail);
            Staff temp = (Staff) q.getSingleResult();
            Facility fac = temp.getFac();

            System.out.println("FACID: " + fac.getId());
            
            java.util.Date date = new java.util.Date();

            //change date into ddMMyyyy format 
            SimpleDateFormat dateformatddMMyyyy = new SimpleDateFormat("E dd/MM/yyyy");
            String date_to_string = dateformatddMMyyyy.format(date);

            //change date for chat timestamp
            SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss.S a zzz");
 
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
                msg.setFrom(InternetAddress.parse(temp.getEmail(), false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));
                msg.setSubject("Ad Hoc Production Plan Order"+"\n\n");
                
                //Create and fill first part             
                MimeBodyPart header = new MimeBodyPart();
                header.setText("New Ad Hoc Production Order, " + "\n\n");
                
                MimeBodyPart timestamp = new MimeBodyPart();
                timestamp.setText("Generated Time: " + date_to_string +" "+ ft.format(date) +"\n\n");
                
                MimeBodyPart body1 = new MimeBodyPart();
                body1.setText("Sender Email: " + temp.getEmail() +"\n\n");
                
                MimeBodyPart body2 = new MimeBodyPart();
                body2.setText("Sender Name: " + temp.getName() +"\n\n");
                
                MimeBodyPart body3 = new MimeBodyPart();
                body3.setText("Sender Contact: " + temp.getContact() +"\n\n");
                
                MimeBodyPart body4 = new MimeBodyPart();
                body4.setText("Fac ID: " + fac.getId() +"\n\n");
                
                MimeBodyPart body5 = new MimeBodyPart();
                body5.setText("Furniture ID: " + furn.getId() +"\n\n");
                
                MimeBodyPart body6 = new MimeBodyPart();
                body6.setText("Furniture Name: " + furn.getName() +"\n\n");
                
                MimeBodyPart body7 = new MimeBodyPart();
                body7.setText("Quantity: " + quantity +"\n\n");
                
                MimeBodyPart body8 = new MimeBodyPart();
                body8.setText("Reason for Request: " + reason +"\n\n");
                
                //Create the Multipart
                Multipart mp = new MimeMultipart();
                mp.addBodyPart(header);
                mp.addBodyPart(timestamp);
                mp.addBodyPart(body1);
                mp.addBodyPart(body2);
                mp.addBodyPart(body3);
                mp.addBodyPart(body4);
                mp.addBodyPart(body5);
                mp.addBodyPart(body6);
                mp.addBodyPart(body7);
                mp.addBodyPart(body8);
                
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
    
     /**
     * Creates a new instance of SuppliesMatToFacManagerBean
     * @return 
     */
   
    public InventoryBean getInventoryBean() {
        return inventoryBean;
    }

    public void setInventoryBean(InventoryBean inventoryBean) {
        this.inventoryBean = inventoryBean;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Item getFurn() {
        return furn;
    }

    public void setFurn(Item furn) {
        this.furn = furn;
    }

    public Item getTempFurn() {
        return tempFurn;
    }

    public void setTempFurn(Item tempFurn) {
        this.tempFurn = tempFurn;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    
}
