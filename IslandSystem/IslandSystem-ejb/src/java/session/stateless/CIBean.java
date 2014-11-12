/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session.stateless;

import entity.ChatRecord;
import entity.Facility;
import entity.Log;
import entity.Staff;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import static session.stateless.ChatBean.PASSWORD_LENGTH;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author nataliegoh
 */
@Stateful
@LocalBean
public class CIBean implements CIBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager em;
    private static final Random RANDOM = new SecureRandom();
    private Collection<Log> log;
    private Log logEntity;
    private List<ChatRecord> chatRecord;

    public CIBean() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/islanddatabase", "root", "");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CIBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void createStaff(Staff newStaff) throws DetailsConflictException {
        if (!staffExists(newStaff.getEmail())) {
            addLog(newStaff, "Account created.");
            em.persist(newStaff);
            System.err.println("CIBean: createStaff: staffPersist()");
        }
        else {
            throw new DetailsConflictException("Email Exists");
        }
        
    }

    @Override
    public boolean staffExists(String email) {
        Query q;
        q = em.createQuery("SELECT TRUE FROM " + Staff.class.getName() + " s where s.email= :param");
        q.setParameter("param", email);
        return !q.getResultList().isEmpty();
    }

    @Override
    public void addLog(Staff staff, String L) {
        logEntity = new Log();
        logEntity.create(staff.getEmail(), L);
        em.persist(logEntity);
    }

    @Override
    public List<Staff> getAllAcounts() {
        Facility fac = (Facility) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("facility");
        Query q = em.createQuery("SELECT s from " + Staff.class.getName() + " s WHERE s.fac = :fac");
        q.setParameter("fac", fac);
        List<Staff> staffList = new ArrayList();
        for (Object o : q.getResultList()) {
            Staff s = (Staff) o;
            staffList.add(s);
        }
        return staffList;
    }

    @Override
    public String encryptPassword(String email, String password) {
        String encrypted = null;
        if (password == null) {
            return null;
        } else {
            try {
                password = password.concat(email);
                System.err.println("encrypt Password: password = " + password);
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(password.getBytes(), 0, password.length());
                encrypted = new BigInteger(1, md.digest()).toString(16);
                System.err.println("encrypt Password: encrypted = " + encrypted);
            } catch (NoSuchAlgorithmException ex) {
            }
        }
        return encrypted;
    }

    @Override
    public int verifyUser(String email, String password) {
        try {
            //password = encryptPassword(username, password);
            Query q;
            System.out.println("Password entered = " + password);
            q = em.createQuery("SELECT s.password FROM Staff s where s.email= :param");
            q.setParameter("param", email);
            String pwd = (String) q.getSingleResult();
            System.out.println("Password found = " + pwd);
            if (pwd == null) {
                System.out.println("PASSWORD NULL");
                return 0;
            } else {
                System.err.println("SURVIVED");
                if (pwd.equals(password)) {
                    return 1;
                } else {
                    return -1;
                }
            }

        } catch (NoResultException ec) {
            System.err.println("ERROR");
            return 0;
        }
    }

    @Override
    public Staff getStaffDetails(String email) {
        //System.out.println("FMSBean: Entered getStaffDetails method");
        Query q = em.createQuery("SELECT s FROM Staff s where s.email= :param");
        q.setParameter("param", email);
        Staff staff = (Staff) q.getSingleResult();
        //staffEntity = em.find(StaffEntity.class, id);
        return staff;
    }

    @Override
    public String generateRandomPassword() {
    // Pick from some letters that won't be easily mistaken for each
        // other. So, for example, omit o O and 0, 1 l and L.
        String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789!@#$%^&*";

        String pw = "";
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = (int) (RANDOM.nextDouble() * letters.length());
            pw += letters.substring(index, index + 1);
        }
        return pw;
    }

    @Override
    public void changePassword(String email, String currpassword, String newpassword) throws DetailsConflictException {
        currpassword = encryptPassword(email, currpassword);
        Query q = em.createQuery("SELECT s FROM Staff s where s.email= :param");
        q.setParameter("param", email);
        Staff staff = (Staff) q.getSingleResult();
        if (staff.getPassword().equals(currpassword)) {
            newpassword = encryptPassword(email, newpassword);
            staff.setPassword(newpassword);
            Log LOG = new Log();
            LOG.create(email, "Password Changed.");
            staff.getLog().add(LOG);
            em.persist(staff);  
        }
        else {
            throw new DetailsConflictException("Wrong Current Password");
        }
       
        
    }

    @Override
    public List<Log> getUserLog(String email) {
        System.out.println("FMSBean: Entered getUserLog method");
        Query q;
        q = em.createQuery("SELECT d FROM Log d where d.email= :param");
        q.setParameter("param", email);
        List<Log> logList = new ArrayList();
        for (Object o : q.getResultList()) {
            Log d = (Log) o;
            logList.add(d);
        }
        return logList;
    }

    @Override
    public List<Log> getAllLog(Facility fac, Staff staff) {
        System.out.println("FMSBean: Entered getAllLog method");
        Query q;
        q = em.createQuery("SELECT l FROM " + Log.class.getName() + " l, " + Staff.class.getName() + " s WHERE l.email = :email AND l.email = s.email AND s.fac = :fac");
        q.setParameter("email", staff.getEmail());
        q.setParameter("fac", fac);
        List<Log> logList = new ArrayList();
        for (Object o : q.getResultList()) {
            Log d = (Log) o;
            logList.add(d);
        }
        return logList;
    }

    @Override
    public void changeContact(String email, String contact) {
        Query q = em.createQuery("SELECT s FROM Staff s where s.email= :param");
        q.setParameter("param", email);
        //Long id = (Long)q.getSingleResult();
        Staff staff = (Staff) q.getSingleResult();
        staff.setContact(contact);
        Log LOG = new Log();
        LOG.create(email, "Contact Changed.");
        staff.getLog().add(LOG);
        em.persist(staff);
    }

    @Override
    public List<Facility> getAllFacilities() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findAll");
        return query.getResultList();
    }

    @Override
    public Staff getStaffByEmail(String email) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query q = em.createNamedQuery("Staff.findByEmail");
        q.setParameter("email", email);
        return (Staff) q.getSingleResult();
    }
    
    @Override
    public String announcementDate() {
        Query q = em.createQuery("SELECT c FROM " + ChatRecord.class.getName() + " c WHERE c.channel = 'announcement'");
        chatRecord = new ArrayList();
        for (Object o : q.getResultList()) {
            ChatRecord c = (ChatRecord) o;
            chatRecord.add(c);
        }
        int size = chatRecord.size();
        if (size > 0) {
        return chatRecord.get(size-1).getMsgTime() + ":";
        }
        else {
            return "";
        }
    }
    
    @Override
    public String announcementDetails() {
        Query q = em.createQuery("SELECT c FROM " + ChatRecord.class.getName() + " c WHERE c.channel = 'announcement' ");
        chatRecord = new ArrayList();
        for (Object o : q.getResultList()) {
            ChatRecord c = (ChatRecord) o;
            chatRecord.add(c);
        }
        int size = chatRecord.size();
        if (size > 0) {
            return chatRecord.get(size-1).getMessage();
        }
        else {
            return "NO NEW ANNOUNCEMENTS.";
        }
        
    }
    
    @Override
    public void remove(Staff staff) {
        Query q = em.createQuery("DELETE FROM Staff s where s.email= :param");
        q.setParameter("param", staff.getEmail());
        q.executeUpdate();
        em.flush();
        q = em.createQuery("DELETE FROM Log s where s.email= :param");
        q.setParameter("param", staff.getEmail());
        q.executeUpdate();
        em.flush();
    }

}
