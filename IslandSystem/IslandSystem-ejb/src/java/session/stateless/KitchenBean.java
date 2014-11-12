/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session.stateless;

import classes.WeekHelper;
import entity.Bill;
import entity.DeliverySchedule;
import entity.DistributionMFtoStore;
import entity.DistributionMFtoStoreProd;
import entity.Facility;
import entity.Ingredient;
import entity.InventoryKit;
import entity.InventoryMaterial;
import entity.InventoryProduct;
import entity.Item;
import entity.Material;
import entity.MrpRecord;
import entity.PoItem;
import entity.PoRecord;
import entity.Product;
import entity.ProductionRecord;
import entity.PurchasePlanningRecord;
import entity.Shelf;
import entity.ShelfSlot;
import entity.ShelfType;
import entity.Staff;
import entity.Supplier;
import entity.SuppliesIngrToFac;
import entity.SuppliesMatToFac;
import entity.SuppliesProdToFac;
import enumerator.InvenLoc;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import util.exception.DetailsConflictException;
import util.exception.EntityDneException;
import util.exception.ReferenceConstraintException;

@LocalBean
@Stateless
public class KitchenBean { //implements ScmBeanRemote {

    private ArrayList<DeliverySchedule> deliverySchedule;

    //@Override
    public List<Facility> getAllStores() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findAllStore");
        return query.getResultList();
    }

    //@Override
    public List<Product> getAllProducts() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Product.findAll");
        return query.getResultList();
    }

    //@Override
    public List<Material> getAllFurniture() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Material.findAllFurniture");
        return query.getResultList();
    }

    //@Override
    public List<DistributionMFtoStore> getDistribution(Facility mf) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT d FROM " + DistributionMFtoStore.class.getName() + " d WHERE d.mfId = :mf");
        List<DistributionMFtoStore> distlist = query.getResultList();
        return new ArrayList<DistributionMFtoStore>(distlist);
    }

    //@Override
    public List<DeliverySchedule> getDeliverySchedule(long mfgid) {
        System.out.println("Creating Delivery Schedule");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT d FROM DeliverySchedule d WHERE d.mfgId = " + mfgid);
        List<DeliverySchedule> deliverySchedule = query.getResultList();
        return new ArrayList<DeliverySchedule>(deliverySchedule);
    }

    //@Override
    public List<ProductionRecord> getProductionRecords(Facility mf) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT p FROM " + ProductionRecord.class.getName() + " p, " + DistributionMFtoStore.class.getName() + " d "
                + "WHERE p.storeId = d.storeId AND p.matId = d.matId AND m.mfId = :mf");
        query.setParameter("mf", mf);
        List<ProductionRecord> pr = query.getResultList();
        System.out.println(pr.size());
        return pr;
    }

    //@Override
    public List<PurchasePlanningRecord> getPurchasePlanningRecords(Facility mf, int year, int period) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT p FROM " + PurchasePlanningRecord.class.getName() + " p, " + DistributionMFtoStoreProd.class.getName() + " d "
                + "WHERE p.store = d.store AND p.prod = d.prod AND d.mf = :mf AND p.year > :year OR (p.year = :year AND p.period > :period)");
        query.setParameter("mf", mf);
        query.setParameter("year", year);
        query.setParameter("period", period);
        List<PurchasePlanningRecord> pr = query.getResultList();
        return pr;
    }

    //@Override
    public List<ProductionRecord> getProductionRecords(Facility mf, int year, int period) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT p FROM " + ProductionRecord.class.getName() + " p, " + DistributionMFtoStore.class.getName() + " d "
                + "WHERE p.store = d.store AND p.mat = d.mat AND d.mf = :mf AND p.year > :year OR (p.year = :year AND p.period > :period)");
        query.setParameter("mf", mf);
        query.setParameter("year", year);
        query.setParameter("period", period);
        List<ProductionRecord> pr = query.getResultList();
        return pr;
    }

    //@Override
    public List<Bill> getUnpaidBills() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT b FROM Bill b WHERE b.status = 'unpaid'");
        List<Bill> unpaidBills = query.getResultList();
        return unpaidBills;
    }

    //@Override
    public void persistBill(Bill bill) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        em.persist(bill);
        em.close();
    }

    //@Override
    public Facility getFac(String loggedInEmail) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Staff.findByEmail");
        query.setParameter("email", loggedInEmail);
        return ((Staff) query.getSingleResult()).getFac();
    }

    //@Override
    public Facility getFacility(long fid) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findById");
        query.setParameter("facilityId", fid);
        return (Facility) query.getSingleResult();
    }

    //@Override
    public Facility getFacility(String fname) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findByName");
        query.setParameter("name", fname);
        return (Facility) query.getSingleResult();
    }

    //@Override
    public Supplier getSupplier(long sid) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Supplier.findById");
        query.setParameter("id", sid);
        return (Supplier) query.getSingleResult();
    }

    public List<Supplier> getIngrSuppliers(Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT DISTINCT s.sup FROM " + SuppliesIngrToFac.class.getName() + " s WHERE s.fac = :fac");
        query.setParameter("fac", fac);
        return query.getResultList();
    }

    //@Override
    public List<SuppliesIngrToFac> getSuppliesIngrToFac(Facility fac, Supplier sup) {
        System.err.println("function: getSuppliesProdToFac()");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        WeekHelper wh = new WeekHelper();
        Query query = em.createQuery("SELECT s FROM " + SuppliesIngrToFac.class.getName() + " s WHERE s.fac = :fac AND s.sup = :sup");
        query.setParameter("fac", fac);
        query.setParameter("sup", sup);
        return query.getResultList();
    }

    public Boolean checkPoIngrExist(Facility fac, Supplier sup, Ingredient item, Date deliveryDate) {
        System.err.println("function: checkPoMatExist()");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        WeekHelper wh = new WeekHelper();
        Query query = em.createQuery("SELECT s FROM " + PoItem.class.getName() + " s WHERE s.item = :item AND s.deliveryDate = :deliveryDate AND s.po IN "
                + "(SELECT po FROM " + PoRecord.class.getName() + " po WHERE po.fac = :fac AND po.sup = :sup)");
        query.setParameter("fac", fac);
        query.setParameter("sup", sup);
        query.setParameter("item", item);
        query.setParameter("deliveryDate", deliveryDate);
        if (query.getResultList().isEmpty()) {
            System.err.println("Poitem does not exist");
            return false;
        } else {
            System.err.println("Poitem already exist");
            return true;
        }
    }

    public List<PoItem> getPoItem(Facility fac, Supplier sup) {
        System.err.println("function: getPoItem()");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        WeekHelper wh = new WeekHelper();
        //Query query = em.createQuery("SELECT DISTINCT pm FROM " + PoItem.class.getName() + " pm WHERE pm.po IN (SELECT po1 FROM " + PoRecord.class.getName() + " po1 WHERE po1.orderDate IN (SELECT MIN(po2.orderDate) FROM " + PoRecord.class.getName() + " po2 WHERE po2.status = 'Ordered' AND po2.fac = :fac AND po2.sup = :sup) AND po1.fac = :fac AND po1.sup = :sup)");
        Query query = em.createQuery("SELECT DISTINCT pm FROM " + PoItem.class.getName() + " pm WHERE (pm.status = 'Ordered' OR pm.status = 'Rejected') AND pm.po IN (SELECT po FROM " + PoRecord.class.getName() + " po WHERE po.status = 'Sent' AND po.fac = :fac AND po.sup = :sup)");
        query.setParameter("fac", fac);
        query.setParameter("sup", sup);
        return query.getResultList();
    }

    public int getItemType(Item item) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query1 = em.createNamedQuery("Material.findById");
        query1.setParameter("id", item.getId());
        if (query1.getSingleResult() != null) {
            return 1;
        }
        Query query2 = em.createNamedQuery("Product.findById");
        query2.setParameter("id", item.getId());
        if (query2.getSingleResult() != null) {
            return 2;
        }
        Query query3 = em.createNamedQuery("Ingredient.findById");
        query3.setParameter("id", item.getId());
        if (query3.getSingleResult() != null) {
            return 3;
        }
        return 0;
    }

    public InventoryKit getInventoryKit(Item ingr, Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT im FROM " + InventoryKit.class.getName() + " im WHERE im.ingr = :ingr AND im.fac = :fac AND im.location = :loc");
        query.setParameter("fac", fac);
        query.setParameter("ingr", ingr);
        query.setParameter("loc", InvenLoc.KITCHEN);
        List<InventoryKit> imList = query.getResultList();
        if (imList.isEmpty()) {
            System.err.println("no invmat found");
            return null;
        }
        InventoryKit invMat = imList.get(0);
        if (imList.size() > 1) {
            System.err.println("multiple invmat found");
            for (InventoryKit im : imList) {
                if (invMat.getQuantity() < im.getQuantity()) {
                    invMat = im;
                }
            }
        }
        return invMat;
    }

    public ShelfSlot getAvailableShelfSlot(Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT s FROM " + ShelfSlot.class.getName() + " s WHERE "
                + "im.mat = :mat AND s.shelf.fac = :fac AND s.occupied = '0' AND s.shelf.location = '5'");
        query.setParameter("fac", fac);
        if (query.getResultList().isEmpty()) {
            return null;
        } else {
            return (ShelfSlot) query.getResultList().get(0);
        }
    }

    public ShelfSlot getAvailableShelfSlot(Long id, Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT s FROM " + ShelfSlot.class.getName() + " s, " + InventoryMaterial.class.getName() + " im WHERE "
                + "im.mat.id = :id AND s.shelf.fac = :fac AND s.shelf.zone = im.shelf.zone AND s.shelf.location = im.shelf.location AND s.occupied = '0'");
        query.setParameter("fac", fac);
        query.setParameter("id", id);
        if (query.getResultList().isEmpty()) {
            System.err.println("query available shelf slot not found");
            return null;
        } else {
            return (ShelfSlot) query.getResultList().get(0);
        }
    }

    public ShelfSlot getOtherShelfSlot(Long id, Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT s FROM " + ShelfSlot.class.getName() + " s, " + InventoryMaterial.class.getName() + " im WHERE "
                + "im.mat.id = :id AND s.shelf.fac = :fac AND s.shelf.location = im.shelf.location AND s.occupied = '0'");
        query.setParameter("fac", fac);
        query.setParameter("id", id);
        if (query.getResultList().isEmpty()) {
            return null;
        } else {
            return (ShelfSlot) query.getResultList().get(0);
        }
    }

    public boolean updateInventoryKit(InventoryKit mat) {
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

    //@Override
    public Integer getMatQty(Facility fac, Item mat, Integer week, Integer year) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT mr FROM " + MrpRecord.class.getName() + " mr WHERE mr.fac = :fac AND mr.mat = :mat AND mr.week = :week AND mr.year = :year");
        query.setParameter("fac", fac);
        query.setParameter("mat", mat);
        query.setParameter("week", week);
        query.setParameter("year", year);
        if (query.getSingleResult() != null) {
            return ((MrpRecord) query.getSingleResult()).getPlanned();
        } else {
            return 0;
        }
    }

    public Integer getMatQtyIngredient(Facility fac, Ingredient mat, Integer week, Integer year) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT mr FROM " + MrpRecord.class.getName() + " mr WHERE mr.fac = :fac AND mr.mat = :mat AND mr.week = :week AND mr.year = :year");
        query.setParameter("fac", fac);
        query.setParameter("mat", mat);
        query.setParameter("week", week);
        query.setParameter("year", year);
        return ((MrpRecord) query.getSingleResult()).getPlanned();
    }

    //@Override
    public boolean persistPoDetails(List<PoItem> piList, PoRecord poRecord) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        System.err.println("persisting poproducts..");

        try {
            for (int i = 0; i < piList.size(); i++) {
                System.err.println("poproduct: " + i);
                piList.get(i).setPo(poRecord);
                em.persist(piList.get(i));
                em.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("error persisting poproduct!");
            return false;
        } finally {
            em.close();
            System.err.println("poproduct persisted!");
            return true;
        }
    }

    //@Override
    public boolean persistPo(PoRecord poRecord) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        System.err.println("persisting porecord..");
        try {
            em.persist(poRecord);
            em.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
            System.err.println("porecord persisted!");
            return true;
        }
    }

    public String getIngrName(Long ingrId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Item.findById");
        query.setParameter("id", ingrId);
        return ((Item) query.getSingleResult()).getName();
    }

    public Item getItem(Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT i FROM " + Item.class.getName() + " i WHERE i.id = :id");
        query.setParameter("id", id);
        return (Item) query.getSingleResult();
    }

    public List<PoItem> getPoItem(Facility fac, Supplier sup, Date currDate) {
        System.err.println("function: getPoItem()");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        WeekHelper wh = new WeekHelper();
        Query query = em.createQuery("SELECT DISTINCT pm FROM " + PoItem.class.getName() + " pm WHERE pm.po IN (SELECT po1 FROM " + PoRecord.class.getName() + " po1 WHERE po1.orderDate IN (SELECT MIN(po2.orderDate) FROM " + PoRecord.class.getName() + " po2 WHERE po2.status = 'Ordered' AND po2.fac = :fac AND po2.sup = :sup) AND po1.fac = :fac AND po1.sup = :sup)");
        query.setParameter("fac", fac);
        query.setParameter("sup", sup);
        return query.getResultList();
    }

    public List<PoItem> getPoItems(PoRecord po) {
        System.err.println("function: getPoItem()");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        WeekHelper wh = new WeekHelper();
        Query query = em.createQuery("SELECT DISTINCT pm FROM " + PoItem.class.getName() + " pm WHERE pm.po = :po");
        query.setParameter("po", po);
        return query.getResultList();
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
        return (InventoryMaterial) query.getSingleResult();
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

    public ShelfSlot getAvailableShelfSlot(InventoryMaterial mat, Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT s FROM " + ShelfSlot.class.getName() + " s WHERE "
                + "s.shelf.fac = :fac AND s.shelf.zone = im.shelf.zone AND s.shelf.shelfType = '" + mat.getShelf().getShelfType() + "' AND s.occupied = '0'");
        query.setParameter("fac", fac);
        query.setParameter("mat", mat);
        return (ShelfSlot) query.getResultList().get(0);
    }

    public boolean persistInventoryKit(InventoryKit im) {
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
            //System.err.println("inventorymaterial persisted!");
            return true;
        }
    }

    public Map<String, Double> calcThresValues(Double slotLength, Double slotBreadth, Double slotHeight, Double itemLength, Double itemBreadth, Double itemHeight) {
        Map<String, Double> finalvalues = new HashMap<String, Double>();

        Integer upperThreshold;
        Integer lowerThreshold;
        Integer temp;
        Double finallength = itemLength;
        Double finalbreadth = itemBreadth;
        Double finalheight = itemHeight;

        System.out.println("itemLength " + itemLength);
        System.out.println("itemHeight " + itemHeight);
        System.out.println("itemBreadth " + itemBreadth);

        upperThreshold = (int) ((slotLength / itemLength) * (slotBreadth / itemBreadth) * (slotHeight / itemHeight));

        temp = (int) ((slotLength / itemLength) * (slotBreadth / itemHeight) * (slotHeight / itemBreadth));
        if (temp > upperThreshold) {
            upperThreshold = temp;
            finallength = itemLength;
            finalbreadth = itemHeight;
            finalheight = itemBreadth;

        }

        temp = (int) ((slotLength / itemBreadth) * (slotBreadth / itemLength) * (slotHeight / itemHeight));
        if (temp > upperThreshold) {
            upperThreshold = temp;
            finallength = itemBreadth;
            finalbreadth = itemLength;
            finalheight = itemHeight;

        }

        temp = (int) ((slotLength / itemBreadth) * (slotBreadth / itemHeight) * (slotHeight / itemLength));
        if (temp > upperThreshold) {
            upperThreshold = temp;
            finallength = itemBreadth;
            finalbreadth = itemHeight;
            finalheight = itemLength;

        }

        temp = (int) ((slotLength / itemHeight) * (slotBreadth / itemBreadth) * (slotHeight / itemLength));
        if (temp > upperThreshold) {
            upperThreshold = temp;
            finallength = itemHeight;
            finalbreadth = itemBreadth;
            finalheight = itemLength;

        }

        temp = (int) ((slotLength / itemHeight) * (slotBreadth / itemLength) * (slotHeight / itemBreadth));
        if (temp > upperThreshold) {
            upperThreshold = temp;
            finallength = itemHeight;
            finalbreadth = itemLength;
            finalheight = itemBreadth;

        }

        lowerThreshold = (int) (0.2 * upperThreshold);

        Double finalUpperThres = (double) upperThreshold;
        Double finalLowerThres = (double) lowerThreshold;

        finalvalues.put("lengthUsed", finallength);
        finalvalues.put("breadthUsed", finalbreadth);
        finalvalues.put("heightUsed", finalheight);
        finalvalues.put("upperThreshold", finalUpperThres);
        finalvalues.put("lowerThreshold", finalLowerThres);

        System.out.println("Length to use: " + finallength);
        System.out.println("Breadth to use: " + finalbreadth);
        System.out.println("Height to use: " + finalheight);
        System.out.println("Upper Threshold: " + finalUpperThres);
        System.out.println("Lower Threshold: " + finalLowerThres);

        return finalvalues;

    }
    
    public List<InventoryKit> getAllInvenIngr(){
        Facility fac = new Facility();
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        
       String loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        Query q = em.createNamedQuery("Staff.findByEmail");
        q.setParameter("email", loggedInEmail);
        
         System.out.println("email: "+ loggedInEmail);
         Staff temp =(Staff) q.getSingleResult();
          fac = temp.getFac();
         
        System.out.println("FACID: "+fac.getId());
        
        //Integer genCategory = 0; 
         
        Query query = em.createQuery("SELECT im FROM " + InventoryKit.class.getName() + " im WHERE im.fac = :fac AND im.ingr IN (SELECT m FROM " + Ingredient.class.getName() + " m)");
        //query.setParameter("genCategory", genCategory);
        query.setParameter("fac", fac);
        
        System.out.println("IN INVENFURNS: "+ query.getResultList().size());
        
        return query.getResultList();
    }
    
    
     public List<Shelf> getZoneShelfEntitiesFromFac(){
       Facility fac = new Facility();
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
     
    public List<Item> getAllIngr(){
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
       
        Query q = em.createNamedQuery("Ingredient.findAll");
        return q.getResultList();
      }
    
    
      public Long addNewInventoryIngr(Item pdt, Long shelfValueLong, Integer shelfSlotPos, InvenLoc loc, String zon, Integer upperThres, Integer lowerThres, Double pdtLength, Double pdtBreadth, Double pdtHeight) {
        Facility fac = new Facility();
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
            System.out.println("VALUE: " + tempSlot.getOccupied());
            InventoryKit p = new InventoryKit();
            p.setFac(em.find(Facility.class, fac.getId()));
            p.setIngr(pdt);
            p.setZone(zon);
            p.setShelf(em.find(Shelf.class, shelfValueLong));
            p.setShelfSlot(tempSlot);
            p.setQuantity(0);
            p.setUppThreshold(upperThres);
            p.setLowThreshold(lowerThres);
            p.setLocation(loc);
            p.setIngLength(pdtLength);
            p.setIngBreadth(pdtBreadth);
            p.setIngHeight(pdtHeight);
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


    public void removeRetail(InventoryKit pdt) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            InventoryKit removePdt = em.merge(pdt);
            
            ShelfSlot slot = removePdt.getShelfSlot();
            slot.setOccupied(false);
            
            em.merge(slot);
            
            em.remove(removePdt);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Retail Inventory Record ID " + pdt.getId() + " due to Foreign Key constraints");
        }
       
    }
    
    public void updateInventoryIngr(InventoryKit ip)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        em.merge(ip);
        
    }
    public List<SuppliesIngrToFac> getDistributionKit(Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT d FROM " + SuppliesIngrToFac.class.getName() + " d "
        + "WHERE d.fac = :fac");
        query.setParameter("fac", fac);

        List<SuppliesIngrToFac> dist = query.getResultList();
        return dist;
    }
    
    public List<Shelf> getAllShelf() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility f = (Facility) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("facility");
        Query query = em.createQuery("SELECT s FROM " + Shelf.class.getName() + " s WHERE s.fac = :fac AND s.location = :location");
        query.setParameter("fac", f);
        query.setParameter("location", InvenLoc.KITCHEN);
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
    
    private Boolean ForeignKeyConstraintAlreadyExist(Shelf shelf) {
       
       System.out.println("IN HERE");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
       
        Query query = em.createNamedQuery("InventoryKit.findByShelf");
        query.setParameter("shelf",shelf);
        System.out.println(shelf);
 
        
        List<InventoryKit> ingrResultList = query.getResultList();
        if (ingrResultList.isEmpty()) {
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
}