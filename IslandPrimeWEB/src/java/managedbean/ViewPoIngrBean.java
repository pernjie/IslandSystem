/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import classes.IngredientPoDetailsClass;
import classes.WeekHelper;
import entity.Facility;
import entity.Item;
import entity.PoItem;
import entity.PoRecord;
import entity.Supplier;
import entity.SuppliesIngrToFac;
import entity.SuppliesMatToFac;
import entity.SuppliesProdToFac;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.primefaces.event.CellEditEvent;
import session.stateless.KitchenBean;
import util.SMTPAuthenticator;

@ManagedBean(name = "viewPoIngrBean")
@ViewScoped
public class ViewPoIngrBean implements Serializable {

    @EJB
    private KitchenBean kb;
    //@ManagedProperty(value="#{poIngrBean}")
    //private PoIngrBean poIngrBean;
    private List<IngredientPoDetailsClass> ingredientPoDetailsClass;
    private List<SuppliesIngrToFac> suppliesIngrToFac;
    private String loggedInEmail;
    private Date currDate;
    private Facility fac;
    private String supplier;
    private Supplier sup;
    private Item item;
    private Integer week;
    private Integer year;
    private Date delivery_date;
    private PoRecord poRecord;
    private List<PoItem> piList;
    private PoItem pi;
    private WeekHelper wh = new WeekHelper();
    
   String emailServerName = "mailauth.comp.nus.edu.sg";
// Replace with your real name and unix email address
    String emailFromAddress = "Island Furniture System Administrator <a0101309@u.nus.edu>";
// Replace with your real name and NUSNET email address
    String mailer = "JavaMailer";

    public ViewPoIngrBean() {
    }
    /*
    public void setPoIngrBean(PoIngrBean poIngrBean) {
        this.poIngrBean = poIngrBean;
    }
    */
    @PostConstruct
    public void init() {
        //check if new product?? TODO
        System.err.println("view PO init():");
        poRecord = new PoRecord();
        try {
            //poRecord = poIngrBean.getPoRecord();
            poRecord = (PoRecord) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("poRecord");
            System.err.println("porecord id: " + poRecord);
        } catch (Exception ex) {
            String statusMessage = "Item Not Found.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Generate Purchase Order Result: "
                + statusMessage, ""));
                ex.printStackTrace();
        }
        sup = poRecord.getSup();
        fac = poRecord.getFac();
        piList = kb.getPoItems(poRecord);
    }
    
    public void sendPo() throws IOException {
        String toEmailAddress = sup.getContactEmail();
        System.out.println("send to: " + toEmailAddress);
        
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
                msg.setSubject("Purchase Order from Island Furniture!"+"\n\n");
                
                //Create and fill first part
                MimeBodyPart header = new MimeBodyPart();
                header.setText("Hi " + sup.getName() + ".\n\n");
                
                //Create and fill second part
                MimeBodyPart body = new MimeBodyPart();
                body.setText("You have a new purchase order request from Island Furniture - " + fac.getName() + ".\n\n");
                
                //Create and fill third part
                MimeBodyPart linkText = new MimeBodyPart();
                linkText.setText("Please log in to Island Furniture Supplier Extranet to view the purchase order. Thank you.");
                
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
    FacesContext.getCurrentInstance().getExternalContext().redirect("../kitchen/kitchen_home.xhtml");
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Supplier getSup() {
        return sup;
    }

    public void setSup(Supplier sup) {
        this.sup = sup;
    }

    public Integer getWeek() {
        return week;
    }

    public Integer getYear() {
        return year;
    }

    public PoRecord getPoRecord() {
        return poRecord;
    }

    public void setPoRecord(PoRecord poRecord) {
        this.poRecord = poRecord;
    }

    public List<PoItem> getPiList() {
        return piList;
    }

    public void setPiList(List<PoItem> piList) {
        this.piList = piList;
    }

    public PoItem getPi() {
        return pi;
    }

    public void setPi(PoItem pi) {
        this.pi = pi;
    }

    public String dateString(Date date) {
        return wh.getDateString(date);
    }
}
