package managedbean;

import classes.WeekHelper;
import entity.Facility;
import entity.InventoryMaterial;
import entity.Item;
import entity.MrpRecord;
import entity.ProductionRecord;
import entity.PurchasePlanningOrder;
import entity.PurchasePlanningRecord;
import entity.ShelfSlot;
import entity.ShelfType;
import entity.Supplier;
import enumerator.InvenLoc;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import session.stateless.ScmBean;
import util.SMTPAuthenticator;

@ManagedBean(name = "productionOperationBean")
@ApplicationScoped
public class ProductionOperationBean implements Serializable {

    private String loggedInEmail;
    private Date currDate;
    private Date productionDate;
    private Facility fac;
    private Item item;
    private Integer week;
    private Integer year;
    private WeekHelper wh = new WeekHelper();

    private Double furnLengthRes;
    private Double furnBreadthRes;
    private Double furnHeightRes;
    private Integer resUpperThres;
    private Integer resLowerThres;
    private boolean disable;

    String emailServerName = "mailauth.comp.nus.edu.sg";
// Replace with your real name and unix email address
    String emailFromAddress = "Island Furniture System Administrator <a0101309@u.nus.edu>";
// Replace with your real name and NUSNET email address
    String mailer = "JavaMailer";

    @EJB
    private ScmBean sb;

    @PostConstruct
    public void init() {
        System.err.println("function: init()");
        productionDate = wh.getDate(2014, 46);
        System.err.println("last production date: " + productionDate);
        loggedInEmail = new String();
        loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        fac = sb.getFac(loggedInEmail);
        System.err.println("fac: " + fac);
        currDate = wh.getCurrDate();
        week = wh.getWeek();
        year = wh.getYear();
        System.err.println("week: " + week);
        System.err.println("year: " + year);
        this.disable = false;
        System.err.println("disable: " + disable);
    }

    public void startProduction() {
        if (wh.getDateString(currDate) != wh.getDateString(productionDate)) {
            productionDate = currDate;
            System.out.println("Production operation for: " + new Date());
            currDate = wh.getCurrDate();
            List<MrpRecord> mrprec = new ArrayList<MrpRecord>();
            try {
                mrprec = sb.getMrpRecord(fac, week, year);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!mrprec.isEmpty()) {
                for (MrpRecord mr : mrprec) {
                    System.err.println("function: iterate production record: " + mr.getMat());
                    InventoryMaterial im = new InventoryMaterial();
                    try {
                        im = sb.getInventoryMat(mr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.err.println("function: inventorymaterial " + im.getMat() + " at fac " + im.getFac() + " with qty " + im.getQuantity());

                    Integer qty = im.getQuantity() - wh.getDailyDemand(mr.getRequirement(), wh.getDay());
                    System.err.println("qty: " + qty);
                    if (qty > 0) {
                        im.setQuantity(qty);
                        sb.updateInventoryMat(im);
                        this.disable = true;
                        System.err.println("disable: " + disable);
                        if (qty < im.getLowThreshold()) {
                            System.err.println("Qty below lower threshold");
                        }
                    } else {
                        System.err.println("Insufficient Inventory");
                        sendAdHocPurchaseOrder(fac, im.getMat());
                    }
                }
            } else {
                System.out.println("No mrp record found");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No Production Available", ""));
            }
        }
        else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "There is no more production for today", ""));
        }
        RequestContext.getCurrentInstance().update(":form:disablepanel");
    }

    public void endProduction() {
        System.out.println("Inventory Update for completed goods on: " + new Date() + " -- " + wh.getPeriod(0) + wh.getYear());
        currDate = wh.getCurrDate();
        Integer period = wh.getPeriod(0);
        List<ProductionRecord> prodrec = new ArrayList<ProductionRecord>();
        prodrec = sb.getProductionRecord(fac, period, year);

        if (!prodrec.isEmpty()) {
            for (ProductionRecord pr : prodrec) {
                System.err.println("function: iterate production record: " + pr.getMat());
                InventoryMaterial im = new InventoryMaterial();
                im = sb.getInventoryMat(pr);
                System.err.println("function: inventorymaterial " + im.getMat() + " at fac " + im.getFac() + " with qty " + im.getQuantity());

                Integer qty = im.getQuantity();
                Integer week = wh.getWeekOfPeriod();
                if (week == 1) {
                    qty += wh.getDailyDemand(pr.getQuantityW1(), wh.getDay());
                } else if (week == 2) {
                    qty += wh.getDailyDemand(pr.getQuantityW2(), wh.getDay());
                } else if (week == 3) {
                    qty += wh.getDailyDemand(pr.getQuantityW3(), wh.getDay());
                } else if (week == 4) {
                    qty += wh.getDailyDemand(pr.getQuantityW4(), wh.getDay());
                } else {
                    qty += wh.getDailyDemand(pr.getQuantityW5(), wh.getDay());
                }
                System.err.println("new qty: " + qty);
                if (qty > im.getUppThreshold()) {
                    System.err.println("Qty exceeds upper threshold");
                    InventoryMaterial mat = new InventoryMaterial();
                    Item furn = im.getMat();
                    System.err.println("mat id:" + furn);
                    try {
                        mat = sb.getInventoryMat(furn, fac, InvenLoc.MF);
                        System.err.println("found invmat id: " + mat);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (mat == null) {
                        System.err.println("new mat:" + furn);
                        ShelfSlot shelfSlot = new ShelfSlot();
                        shelfSlot = sb.getAvailableShelfSlot(fac);
                        if (shelfSlot == null) {
                            System.err.println("no shelf slot in current mf");
                        } else {
                            mat.setShelfSlot(shelfSlot);
                            shelfSlot.setOccupied(Boolean.TRUE);
                            sb.persistShelfSlot(shelfSlot);
                            mat.setShelf(shelfSlot.getShelf());
                            mat.setZone(shelfSlot.getShelf().getZone());
                            mat.setLocation(InvenLoc.MF);
                            ShelfType shelfType = shelfSlot.getShelf().getShelfType();
                            Double slotLength = shelfType.getLength();
                            Double slotBreadth = shelfType.getBreadth();
                            Double slotHeight = shelfType.getHeight();
                            try {
                                Double furnLength = furn.getLength();
                                Double furnBreadth = furn.getBreadth();
                                Double furnHeight = furn.getHeight();
                                Map<String, Double> finalvalues = new HashMap<String, Double>();

                                finalvalues = sb.calcThresValues(slotLength, slotBreadth, slotHeight, furnLength, furnBreadth, furnHeight);
                                furnLengthRes = finalvalues.get("lengthUsed");
                                furnBreadthRes = finalvalues.get("breadthUsed");
                                furnHeightRes = finalvalues.get("heightUsed");
                                Double upperThreshold = finalvalues.get("upperThreshold");
                                Double lowerThreshold = finalvalues.get("lowerThreshold");

                                resUpperThres = upperThreshold.intValue();
                                System.out.println("resUpperThreshold 2: " + resUpperThres);

                                resLowerThres = lowerThreshold.intValue();
                                System.out.println("resUpperThreshold 2: " + resLowerThres);

                                System.out.println("Length to use 2: " + furnLengthRes);
                                System.out.println("Breadth to use 2: " + furnBreadthRes);
                                System.out.println("Height to use 2: " + furnHeightRes);
                                System.out.println("Upper Threshold 2: " + resUpperThres);
                                System.out.println("Lower Threshold 2: " + resLowerThres);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            mat.setLowThreshold(resUpperThres);
                            mat.setUppThreshold(resLowerThres);
                            mat.setMatBreadth(furnBreadthRes);
                            mat.setMatHeight(furnHeightRes);
                            mat.setMatLength(furnLengthRes);
                            sb.persistInventoryMaterial(mat);
                        }
                    }
                } else {
                    im.setQuantity(qty);
                    sb.updateInventoryMat(im);
                }
            }
        } else {
            System.out.println("No production record");
        }
        RequestContext.getCurrentInstance().update(":form:disablepanel");
    }

    public void sendAdHocPurchaseOrder(Facility fac, Item mat) {
        System.err.println("send ad hoc prod order");
        Supplier sup = new Supplier();
        sup = sb.getSupplier(fac, mat);
        String toEmailAddress = "islandFurnituremf@gmail.com";
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
                msg.setSubject("Ad Hoc Purchase Request for " + mat.getName() + " from " + fac.getName() + "\n\n");

                //Create and fill first part
                MimeBodyPart header = new MimeBodyPart();
                header.setText("Hi " + fac.getName() + ".\n\n");

                //Create and fill second part
                MimeBodyPart body = new MimeBodyPart();
                body.setText("This is a Ad Hoc Production request from " + fac.getName() + ".\n\n");

                //Create and fill third part
                MimeBodyPart linkText = new MimeBodyPart();
                linkText.setText("The inventory level for " + mat.getName() + " is currently running low.");

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

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }
}
