///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package session.stateless;
//
//import entity.Campaign;
//import entity.Country;
//import entity.Customer;
//import entity.CustomerCampaignMetric;
//import entity.CustomerMetric;
//import entity.Item;
//import entity.Material;
//import entity.Region;
//import entity.RegionItemPrice;
//import enumerator.BusinessArea;
//import enumerator.CustomerCampaignCat;
//import enumerator.Gender;
//import java.math.BigInteger;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//import java.util.Random;
//import javax.ejb.EJBException;
//import javax.ejb.LocalBean;
//import javax.ejb.Stateless;
//import javax.mail.Message;
//import javax.mail.Multipart;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.NoResultException;
//import javax.persistence.Query;
//import util.SMTPAuthenticator;
//import util.exception.DetailsConflictException;
//
///**
// *
// * @author Anna
// */
//@Stateless
//@LocalBean
//public class EComBean {//implements EComBeanLocal{
//    //@Override
//
//    public Boolean unsubscribe(Customer customer) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        customer.setUnsubscribed(true);
//        try {
//            em.merge(customer);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public Boolean resetPassword(String email) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        Query q = em.createQuery("SELECT c FROM Customer c WHERE c.email = :email");
//        q.setParameter("email", email);
//        try {
//            Customer customer = (Customer) q.getSingleResult();
//            String name = customer.getName();
//            String password = generateRandomPassword();
//            customer.setPassword(encryptPassword(email, password));
//            em.merge(customer);
//            emailPassword(name, email, password);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public String generateRandomPassword() {
//        // Pick from some letters that won't be easily mistaken for each
//        // other. So, for example, omit o O and 0, 1 l and L.
//        String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789!@#$%^&*";
//        Random RANDOM = new SecureRandom();
//
//        String pw = "";
//        for (int i = 0; i < 8; i++) {
//            int index = (int) (RANDOM.nextDouble() * letters.length());
//            pw += letters.substring(index, index + 1);
//        }
//        return pw;
//    }
//
//    public void emailPassword(String name, String email, String password) {
//        String emailServerName = "mailauth.comp.nus.edu.sg";
//        String emailFromAddress = "Island Furniture System Administrator <a0101309@u.nus.edu>";
//        String mailer = "JavaMailer";
//        String toEmailAddress = email;
//
//        try {
//            Properties props = new Properties();
//            props.put("mail.transport.protocol", "smtp");
//            props.put("mail.smtp.host", emailServerName);
//            props.put("mail.smtp.port", "25");
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//            props.put("mail.smtp.debug", "true");
//            javax.mail.Authenticator auth = new SMTPAuthenticator();
//            Session session = Session.getInstance(props, auth);
//            session.setDebug(true);
//            Message msg = new MimeMessage(session);
//            if (msg != null) {
//                msg.setFrom(InternetAddress.parse(emailFromAddress, false)[0]);
//                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress, false));
//                msg.setSubject("Welcome to Island Furniture!" + "\n\n");
//
//                //Create and fill first part
//                MimeBodyPart header = new MimeBodyPart();
//                header.setText("Welcome to Island Furniture, " + name + ".\n\n");
//
//                //Create and fill second part
//                MimeBodyPart body = new MimeBodyPart();
//                body.setText("Here's your newly reset password: " + password + "\n\n");
//
//                //Create and fill third part
//                MimeBodyPart linkText = new MimeBodyPart();
//                linkText.setText("You can log in with this new password at our website. Thank you.");
//
//                //Create the Multipart
//                Multipart mp = new MimeMultipart();
//                mp.addBodyPart(header);
//                mp.addBodyPart(body);
//                mp.addBodyPart(linkText);
//
//                //Set Message Content
//                msg.setContent(mp);
//
//                //String messageText = "Welcome to Island Furniture Family, " +name+ ".\n\n Here's the autogenerated password: " + password +"\n\n";
//                //msg.setText(messageText);
//                //msg.setDisposition(Part.INLINE);
//                msg.setHeader("X-Mailer", mailer);
//                Date timeStamp = new Date();
//                msg.setSentDate(timeStamp);
//                Transport.send(msg);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new EJBException(e.getMessage());
//        }
//    }
//
//    public void changePassword(String email, String currpassword, String newpassword) throws DetailsConflictException {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        currpassword = encryptPassword(email, currpassword);
//        Query q = em.createQuery("SELECT c FROM Customer c where c.email= :param");
//        q.setParameter("param", email);
//        Customer customer = (Customer) q.getSingleResult();
//        if (customer.getPassword().equals(currpassword)) {
//            newpassword = encryptPassword(email, newpassword);
//            customer.setPassword(newpassword);
//            em.merge(customer);
//        } else {
//            throw new DetailsConflictException("Wrong Current Password");
//        }
//
//    }
//
//    public String encryptPassword(String email, String password) {
//        String encrypted = null;
//        if (password == null) {
//            return null;
//        } else {
//            try {
//                password = password.concat(email);
//                System.err.println("encrypt Password: password = " + password);
//                MessageDigest md = MessageDigest.getInstance("MD5");
//                md.update(password.getBytes(), 0, password.length());
//                encrypted = new BigInteger(1, md.digest()).toString(16);
//                System.err.println("encrypt Password: encrypted = " + encrypted);
//            } catch (NoSuchAlgorithmException ex) {
//            }
//        }
//        return encrypted;
//
//    }
//
//    public Boolean verifyUser(String email, String password) {
//        password = encryptPassword(email, password);
//        try {
//            if (password.equals("")) {
//                return false;
//            } else {
//                EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//                EntityManager em = emf.createEntityManager();
//                Query q;
//                q = em.createQuery("SELECT c.password FROM Customer c WHERE c.email= :param");
//                q.setParameter("param", email);
//                String pwd = (String) q.getSingleResult();
//                if (!(pwd.equals(password))) {
//                    return false;
//                } else {
//                    return true;
//                }
//            }
//        } catch (NoResultException ec) {
//            System.err.println("ERROR");
//            return false;
//        }
//    }
//
//    public List<RegionItemPrice> getFurnitureDisplays(long regionId) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//
//        Query q = em.createNamedQuery("Region.findById");
//        q.setParameter("id", regionId);
//
//        Region region = (Region) q.getSingleResult();
//
//        System.out.println("IN GET FURNITURE!");
//        System.out.print("REGION " + region);
//        Query query = em.createQuery("SELECT r FROM " + RegionItemPrice.class.getName() + " r WHERE r.region =:region AND r.item IN (SELECT m FROM " + Material.class.getName() + " m WHERE NOT m.id = 64)");
//        query.setParameter("region", region);
//
//        System.out.println("IN GET FURNITURE 2!");
//
//        return query.getResultList();
//    }
//
//    //@Override
//    public Material getItem(Long itemId) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//
//        System.out.println("itemid: !" + itemId);
//        Query q = em.createNamedQuery("Material.findById");
//        q.setParameter("id", itemId);
//
//        Material result = (Material) q.getSingleResult();
//        return result;
//    }
//
//    //@Override
//    public Material getItembyString(String itemName) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//
//        Query q = em.createNamedQuery("Material.findByName");
//        q.setParameter("name", itemName);
//
//        try {
//            Material result = (Material) q.getSingleResult();
//            System.out.println("IN GET ITEM!");
//            System.out.println(result);
//            return result;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return null;
//        }
//
//    }
//
//    //@Override
//    public List<Region> getRegions() {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//
//        Query q = em.createNamedQuery("Region.findAll");
//
//        System.out.println("IN GET REGION!");
//        System.out.println(q.getResultList().size());
//        return q.getResultList();
//    }
//
//    //@Override
//    public Region getRegion(long regionId) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//
//        Query q = em.createNamedQuery("Region.findById");
//        q.setParameter("id", regionId);
//
//        return (Region) q.getSingleResult();
//    }
//
//    //@Override
//    public RegionItemPrice getRegionPriceRecord(Item item, Region region) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//
//        Query q = em.createNamedQuery("RegionItemPrice.findByRegionItem");
//        q.setParameter("region", region);
//        q.setParameter("item", item);
//
//        return (RegionItemPrice) q.getSingleResult();
//    }
//
//    //@Override
//    public List<Item> getItems(long regionId) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//
//        System.out.println("IN GET ITEMS!");
//
//        Integer genCategory = 0;
//        List<Item> resItems = new ArrayList<Item>();
//
//        Query q = em.createNamedQuery("Region.findById");
//        q.setParameter("id", regionId);
//
//        Region resultRegion = (Region) q.getSingleResult();
//
//        Query query = em.createQuery("SELECT r FROM " + RegionItemPrice.class.getName() + " r WHERE r.region =:region AND r.item IN (SELECT m FROM " + Material.class.getName() + " m WHERE NOT m.genCategory = :genCategory)");
//        query.setParameter("region", resultRegion);
//        query.setParameter("genCategory", genCategory);
//        List<RegionItemPrice> mats = query.getResultList();
//
//        for (RegionItemPrice m : mats) {
//            System.out.println(m.getId());
//            resItems.add(m.getItem());
//        }
//
//        return resItems;
//    }
//
//    //@Override
//    public void unsubscribe(long custId) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        Query q = em.createQuery("SELECT c FROM Customer c where c.id= :param");
//        q.setParameter("param", custId);
//        Customer customer = (Customer) q.getSingleResult();
//        customer.setUnsubscribed(true);
//        em.persist(customer);
//    }
//
//    //@Override
//    public Long addNewCustomer(Customer customer) throws DetailsConflictException {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        String email = customer.getEmail();
//        if (!emailAlreadyExist(email)) {
//            try {
//                customer.setPassword(encryptPassword(customer.getEmail(), customer.getPassword()));
//                em.persist(customer);
//                createCustomerMetrics(customer);
//                checkOngoingCampaign(customer);
//                return customer.getId();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//
//                return null;
//
//            } finally {
//                em.close();
//
//            }
//        } else {
//            throw new DetailsConflictException("Email exists: " + customer.getEmail());
//        }
//    }
//
//    private void createCustomerMetrics(Customer customer) {
//        CustomerMetric cmkit = new CustomerMetric();
//        CustomerMetric cmmat = new CustomerMetric();
//        CustomerMetric cmprod = new CustomerMetric();
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        cmmat.setCustomer(customer);
//        cmkit.setCustomer(customer);
//        cmprod.setCustomer(customer);
//
//        cmmat.setBusinessArea(BusinessArea.FURNITURE);
//        cmprod.setBusinessArea(BusinessArea.PRODUCT);
//        cmkit.setBusinessArea(BusinessArea.KITCHEN);
//
//        cmmat.setCurrentRfm(0);
//        cmkit.setCurrentRfm(0);
//        cmprod.setCurrentRfm(0);
//
//        cmprod.setHighestRfm(0);
//        cmkit.setHighestRfm(0);
//        cmmat.setHighestRfm(0);
//
//        cmmat.setInactive(false);
//        cmkit.setInactive(false);
//        cmprod.setInactive(false);
//
//        em.persist(cmkit);
//        em.persist(cmprod);
//        em.persist(cmmat);
//    }
//
//    private void checkOngoingCampaign(Customer customer) {
//        System.out.println("checkOngoingCampaign initialized");
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        Date currDate = Calendar.getInstance().getTime();
//        Integer custAge = translateDobToAge(customer.getDob());
//        System.out.println("custAge: " + custAge);
//
//        Gender gender = customer.getGender();
//        System.out.println("gender: " + gender);
//        Region region = customer.getRegion();
//        System.out.println("region: " + region.getId());
//
//        Query q = em.createQuery("SELECT c FROM Campaign c WHERE c.region = :region AND c.targetNew = :true "
//                + "AND ( :custAge BETWEEN c.lowerAge AND c.upperAge ) AND ( :currDate BETWEEN c.startDate AND c.endDate)");
//        q.setParameter("region", region);
//        q.setParameter("custAge", custAge);
//        q.setParameter("currDate", currDate);
//        q.setParameter("true", true);
//        if (!q.getResultList().isEmpty()) {
//            List<Campaign> campaigns = (List<Campaign>) q.getResultList();
//            for (Campaign camp : campaigns) {
//                if (camp.getTargetGender().equals(Gender.ALL) || camp.getTargetGender().equals(customer.getGender())) {
//                    CustomerCampaignMetric ccm = new CustomerCampaignMetric();
//                    ccm.setCampaign(camp);
//                    ccm.setCustomer(customer);
//                    ccm.setCustomerCampaignCat(CustomerCampaignCat.NEW);
//                    ccm.setNumHits(0);
//                    ccm.setNumPromoCodeUsed(0);
//                    em.persist(ccm);
//                }
//            }
//        }
//    }
//
//    public Integer translateDobToAge(Date date) {
//        Calendar currDate = Calendar.getInstance();
//        Integer currYear = currDate.get(Calendar.YEAR);
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        Integer bornYear = cal.get(Calendar.YEAR);
//        return currYear - bornYear;
//    }
//
//    public Customer getCustomerDetails(String email) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        Query q = em.createQuery("SELECT c FROM Customer c where c.email= :param");
//        q.setParameter("param", email);
//        Customer customer = (Customer) q.getSingleResult();
//        return customer;
//    }
//
//    private Boolean emailAlreadyExist(String email) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//
//        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.email = :email");
//        query.setParameter("email", email);
//        List resultList = query.getResultList();
//        if (resultList.isEmpty()) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    //@Override
//    public List<Customer> getAllCustomers() {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        Query query = em.createNamedQuery("Customer.findAll");
//        return query.getResultList();
//    }
//
//    //@Override
//    public List<Country> getAllCountries() {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        Query query = em.createNamedQuery("Country.findAll");
//        return query.getResultList();
//    }
//}
