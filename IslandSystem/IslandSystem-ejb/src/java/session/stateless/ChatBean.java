/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package session.stateless;

import entity.ChatRecord;
import entity.Staff;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author nataliegoh
 */
@Stateful
public class ChatBean implements ChatBeanRemote{

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager em;
    public static final int PASSWORD_LENGTH = 8;
    private static final Random RANDOM = new SecureRandom();
    private ChatRecord chatlog;
    
    public ChatBean() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/islanddatabase", "root", "");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param chatlog
     */
    @Override
    public void persistChatlog(ChatRecord chatlog) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.merge(chatlog);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ChatRecord> getChat(int type, Staff user) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query;
        
        if (type == 1)
            query = em.createQuery("SELECT c FROM " + ChatRecord.class.getName() + " c WHERE c.channel = 'announcement'");
        else {
            query = em.createQuery("SELECT c FROM " + ChatRecord.class.getName() + " c WHERE "
                    + "(c.channel = 'PM' AND (c.recipient = :user OR c.sender = :user)) "
                    + "OR c.channel = '/{IslandPublic}/'");
            query.setParameter("user", user);
        }
        return query.getResultList();
    }
    
    public Staff getUser(String email) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Staff.findByEmail");
        query.setParameter("email", email);
        
        return (Staff)query.getSingleResult();
    }
}


