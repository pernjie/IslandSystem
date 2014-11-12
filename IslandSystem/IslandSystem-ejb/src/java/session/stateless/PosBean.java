/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package session.stateless;

import entity.Campaign;
import entity.Country;
import entity.Customer;
import entity.Facility;
import entity.Item;
import entity.Material;
import entity.Region;
import entity.RegionItemPrice;
import entity.RegionServicePrice;
import entity.Service;
import entity.TransactionItem;
import entity.TransactionRecord;
import entity.TransactionService;
import enumerator.BusinessArea;
import enumerator.TenderType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author pern
 */
@Stateless
@WebService
public class PosBean implements PosBeanRemote {
    @Override
    public int login(String email, String password) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        try {
            password = encryptPassword(email, password);
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
    
    private String encryptPassword(String email, String password) {
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
    public Facility getStore(String email) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q;
        q = em.createQuery("SELECT s.fac FROM Staff s WHERE s.email= :param");
        q.setParameter("param", email);
        Facility store = (Facility) q.getSingleResult();
        return store;
    }
    
    @Override
    public Item getItem(String id, String type) {
        System.out.println("ID IS " + id);
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();
            Query query = em.createNamedQuery("Item.findById");
            query.setParameter("id", Long.parseLong(id,10));
            Item item = (Item) query.getSingleResult();
            
            if (type.equals("Furniture"))  {
                if (item.getItemType().equals("Material") || item.getItemType().equals("Product"))
                    return item;
                else
                    return null;
            } else if (type.equals("Kitchen"))  {
                if (item.getItemType().equals("SetItem") || item.getItemType().equals("MenuItem"))
                    return item;
                else
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
    
    @Override
    public Service getService(String id) {
        System.out.println("ID IS " + id);
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();
            Query query = em.createNamedQuery("Service.findById");
            query.setParameter("id", Long.parseLong(id,10));
            Service service = (Service) query.getSingleResult();
            return service;
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public Customer getCustomer(String card) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q;
        System.out.println("card:" + card);
        q = em.createQuery("SELECT c FROM " + Customer.class.getName() + " c WHERE c.loyaltyCard = :card");
        q.setParameter("card", card);
        try {
            Customer cust = (Customer) q.getSingleResult();
            return cust;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @Override
    public Region getRegion(Facility store) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q;
        q = em.createQuery("SELECT f.region FROM " + Facility.class.getName() + " f WHERE f = :store");
        q.setParameter("store", store);
        Region region  = (Region) q.getSingleResult();
        return region;
    }
    
    @Override
    public Double getItemPrice(Item item, Region region) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q;
        q = em.createQuery("SELECT r.price FROM " + RegionItemPrice.class.getName() + " r WHERE r.item = :item AND r.region = :region");
        q.setParameter("region", region);
        q.setParameter("item", item);
        Double price = (Double) q.getSingleResult();
        return price;
    }
    
    @Override
    public Double getServicePrice(Service service, Region region) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q;
        q = em.createQuery("SELECT r.price FROM " + RegionServicePrice.class.getName() + " r WHERE r.svc = :service AND r.region = :region");
        q.setParameter("region", region);
        q.setParameter("service", service);
        Double price = (Double) q.getSingleResult();
        return price;
    }
    
    @Override
    public boolean redeemCake(Customer cust) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q;
        q = em.createQuery("SELECT c FROM " + Customer.class.getName() + " c WHERE c = :cust");
        q.setParameter("cust", cust);
        Customer cust2 = (Customer) q.getSingleResult();
        cust2.setRedeemedCake(true);
        try {
            em.merge(cust2);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean verifyPromo(String code, Region region, String type) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q;
        q = em.createQuery("SELECT 1 FROM " + Campaign.class.getName() + " c WHERE c.promoCode = :code AND c.region = :region AND c.businessArea = :type");
        q.setParameter("code", code);
        q.setParameter("region", region);
        switch (type) {
            case "Furniture":q.setParameter("type", BusinessArea.FURNITURE);break;
            case "Kitchen":q.setParameter("type", BusinessArea.KITCHEN);break;
            default:q.setParameter("type", BusinessArea.FURNITURE);break;
        }
        return (!q.getResultList().isEmpty());
    }
    
    @Override
    public TransactionRecord addTransactionRecord(Facility store, List<TransactionItem> transitems, List<TransactionService> transservices, 
            List<Item> items, List<Service> services,
            Double amount, String promo, int tender, Customer cust, boolean redeemed) {
        TransactionRecord tr = new TransactionRecord();
        tr.setGrossTotalAmount(amount);
        Double discount = 0.0;
        Double tax = 0.0;
        System.out.println("amt: " + amount);
        if (promo != null) {
            discount = getPromoDiscount(amount,promo);
        }
        else if (redeemed) {
            discount = getRedemptionDiscount(amount);
            tr.setRedemption(true);
        }
        tr.setDiscountAmount(round(discount,2));
        amount -= discount;
        tax = getTaxAmount(amount,store);
        tr.setTaxAmount(round(tax,2));
        amount -= tax;
        for (TransactionItem ti : transitems) {
            ti.setTransact(tr);
        }
        for (TransactionService ts : transservices) {
            ts.setTransact(tr);
        }
        for (int i=0;i<transitems.size();i++) {
            transitems.get(i).setItem(items.get(i));
        }
        for (int i=0;i<transservices.size();i++) {
            transservices.get(i).setService(services.get(i));
        }
        tr.setNetTotalAmount(round(amount,2));
        Calendar cal = Calendar.getInstance();
        tr.setTransactTime(cal.getTime());
        tr.setFac(store);
        if (cust != null) {
            tr.setCust(cust);
            editCustomerPoints(cust,tr.getGrossTotalAmount());
            if (redeemed)
                editCustomerPoints(cust,-500.0);
        }
        
        switch (tender) {
            case 0:
                tr.setTenderType(TenderType.CASH);
                break;
            case 1:
                tr.setTenderType(TenderType.CREDIT_CARD);
                break;
            case 2:
                tr.setTenderType(TenderType.NETS);
                break;
        }
        tr.setCollected(false);
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            tr.setId(getId(tr));
            em.persist(tr);
//            for (TransactionItem ti : transitems) {
//                ti.setId(getId(ti));
//                em.persist(ti);
//            }
            for (int i=0;i<transitems.size();i++) {
                transitems.get(i).setId(getId(transitems.get(i))+i);
                em.persist(transitems.get(i));
            }
            for (int i=0;i<transservices.size();i++) {
                transservices.get(i).setId(getId(transservices.get(i))+i);
                em.persist(transservices.get(i));
            }
//            for (TransactionService ts : transservices) {
//                ts.setId(getId(ts));
//                em.persist(ts);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
            return tr;
        }
    }
 
    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    private Long getId(Object object) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT MAX(o.id) FROM " + object.getClass().getName() + " AS o");
        Long id = (Long) (q.getSingleResult());
        if (id != null) {
            return ++id;
        } else {
            return 0L;
        }

    }
    
    private void editCustomerPoints(Customer cust, double points) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q;
        q = em.createQuery("SELECT c FROM " + Customer.class.getName() + " c WHERE c = :cust");
        q.setParameter("cust", cust);
        Customer cust2 = (Customer) q.getSingleResult();
        cust2.setPoints(cust2.getPoints()+(int)(Math.floor(points)));
        if (cust2.getPoints()>=150 && !cust2.getPlus())
            cust2.setPlus(true);
        em.merge(cust2);
    }
    
    private Double getPromoDiscount(Double amount, String promo) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q;
        q = em.createQuery("SELECT c.percentDisc FROM " + Campaign.class.getName() + " c "
                + "WHERE c.promocode = :promo");
        q.setParameter("promo", promo);
        Double disc = (Double) q.getSingleResult();
        return amount * (disc/100);
    }

    private Double getRedemptionDiscount(Double amount) {
        return (amount>5.0) ? 5:amount;
    }
    
    private Double getTaxAmount(Double amount, Facility store) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q;
        q = em.createQuery("SELECT c.taxPercent FROM " + Country.class.getName() + " c, " + Facility.class.getName() + " f "
                + "WHERE f.country = c AND f = :store");
        q.setParameter("store", store);
        Double tax = (Double) q.getSingleResult();
        return amount * (tax/100);
    }
}