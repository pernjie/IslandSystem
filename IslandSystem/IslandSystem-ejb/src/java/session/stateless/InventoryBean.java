/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session.stateless;

import classes.WeekHelper;
import entity.DeliverySchedule;
import entity.DistributionMFtoStore;
import entity.DistributionMFtoStoreProd;
import entity.Facility;
import entity.InventoryMaterial;
import entity.InventoryProduct;
import entity.Item;
import entity.Material;
import entity.PoItem;
import entity.PoRecord;
import entity.Product;
import entity.Shelf;
import entity.ShelfSlot;
import entity.ShelfType;
import entity.Staff;
import entity.Supplier;
import entity.SuppliesProdToFac;
import enumerator.InvenLoc;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import util.exception.DetailsConflictException;
import util.exception.EntityDneException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author MY ASUS
 */
@Stateless
@LocalBean
public class InventoryBean {
    private Facility fac;

    public Facility getFac(String loggedInEmail) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Staff.findByEmail");
        query.setParameter("email", loggedInEmail);
        return ((Staff) query.getSingleResult()).getFac();
    }

    public Facility getFacility(long fid) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findById");
        query.setParameter("id", fid);
        return (Facility) query.getSingleResult();
    }

    public List<Supplier> getSuppliers(Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT DISTINCT s FROM " + Supplier.class.getName() + " s, " + SuppliesProdToFac.class.getName() + " sf WHERE sf.fac = :fac AND s = sf.sup");
        query.setParameter("fac", fac);
        return query.getResultList();
    }

    public List<Facility> getMatFacilities(Facility fac) {
        System.err.println("looking for facilities..");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT DISTINCT f FROM " + Facility.class.getName() + " f, " + DistributionMFtoStore.class.getName() + " d WHERE d.store = :fac AND f = d.mf");
        query.setParameter("fac", fac);
        return query.getResultList();
    }

        public List<Facility> getProdFacilities(Facility fac) {
        System.err.println("looking for facilities..");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT DISTINCT f FROM " + Facility.class.getName() + " f, " + DistributionMFtoStoreProd.class.getName() + " d WHERE d.store = :fac AND f = d.mf");
        query.setParameter("fac", fac);
        return query.getResultList();
    }
        
    public Supplier getSupplier(long sid) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Supplier.findById");
        query.setParameter("id", sid);
        return (Supplier) query.getSingleResult();
    }
    
    public List<PoItem> getPoItem(Facility fac, Supplier sup, Date currDate) {
        System.err.println("function: getPoItem()");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        WeekHelper wh = new WeekHelper();
        Query query = em.createQuery("SELECT DISTINCT pm FROM " + PoItem.class.getName() + " pm, " + PoRecord.class.getName() + " pr "
                + "WHERE pr = pm.po AND pr.fac = :fac AND pr.sup = :sup AND pm.deliveryDate = (SELECT MIN(deliveryDate) FROM " + PoRecord.class.getName() + " pr2 WHERE pr2.status = 'sent') "
                + "AND (pm.status = 'Ordered' " + " OR pm.status = 'Late')");
        query.setParameter("fac", fac);
        query.setParameter("sup", sup);
        return query.getResultList();
    }

    public List<DeliverySchedule> getDeliveryItem(Facility fac, Facility sup) {
        System.err.println("function: getPoItem2()");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        WeekHelper wh = new WeekHelper();
        Query query = em.createQuery("SELECT DISTINCT ds FROM " + DeliverySchedule.class.getName() + " ds "
                + "WHERE ds.store = :fac AND ds.mf = :sup AND ds.status = 'sent'");
        query.setParameter("fac", fac);
        query.setParameter("sup", sup);
        return query.getResultList();
    }
    
    public String getMatName(Long matId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Item.findById");
        query.setParameter("id", matId);
        return ((Item) query.getSingleResult()).getName();
    }

    public boolean persistPoItem(PoItem pi) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        System.err.println("persisting poitem..");
        try {
            em.merge(pi);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
            System.err.println("poitem persisted!");
            return true;
        }
    }

     public InventoryMaterial getInventoryMat(Item mat, Facility fac, InvenLoc location) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT im FROM " + InventoryMaterial.class.getName() + " im WHERE im.mat = :mat AND im.fac = :fac AND im.location = :location");
        query.setParameter("fac", fac);
        query.setParameter("mat", mat);
        query.setParameter("location", location);
        List<InventoryMaterial> imList = query.getResultList();
        if(imList.isEmpty()) {
            System.err.println("no invmat found");
            return null;
        }
        InventoryMaterial invMat = imList.get(0);
        if(imList.size() > 1) {
            System.err.println("multiple invmat found for " + mat.getName());
            for(InventoryMaterial im : imList) {
                if(invMat.getQuantity()<im.getQuantity() && im.getQuantity()!=0) {
                    invMat = im;
                }
            }
        }
        System.err.println("invmat: " + invMat.getId());
        return invMat;
    }

     public InventoryProduct getInventoryProd(Item mat, Facility fac, InvenLoc location) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT im FROM " + InventoryProduct.class.getName() + " im WHERE im.mat = :mat AND im.fac = :fac AND im.location = :location");
        query.setParameter("fac", fac);
        query.setParameter("mat", mat);
        query.setParameter("location", location);
        List<InventoryProduct> imList = query.getResultList();
        if(imList.isEmpty()) {
            System.err.println("no invmat found");
            return null;
        }
        InventoryProduct invMat = imList.get(0);
        if(imList.size() > 1) {
            System.err.println("multiple invmat found");
            for(InventoryProduct im : imList) {
                if(invMat.getQuantity()<im.getQuantity() && im.getQuantity()!=0) {
                    invMat = im;
                }
            }
        }
        return invMat;
    }
    public boolean updateInventory(InventoryMaterial mat) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        //System.err.println("persisting inventorymaterial..");
        try {
            em.merge(mat);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
            //System.err.println("inventorymaterial persisted!");
            return true;
        }
    }

    public boolean updateDeliverySchedule(DeliverySchedule ds) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        //System.err.println("persisting inventorymaterial..");
        try {
            em.merge(ds);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
            //System.err.println("inventorymaterial persisted!");
            return true;
        }
    }
     
    public boolean updateInventoryMat(InventoryMaterial mat) {
        System.err.println("updateinventory():");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        //System.err.println("persisting inventorymaterial..");
        try {
            em.merge(mat);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
            //System.err.println("inventorymaterial persisted!");
            return true;
        }
    }

     public boolean updateInventoryProd(InventoryProduct mat) {
        System.err.println("updateinventory():");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        //System.err.println("persisting inventorymaterial..");
        try {
            em.merge(mat);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
            //System.err.println("inventorymaterial persisted!");
            return true;
        }
    }
     
     public ShelfSlot getAvailableShelfSlot(Facility fac, InvenLoc location) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT s FROM " + ShelfSlot.class.getName() + " s WHERE "
                + "s.shelf.fac = :fac AND s.shelf.location = :location AND s.occupied = '0'");
        query.setParameter("fac", fac);
        query.setParameter("location", location);
        if(query.getResultList().isEmpty()) {
            return null;
        }
        else {
            return (ShelfSlot) query.getResultList().get(0);
        }
    }
    
    public ShelfSlot getAvailableShelfSlot(Long id, Facility fac, InvenLoc location) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT s FROM " + ShelfSlot.class.getName() + " s, " + InventoryMaterial.class.getName() + " im WHERE "
                + "im.mat.id = :id AND s.shelf.fac = :fac AND s.shelf.zone = im.shelf.zone AND s.shelf.location = :location AND s.occupied = '0'");
        query.setParameter("fac", fac);
        query.setParameter("id", id);
        query.setParameter("location", location);
        if(query.getResultList().isEmpty()) {
            System.err.println("query available shelf slot not found");
            return null;
        }
        else {
            return (ShelfSlot) query.getResultList().get(0);
        }
    }
    
    public ShelfSlot getOtherShelfSlot(Long id, Facility fac, InvenLoc location) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT s FROM " + ShelfSlot.class.getName() + " s, " + InventoryMaterial.class.getName() + " im WHERE "
                + "im.mat.id = :id AND s.shelf.fac = :fac AND s.shelf.location = :location AND s.occupied = '0'");
        query.setParameter("fac", fac);
        query.setParameter("id", id);
        query.setParameter("location", location);
        if(query.getResultList().isEmpty()) {
            return null;
        }
        else {
            return (ShelfSlot) query.getResultList().get(0);
        }
    }
    
    public boolean persistInventoryMaterial(InventoryMaterial im) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        //System.err.println("persisting inventorymaterial..");
        try {
            em.persist(im);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
            //System.err.println("inventoryMterial persisted!");
            return true;
        }
    }
    
    public boolean persistInventoryProduct(InventoryProduct im) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        //System.err.println("persisting inventorymaterial..");
        try {
            em.persist(im);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
            //System.err.println("inventoryMterial persisted!");
            return true;
        }
    }
    
     public List<Shelf> getZoneShelfEntitiesFromFac(){
       EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
       EntityManager em = emf.createEntityManager();
        
        String loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        Query q = em.createNamedQuery("Staff.findByEmail");
        q.setParameter("email", loggedInEmail);
        
         System.out.println("email: "+ loggedInEmail);
         Staff temp =(Staff) q.getSingleResult();
          fac = temp.getFac();
         
        System.out.println("FACID: "+fac.getId());

        Query query = em.createNamedQuery("Shelf.getZone");
        query.setParameter("fac",fac);
        
        System.out.println("SIZE of getZoneShelfEntitiesFromFac: "+ query.getResultList().size());
     
        return query.getResultList();
    }
    
    
    public List<Shelf> getShelfEntities(String zoneName){
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
       
        Boolean occupied = false;
        String zone = zoneName;
        
        
        String qStr = "SELECT * FROM Shelf WHERE id IN (SELECT shelf FROM ShelfSlot WHERE occupied = '"+occupied+"'  GROUP BY shelf) AND zone =  '"+zone+"'";
        
        Query query = em.createNativeQuery(qStr, Shelf.class);
        
        System.out.println("QUERY LIST SIZE: " + query.getResultList().size());
        return query.getResultList();
    }
    
     public List<ShelfSlot> getShelfSlots(Long shelfNum){
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
       
        Boolean occupied = false;
        
        
        String qStr = "SELECT * FROM ShelfSlot WHERE shelf IN (SELECT id FROM shelf WHERE id = '" +shelfNum+"' ) AND occupied = '"+occupied+"'";
                
           
        
        Query query = em.createNativeQuery(qStr, ShelfSlot.class);
        
        System.out.println("QUERY LIST SIZE (ShelfSLot): " + query.getResultList().size());
        return query.getResultList();
    }
     
     public ShelfType getShelfType(Long shelfNum){
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
       
       Query q = em.createNamedQuery("Shelf.findById");
       q.setParameter("id", shelfNum);
       
       Shelf tempShelf = (Shelf) q.getSingleResult();
       
        
        System.out.println("RESULT IN getShelfType: " + tempShelf.getId());
          System.out.println("RESULT IN getShelfType: " + tempShelf.getShelfType());
          return tempShelf.getShelfType();
    }
     
      public List<Item> getRawMats(){
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
       
        Query q = em.createNamedQuery("Material.findAllRawMat");
        return q.getResultList();
      }
      
    public List<Item> getFurnitures(){
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
       
        System.out.println("IN GET FURNITURE!");
       Query q = em.createNamedQuery("Material.findAllMaterial");
       
       List<Item> mats= q.getResultList();
       
       System.out.println("IN GET FURNITURE 2!");
       for(Item m: mats)
           System.out.println(m.getId());
       
          return q.getResultList();
    }
    
     public List<InventoryProduct> getAllRetails(){
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
       
       String loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        Query q = em.createNamedQuery("Staff.findByEmail");
        q.setParameter("email", loggedInEmail);
        
         System.out.println("email: "+ loggedInEmail);
         Staff temp =(Staff) q.getSingleResult();
          fac = temp.getFac();
         
        System.out.println("FACID: "+fac.getId());
        
       Query query = em.createNamedQuery("InventoryProduct.findByFac");
       query.setParameter("fac",fac);
          return query.getResultList();
    }
     
     public List<InventoryMaterial> getAllInvenFurns(){
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        
       String loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        Query q = em.createNamedQuery("Staff.findByEmail");
        q.setParameter("email", loggedInEmail);
        
         System.out.println("email: "+ loggedInEmail);
         Staff temp =(Staff) q.getSingleResult();
          fac = temp.getFac();
         
        System.out.println("FACID: "+fac.getId());
        
        Integer genCategory = 0; 
         
        Query query = em.createQuery("SELECT im FROM " + InventoryMaterial.class.getName() + " im WHERE im.fac = :fac AND im.mat NOT IN (SELECT m FROM " + Material.class.getName() + " m WHERE m.genCategory = :genCategory)");
        query.setParameter("genCategory", genCategory);
        query.setParameter("fac", fac);
        
        System.out.println("IN INVENFURNS: "+ query.getResultList().size());
        
        return query.getResultList();
    }
    
      public List<InventoryMaterial> getAllInvenMats(){
          
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
       
       String loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        Query q = em.createNamedQuery("Staff.findByEmail");
        q.setParameter("email", loggedInEmail);
        
         System.out.println("email: "+ loggedInEmail);
         Staff temp =(Staff) q.getSingleResult();
          fac = temp.getFac();
         
        System.out.println("FACID: "+fac.getId());
        
        Integer genCategory= 0; 
           
        Query query = em.createQuery("SELECT im FROM " + InventoryMaterial.class.getName() + " im WHERE im.fac = :fac AND im.mat IN (SELECT m FROM " + Material.class.getName() + " m WHERE m.genCategory = :genCategory)");
        query.setParameter("genCategory", genCategory);
        query.setParameter("fac", fac);
        
        
        List<InventoryMaterial> results = query.getResultList();
        
        System.out.println("IN INVENMATS: "+ results.size());
        
        return results;
    }
      
 public Map<String,Double> calcThresValues(Double slotLength, Double slotBreadth, Double slotHeight, Double itemLength, Double itemBreadth, Double itemHeight ){
     Map<String,Double> finalvalues = new HashMap<String, Double>();
     
     Integer upperThreshold;
     Integer lowerThreshold;
     Integer temp;
     Double finallength = itemLength;
     Double finalbreadth = itemBreadth;
     Double finalheight = itemHeight;
     
     System.out.println("itemLength "+ itemLength);
     System.out.println("itemHeight "+ itemHeight);
     System.out.println("itemBreadth "+ itemBreadth);
     
     upperThreshold = (int) ((slotLength/itemLength) * (slotBreadth/itemBreadth) * (slotHeight/itemHeight));

		temp = (int) ((slotLength/itemLength) * (slotBreadth/itemHeight) * (slotHeight/itemBreadth));
		if (temp > upperThreshold) {
			upperThreshold = temp;
			finallength = itemLength;
			finalbreadth = itemHeight;
			finalheight = itemBreadth;

		}

		temp = (int) ((slotLength/itemBreadth) * (slotBreadth/itemLength) * (slotHeight/itemHeight));
		if (temp > upperThreshold) {
			upperThreshold = temp;
			finallength = itemBreadth;
			finalbreadth = itemLength;
			finalheight = itemHeight;

		}

		temp = (int) ((slotLength/itemBreadth) * (slotBreadth/itemHeight) * (slotHeight/itemLength));
		if (temp > upperThreshold) {
			upperThreshold = temp;
			finallength = itemBreadth;
			finalbreadth = itemHeight;
			finalheight = itemLength;

		}

		temp = (int) ((slotLength/itemHeight) * (slotBreadth/itemBreadth) * (slotHeight/itemLength));
		if (temp > upperThreshold) {
			upperThreshold = temp;
			finallength = itemHeight;
			finalbreadth = itemBreadth;
			finalheight = itemLength;

		}

		temp = (int) ((slotLength/itemHeight) * (slotBreadth/itemLength) * (slotHeight/itemBreadth));
		if (temp > upperThreshold) {
			upperThreshold = temp;
			finallength = itemHeight;
			finalbreadth = itemLength;
			finalheight = itemBreadth;

		}

		lowerThreshold = (int) (0.2 * upperThreshold); 

                    
                            Double finalUpperThres = (double) upperThreshold;
                            Double finalLowerThres = (double) lowerThreshold;
                
                             finalvalues.put("lengthUsed" ,finallength);
                             finalvalues.put("breadthUsed" ,finalbreadth);
                             finalvalues.put("heightUsed" ,finalheight);
                             finalvalues.put("upperThreshold" ,finalUpperThres);
                             finalvalues.put("lowerThreshold" ,finalLowerThres);
                             
                             System.out.println("Length to use: " + finallength);
		System.out.println("Breadth to use: " + finalbreadth);
		System.out.println("Height to use: " + finalheight);
		System.out.println("Upper Threshold: " + finalUpperThres);
		System.out.println("Lower Threshold: " + finalLowerThres);
                
                
                return finalvalues;
                
	}
 
      public Long addNewInventoryPdt(Item pdt, Long shelfValueLong, Integer shelfSlotPos, InvenLoc loc, String zon, Integer upperThres, Integer lowerThres, Double pdtLength, Double pdtBreadth, Double pdtHeight) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        String loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        Query q = em.createNamedQuery("Staff.findByEmail");
        q.setParameter("email", loggedInEmail);

        System.out.println("email: " + loggedInEmail);
        Staff temp = (Staff) q.getSingleResult();
        fac = temp.getFac();

        System.out.println("FACID: " + fac.getId());
        
        Query q2 = em.createNamedQuery("ShelfSlot.findSlot");
        q2.setParameter("position", shelfSlotPos);
        q2.setParameter("shelf", em.find(Shelf.class, shelfValueLong));

        ShelfSlot tempSlot = (ShelfSlot) q2.getSingleResult();

       try {
                        tempSlot.setOccupied(true);
                        em.persist(tempSlot);
                        System.out.println("VALUE: "+tempSlot.getOccupied());
                        InventoryProduct p = new InventoryProduct();     
                        p.setFac(em.find(Facility.class, fac.getId()));
                        p.setProd(pdt);
                        p.setZone(zon);
                        p.setShelf(em.find(Shelf.class, shelfValueLong));
                        p.setShelfSlot(tempSlot);
                        p.setQuantity(0);
                        p.setUppThreshold(upperThres);
                        p.setLowThreshold(lowerThres);
                        p.setLocation(loc);
                        p.setPdtLength(pdtLength);
                        p.setPdtBreadth(pdtBreadth);
                        p.setPdtHeight(pdtHeight);
                        em.persist(p);
                        em.flush();

                        return p.getId();

                    } catch (Exception e) {
                        e.printStackTrace();

                        return null;

                    } finally {
                        em.close();
                    }
    }
      
      public Long addNewInventoryRawMat(Item mat, Long shelfValueLong, Integer shelfSlotPos, InvenLoc loc, String zon, Integer upperThres, Integer lowerThres, Double matLength, Double matBreadth, Double matHeight) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        String loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        Query q = em.createNamedQuery("Staff.findByEmail");
        q.setParameter("email", loggedInEmail);

        System.out.println("email: " + loggedInEmail);
        Staff temp = (Staff) q.getSingleResult();
        fac = temp.getFac();

        System.out.println("FACID: " + fac.getId());
        
        Query q2 = em.createNamedQuery("ShelfSlot.findSlot");
        q2.setParameter("position", shelfSlotPos);
        q2.setParameter("shelf", em.find(Shelf.class, shelfValueLong));

        ShelfSlot tempSlot = (ShelfSlot) q2.getSingleResult();

       try {
                        tempSlot.setOccupied(true);
                        em.persist(tempSlot);
                        System.out.println("VALUE: "+tempSlot.getOccupied());
                        InventoryMaterial im = new InventoryMaterial();     
                        im.setFac(em.find(Facility.class, fac.getId()));
                        im.setMat(mat);
                        im.setZone(zon);
                        im.setShelf(em.find(Shelf.class, shelfValueLong));
                        im.setShelfSlot(tempSlot);
                        im.setQuantity(0);
                        im.setUppThreshold(upperThres);
                        im.setLowThreshold(lowerThres);
                        im.setMatLength(matLength);
                        im.setMatBreadth(matBreadth);
                        im.setMatHeight(matHeight);
                        System.out.println("LOC IN SAVE: "+loc);
                        im.setLocation(loc);
                        em.persist(im);
                        em.flush();

                        return im.getId();

                    } catch (Exception e) {
                        e.printStackTrace();

                        return null;

                    } finally {
                        em.close();
                    }
    }
      
    public void updateInventoryProduct(InventoryProduct ip)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        em.merge(ip);
        
    }
    
      public void updateInvenMat(InventoryMaterial im)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        em.merge(im);
        
    }
    
    private Boolean ShelfSlotAlreadyExist(Long shelfId, Integer shelfSlotId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Shelf shelf = em.find(Shelf.class, shelfId);
        ShelfSlot shelfSlot = em.find(ShelfSlot.class, shelfSlotId);
        Query query = em.createQuery("SELECT i FROM InventoryProduct i WHERE i.shelf = :shelf AND i.shelfSlot = :shelfSlot");
        query.setParameter("shelf", shelf);
        query.setParameter("shelfSlot", shelfSlot);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    
    public void removeRetail(InventoryProduct pdt) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            InventoryProduct removePdt = em.merge(pdt);
            
            ShelfSlot slot = removePdt.getShelfSlot();
            slot.setOccupied(false);
            
            em.merge(slot);
            
            em.remove(removePdt);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Retail Inventory Record ID " + pdt.getId() + " due to Foreign Key constraints");
        }
       
    }
    
    public void removeFurn(InventoryMaterial invenFurn) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            InventoryMaterial removeInvenFurn = em.merge(invenFurn);
            
            ShelfSlot slot =removeInvenFurn.getShelfSlot();
            slot.setOccupied(false);
            
            em.merge(slot);
            
            em.remove(removeInvenFurn);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Retail Inventory Record ID " + invenFurn.getId() + " due to Foreign Key constraints");
        }
       
    }
    
     public void removeInvenMat(InventoryMaterial invenMat) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            InventoryMaterial removeInvenMat = em.merge(invenMat);
            
            ShelfSlot slot = removeInvenMat.getShelfSlot();
            slot.setOccupied(false);
            
            em.merge(slot);
            
            em.remove(removeInvenMat);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Retail Inventory Record ID " + invenMat.getId() + " due to Foreign Key constraints");
        }
       
    }
    
     private Boolean EntityIdIntDNE(String entityClass, Integer entityId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT e FROM " + entityClass + " e WHERE e.id= " + entityId);
        List resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    
    private Boolean EntityIdDNE(String entityClass, Long entityId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT e FROM " + entityClass + " e WHERE e.id= " + entityId);
        List resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    
    //Shelf Bean Methods
     public Long addNewShelf(Long facId, InvenLoc location, String zone, Integer shelf, Long shelfTypeId)
            throws EntityDneException, DetailsConflictException {
         System.err.println("addNewShelf()");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        if (!EntityIdDNE("Facility", facId)) {
            if (!EntityIdDNE("ShelfType", shelfTypeId)) {
                if (!ShelfNumberAlreadyExist(location, zone, shelf)) {
                    try {
                        Shelf s = new Shelf();     
                        s.setFac(em.find(Facility.class, facId));
                        s.setLocation(location);
                        s.setZone(zone);
                        s.setShelf(shelf);
                        s.setShelfType(em.find(ShelfType.class, shelfTypeId));
                        System.err.println("shelf attributes set!");
                        em.persist(s);
                        em.flush();
                        System.err.println("shelf entity persisted");
                       ShelfType shelfType =  em.find(ShelfType.class, shelfTypeId);
                       Shelf  createdShelf = em.find(Shelf.class, s.getId());
                      
                       for(int i=0; i<shelfType.getNumSlot(); ++i){
                           int position = i+1;
                           ShelfSlot shelfSlot = new ShelfSlot();
                           shelfSlot.setShelf(createdShelf);
                           shelfSlot.setPosition(position);
                           shelfSlot.setOccupied(false);
                           em.persist(shelfSlot);
                       }
                        em.flush();
                        System.err.println("ID = " + s.getId());
                        return s.getId();

                    } catch (Exception e) {
                        e.printStackTrace();

                        return null;

                    } finally {
                        em.close();
                    }
                } else {
                    throw new DetailsConflictException("Shelf Number conflict: " + shelf);
                }
            } else {
                throw new EntityDneException("Shelf Type DNE: " + shelfTypeId);
            }
        } else {
            throw new EntityDneException("Facility DNE: " + facId);
        }
    } 
    
     private Boolean ShelfNumberAlreadyExist(InvenLoc location, String zone, Integer shelfNum) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        
        Facility f = (Facility) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("facility");
        Query query = em.createQuery("SELECT s FROM " + Shelf.class.getName() + " s where s.fac = :fac AND s.location = :location AND s.zone = :zone AND s.shelf = :shelf");
        query.setParameter("fac", f);
        query.setParameter("location", location);
        query.setParameter("zone", zone);
        query.setParameter("shelf", shelfNum);
      
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
     
    public List<Facility> getAllMFsStores() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findAllMfs");
        return query.getResultList();
    }
    
     public List<ShelfType> getAllShelfTypes() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("ShelfType.findAll");
        return query.getResultList();
    }
         
      public void removeShelf(Shelf shelf) throws ReferenceConstraintException {
          
          System.out.println("REMOVE SHELF"+ shelf);
         if(!ForeignKeyConstraintAlreadyExist(shelf)){
         try{
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();
       
                 Shelf removeShelf = em.merge(shelf);
                 
                 Query q = em.createNamedQuery("ShelfSlot.findByShelf");
                 q.setParameter("shelf", removeShelf);
                 
                 List<ShelfSlot> resultSlots = q.getResultList();
                 
                 for(int i=0; i<resultSlots.size(); ++i){
                     em.merge(resultSlots.get(i));
                     em.remove(resultSlots.get(i));
                 }
                 
                 em.remove(removeShelf);

             } catch (Exception e) {
                 throw new ReferenceConstraintException("Cannot delete Shelf Status ID " + shelf.getId() + " due to Foreign Key constraints");
             }
        }else{
             throw new ReferenceConstraintException("Cannot delete Shelf Type ID " + shelf.getId() + " due to Foreign Key constraints");
         }
      }
    
     public List<Shelf> getAllShelf() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility f = (Facility) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("facility");
        Query query = em.createNamedQuery("Shelf.findByFac");
        query.setParameter("fac", f);
        return query.getResultList();
    }


private Boolean ShelfAlreadyExist(String entity, Integer shelfNum, Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT st FROM " + entity + " st WHERE st.shelf = '" + shelfNum + "' AND NOT st.id = " + id);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

   private Boolean ForeignKeyConstraintAlreadyExist(Shelf shelf) {
       
       System.out.println("IN HERE");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
       
        Query query = em.createNamedQuery("InventoryMaterial.findByShelf");
        query.setParameter("shelf",shelf);
        System.out.println(shelf);
        
        Query q = em.createNamedQuery("InventoryProduct.findByShelf");
        q.setParameter("shelf",shelf);
        System.out.println(shelf);
 
        
        List<InventoryMaterial> matResultList = query.getResultList();
        List<InventoryProduct> pdtResultList = q.getResultList();
        if ( (matResultList.isEmpty()&&pdtResultList.isEmpty())) {
            return false;
        } else {
            return true;
        }
    }
   
   //Shelf Slot Bean
   public List<ShelfSlot> getAllShelfSlot(Shelf shelf) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("ShelfSlot.getOccupied");
        query.setParameter("shelf",shelf);
        
        System.out.println("LIST SIZE: "+ query.getResultList().size());
        
        return query.getResultList();
    }
   
    public Long addNewShelfType(ShelfType shelfType) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        
        if (!NameAlreadyExist("ShelfType", shelfType.getName())) {
            try {
                em.persist(shelfType);
                em.flush();
                System.err.println("ID = " + shelfType.getId());
                return shelfType.getId();

            } catch (Exception e) {
                e.printStackTrace();
                
                return null;

            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Name conflict: " + shelfType.getName());
        }
    }
   

      private Boolean NameAlreadyExist(String entity, String name) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT st FROM " + entity + " st WHERE st.name = '" + name + "'");
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
     
      
    private Boolean NameAlreadyExist(String entity, String name, Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT st FROM " + entity + " st WHERE st.name = '" + name + "' AND NOT st.id = " + id);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
     
     
     
     
     
     

    /* private Boolean ShelfTypeAlreadyExist(String name, Integer numSlot, Double weight, Double length, Double breadth, Double height) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        
        Query query = em.createQuery("SELECT s FROM ShelfType s WHERE s.name= :inName AND s.numSlot= :inSlot AND s.weightLimit= :inWeight AND s.length = :inLength AND s.breadth= :inBreadth AND s.height= :inHeight");
        query.setParameter("inName", name);
        query.setParameter("inSlot", numSlot);
        query.setParameter("inWeight", weight);
        query.setParameter("inLength", length);
        query.setParameter("inBreadth", breadth);
        query.setParameter("inHeight", height);
        
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    } */
     
     public void removeShelfType(ShelfType shelfType) throws ReferenceConstraintException {
         if(!ForeignKeyConstraintShelfStatsAlreadyExist(shelfType)){
         try{
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();
       
                 ShelfType removeShelfType = em.merge(shelfType);
                 em.remove(removeShelfType);

             } catch (Exception e) {
                 throw new ReferenceConstraintException("Cannot delete Shelf Type ID " + shelfType.getId() + " due to Foreign Key constraints");
             }
         }else {
            throw new ReferenceConstraintException("Cannot delete Shelf Type ID " + shelfType.getId() + " due to Foreign Key constraints");
        } 
     }
     
      private Boolean ForeignKeyConstraintShelfStatsAlreadyExist(ShelfType shelfType) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
       
        Query query = em.createNamedQuery("Shelf.findByShelfType");
        query.setParameter("shelfType",shelfType);
        System.out.println(shelfType);
        
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
      
    public List<Item> getAllProducts() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Item.findAllProduct");
        return query.getResultList();
    }

    public List<Material> getLowShelfInventory(Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT im FROM " + InventoryMaterial.class.getName() + " im WHERE im.fac = :fac AND im.quantity < im.lowThreshold)");
        query.setParameter("fac", fac);
        return query.getResultList();
    }
     
    public List<Material> getLowStoreInventory(Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT im FROM " + InventoryMaterial.class.getName() + " im WHERE im.fac = :fac AND im.mat NOT IN (SELECT DISTINCT im2.mat FROM " + InventoryMaterial.class.getName() + " im2 WHERE im2.fac = :fac AND im2.quantity > im2.lowThreshold)");
        query.setParameter("fac", fac);
        return query.getResultList();
    }
    
    public List<Material> getLowMFInventory(Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT im FROM " + InventoryMaterial.class.getName() + " im WHERE im.fac = :fac AND im.mat NOT IN (SELECT DISTINCT im2.mat FROM " + InventoryMaterial.class.getName() + " im2 WHERE im2.fac = :fac AND im2.quantity > im2.lowThreshold)");
        query.setParameter("fac", fac);
        return query.getResultList();
    }
    
    public Long getInvItemId(Long id, Integer itemType) {
        System.err.println("getInvItem of id:" + id + " and type:" + itemType);
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        if(itemType == 1) {
            Query query1 = em.createNamedQuery("InventoryMaterial.findById");
            query1.setParameter("id", id);
            return ((InventoryMaterial) query1.getSingleResult()).getMat().getId();
        }
        else {
            Query query2 = em.createNamedQuery("InventoryProduct.findById");
            query2.setParameter("id", id);
            return ((InventoryProduct) query2.getSingleResult()).getProd().getId();
        }
    }
 
    public String getInvItemName(Long id, Integer itemType) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        if(itemType == 1) {
            Query query1 = em.createNamedQuery("InventoryMaterial.findById");
            query1.setParameter("id", id);
            return ((InventoryMaterial) query1.getSingleResult()).getMat().getName();
        }
        else {
            Query query2 = em.createNamedQuery("InventoryProduct.findById");
            query2.setParameter("id", id);
            return ((InventoryProduct) query2.getSingleResult()).getProd().getName();
        }
    }
    
    public String getZone(Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query1 = em.createNamedQuery("InventoryMaterial.findById");
        query1.setParameter("id", id);
        if(query1.getSingleResult() != null) {
            return ((InventoryMaterial) query1.getSingleResult()).getZone();
        }
        Query query2 = em.createNamedQuery("InventoryProduct.findById");
        query2.setParameter("id", id);
        if(query2.getSingleResult() != null) {
            return ((InventoryProduct) query1.getSingleResult()).getZone();
        }
        return null;
    }
    
    public String getShelf(Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query1 = em.createNamedQuery("InventoryMaterial.findById");
        query1.setParameter("id", id);
        if(query1.getSingleResult() != null) {
            return ((InventoryMaterial) query1.getSingleResult()).getShelf().toString();
        }
        Query query2 = em.createNamedQuery("InventoryProduct.findById");
        query2.setParameter("id", id);
        if(query2.getSingleResult() != null) {
            return ((InventoryProduct) query1.getSingleResult()).getShelf().toString();
        }
        return null;
    }
            
    public Integer getPosition(Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query1 = em.createNamedQuery("InventoryMaterial.findById");
        query1.setParameter("id", id);
        if(query1.getSingleResult() != null) {
            return ((InventoryMaterial) query1.getSingleResult()).getShelfSlot().getPosition();
        }
        Query query2 = em.createNamedQuery("InventoryProduct.findById");
        query2.setParameter("id", id);
        if(query2.getSingleResult() != null) {
            return ((InventoryProduct) query1.getSingleResult()).getShelfSlot().getPosition();
        }
        return null;
    }
 
    public int getItemType(Item item) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query1 = em.createNamedQuery("Material.findById");
        query1.setParameter("id", item.getId());
        if(query1.getSingleResult() != null) {
            return 1;
        }
        Query query2 = em.createNamedQuery("Product.findById");
        query2.setParameter("id", item.getId());
        if(query2.getSingleResult() != null) {
            return 2;
        }
        Query query3 = em.createNamedQuery("Ingredient.findById");
        query3.setParameter("id", item.getId());
        if(query3.getSingleResult() != null) {
            return 3;
        }
        return 0;
    }
    
    public List<InventoryMaterial> getFurns(Facility fac, InvenLoc location) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT DISTINCT im FROM " + InventoryMaterial.class.getName() + " im WHERE im.fac = :fac AND im.location = :location");
        query.setParameter("fac", fac);
        query.setParameter("location", location);
        System.err.println("getFurns from: " + location);
        return query.getResultList();
    }
    
    public List<InventoryProduct> getProds(Facility fac, InvenLoc location) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT DISTINCT im FROM " + InventoryProduct.class.getName() + " im WHERE im.fac = :fac AND im.location = :location");
        query.setParameter("fac", fac);
        query.setParameter("location", location);
        return query.getResultList();
    }
    
    public boolean persistShelfSlot(ShelfSlot s) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        System.err.println("persisting shelfslot..");
        try {
            em.merge(s);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
            System.err.println("shelfslot persisted!");
            return true;
        }
    }

    public Facility getMatMf(Facility fac, Item mat) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT DISTINCT f FROM " + Facility.class.getName() + " f, " + DistributionMFtoStore.class.getName() + " d WHERE d.store = :fac AND d.mat = :mat AND f = d.mf");
        query.setParameter("fac", fac);
        query.setParameter("mat", mat);
        return (Facility) query.getSingleResult();
    }
}
