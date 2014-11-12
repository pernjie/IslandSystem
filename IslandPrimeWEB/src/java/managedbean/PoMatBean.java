/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import classes.MaterialPoDetailsClass;
import classes.WeekHelper;
import entity.Facility;
import entity.Item;
import entity.PoItem;
import entity.PoRecord;
import entity.Supplier;
import entity.SuppliesMatToFac;
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
import session.stateless.ScmBean;
import util.SMTPAuthenticator;

@ManagedBean(name = "poMatBean")
@ViewScoped
public class PoMatBean implements Serializable {

    @EJB
    private ScmBean sb;
    private List<MaterialPoDetailsClass> materialPoDetailsClass;
    private List<SuppliesMatToFac> suppliesMatToFac;
    private String loggedInEmail;
    private Date currDate;
    private Facility fac;
    private String supplier;
    private Supplier sup;
    private List<Supplier> suppliers;
    private Item item;
    private Integer week;
    private Integer year;
    private Integer delivery_week;
    private Integer delivery_year;
    private Date delivery_date;
    private PoRecord poRecord;
    private List<PoItem> piList;
    private PoItem pi;
    private WeekHelper wh = new WeekHelper();
    private String statusMessage;

    String emailServerName = "mailauth.comp.nus.edu.sg";
// Replace with your real name and unix email address
    String emailFromAddress = "Island Furniture System Administrator <a0101309@u.nus.edu>";
// Replace with your real name and NUSNET email address
    String mailer = "JavaMailer";

    @PostConstruct
    public void init() {
        //check if new product?? TODO
        System.err.println("function: init()");
        loggedInEmail = new String();
        loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        fac = sb.getFac(loggedInEmail);
        System.err.println("fac: " + fac);
        suppliers = sb.getMatSuppliers(fac);
        if (suppliers.isEmpty()) {
            System.err.println("no suppliers found.");
        }
        currDate = wh.getCurrDate();
        week = wh.getWeek();
        year = wh.getYear();
    }

    public void updateTable() {
        System.err.println("function: updateTable");
        sup = sb.getSupplier(Long.valueOf(supplier));
        //System.err.println("supplier: " + supplier);

        materialPoDetailsClass = new ArrayList<MaterialPoDetailsClass>();
        suppliesMatToFac = sb.getSuppliesMatToFac(fac, sup);
        //System.err.println("function: gotSuppliesProdToFac()");

        for (SuppliesMatToFac smf : suppliesMatToFac) {
            //System.err.println("function: iterate supplies");
            //System.err.println("function: current date is " + week);
            delivery_week = week + smf.getLeadTime() + 1;
            delivery_year = year;
            //System.err.println("function: generate delivery date " + delivery_week);
            delivery_date = (Date) wh.getDate(delivery_year, delivery_week);
            //System.err.println("function: delivery date generated " + delivery_date)
            if (!sb.checkPoMatExist(fac, sup, smf.getMat(), delivery_date)) {
                MaterialPoDetailsClass poDetail = new MaterialPoDetailsClass();
                //item = smf.getMat();
                //System.err.println("function: product " + product.getName());
                poDetail.setItem(smf.getMat());
                //to do: check if roll over to next year    
                poDetail.setMatQty(sb.getMatQtyMaterial(fac, smf.getMat(), delivery_week, delivery_year));
                poDetail.setUnitPrice(smf.getUnitPrice());
                //System.err.println("function: mat qty " + sb.getMatQty(fac, product, delivery_week, delivery_year));
                poDetail.setDeliveryDate(delivery_date);
                materialPoDetailsClass.add(poDetail);
            }
        }
        if (materialPoDetailsClass.isEmpty()) {
            statusMessage = "There is no pending purchase order to generate for the supplier this week.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Generate Purchase Order Result: "
                    + statusMessage, ""));
        }
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        Integer qty = Integer.valueOf(newValue.toString());

        if (qty <= 0) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Edited Value", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else if (newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Edited Value", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void generatePO() throws IOException {
        System.err.println("function: generatePO()");
        poRecord = new PoRecord();
        poRecord.setFac(fac);
        poRecord.setSup(sup);
        poRecord.setOrderDate(currDate);
        Double totalPrice = 0.0;

        piList = new ArrayList<PoItem>();
        for (MaterialPoDetailsClass po : materialPoDetailsClass) {
            System.err.println("function: poDetails()");
            pi = new PoItem();
            //pi.setPo(poRecord);
            Item item = sb.getItem(po.getItem().getId());
            pi.setItem(item);
            System.err.println("material: " + item);
            pi.setDeliveryDate(po.getDeliveryDate());
            pi.setQuantity(po.getMatQty());
            pi.setStatus("Ordered");
            pi.setRemarks("");
            pi.setTotalPrice(po.getMatQty() * po.getUnitPrice());
            System.err.println("item price: " + pi.getTotalPrice());
            totalPrice += pi.getTotalPrice();
            /*
             boolean valid = true;
             if (pi.getQuantity() < 0) {
             String msg = "The quantity of item " + pi.getProduct().getName() + " cannot be of a negative value.";
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + msg, ""));
             valid = false;
             }
             Integer lot_size = sb.getLotSize(fac, sup, pi.getProduct());
             if ((pi.getQuantity() % lot_size) != 0) {

             String msg = "The quantity of item " + pi.getProduct().getName() + " does not fit its lot size of " + lot_size + ".";
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + msg, ""));
             valid = false;
             }
             if (valid == true) {
             ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
             */
            piList.add(pi);
        }
        poRecord.setTotalPrice(totalPrice);
        poRecord.setStatus("Sent");
        sb.persistPo(poRecord);
        sb.persistPoDetails(piList, poRecord);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("poRecord", poRecord);
        FacesContext.getCurrentInstance().getExternalContext().redirect("../scm/scm_view_rawmat_po.xhtml");
        //sendPo();
        //}
    }

    public void sendPo() {
        String toEmailAddress = sup.getContactEmail();

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
                msg.setSubject("Purchase Order from Island Furniture!" + "\n\n");

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
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
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

    public List<MaterialPoDetailsClass> getMaterialPoDetailsClass() {
        return materialPoDetailsClass;
    }

    public void setMaterialPoDetailsClass(List<MaterialPoDetailsClass> materialPoDetailsClass) {
        this.materialPoDetailsClass = materialPoDetailsClass;
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
