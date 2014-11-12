/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managedbean;

import entity.Facility;
import entity.Staff;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import session.stateless.CIBeanLocal;
import util.exception.DetailsConflictException;

/**
 *
 * @author nataliegoh
 */
@ManagedBean(name="createManagedBean")
@SessionScoped  
public class CreateStaffBean implements Serializable {
    @EJB
    private CIBeanLocal fmsbean;
    private String name;
    private String email;
    private String contact;
    private String password;
    private String encrypted;
    private String role1;
    private String role2;
    private String role3;
    private Facility fac;
    private String sendEmailTo;
    private List<Facility> facilities;
    private Staff newStaff;
    
    @ManagedProperty(value="#{sendEmailBean}")
    private SendEmailBean emailManagedBean;

        /**
     * Creates a new instance of createManagedBean
     */
    @PostConstruct
    public void init() {
        
        facilities = fmsbean.getAllFacilities();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("facilities", facilities);
    }
    public CreateStaffBean() {
    }

    public Facility getFac() {
        Long facility = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("fac");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Facility.findById");
        q.setParameter("id", facility);
        return (Facility) q.getSingleResult();
    }

    
    public String getRole1() {
        return role1;
    }

    public void setRole1(String role1) {
        this.role1 = role1;
    }

    public String getRole2() {
        return role2;
    }

    public void setRole2(String role2) {
        this.role2 = role2;
    }

    public String getRole3() {
        return role3;
    }

    public void setRole3(String role3) {
        this.role3 = role3;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword() {
        password = fmsbean.generateRandomPassword();        
    }

    public String getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }

    public SendEmailBean getEmailManagedBean() {
        return emailManagedBean;
    }

    public void setEmailManagedBean(SendEmailBean emailManagedBean) {
        this.emailManagedBean = emailManagedBean;
    }
    
    
    
    //String StaffID, String name, String email, String contact, String username, String password, String roles
    public void create(ActionEvent event) throws DetailsConflictException {
        try {
            name = getName();
            System.err.println("create: name: " + name);
            email = getEmail();
            System.err.println("create: email: " + email);
            contact = getContact();
            System.err.println("create: contact: " + contact);
            setPassword();
            password = getPassword();
            System.err.println("create: password: " + password);
            role1 = getRole1();
            System.err.println("create: role1: " + role1);
            role2 = getRole2();
            System.err.println("create: role2: " + role2);
            role3 = getRole3();
            System.err.println("create: role3: " + role3);
            fac = getFac();
            System.out.println("FAC= "+ fac);

            encrypted = fmsbean.encryptPassword(email.trim(), password.trim());
            System.err.println("create: encrypted = " + encrypted);

            newStaff = new Staff();
            newStaff.setName(name);
            newStaff.setEmail(email);
            newStaff.setContact(contact);
            newStaff.setPassword(encrypted);
            newStaff.setRole1(role1);
            newStaff.setRole2(role2);
            newStaff.setRole3(role3);
            newStaff.setFac(fac);
            fmsbean.createStaff(newStaff);
            sendEmailTo = new String();
            sendEmailTo = name + "<" + email + ">";
            System.err.println("sendEmailTo = " + sendEmailTo);
            System.err.println("sending email to " + sendEmailTo + " password: " + password);
            emailManagedBean.emailMyEvents(name, sendEmailTo, password);
            System.err.println("emailManagedBean: email sent to " + email + " password: " + password);
            System.out.println("FAC2= "+fac);
            //fmsbean.addLog(email, "Account created.");
            //fmsbean.addRoles(role1, role2, role3);
            //fmsbean.addRoles(email, role2);
            //fmsbean.addRoles(email, role3);
            //fmsbean.staffPersist();
            //System.err.println("createManagedBean: staffPersist()");
            //emailManagedBean.emailMyEvents(name, email, password);
            //System.err.println("emailManagedBean: email sent to " + email + " password: " + password);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success! Account Created.", "Account Created!"));
            //ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //ec.redirect(ec.getRequestContextPath() + "/index.xhtml");
            
            setName(null);
            setEmail(null);
            setContact(null);
            setRole1(null);
            setRole2(null);
            setRole3(null);
        } catch (DetailsConflictException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "User Exists!", "User Exists"));
        }
            
            
    }
}