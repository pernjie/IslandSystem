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
import entity.Staff;
import entity.Supplier;
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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

@LocalBean
@Stateless
public class ScmBean { //implements ScmBeanRemote {

    private ArrayList<DeliverySchedule> deliverySchedule;

    //@Override
    public List<Facility> getAllStores() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findAllStores");
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
        Query query = em.createNamedQuery("Material.findAllMaterial");
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
                + "WHERE p.store = d.store AND p.mat = d.mat AND d.mf = :mf");
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
    public Supplier getSupplier(long sid) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Supplier.findById");
        query.setParameter("id", sid);
        return (Supplier) query.getSingleResult();
    }

    //@Override
    public List<Supplier> getMatSuppliers(Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT DISTINCT s.sup FROM " + SuppliesMatToFac.class.getName() + " s WHERE s.fac = :fac");
        query.setParameter("fac", fac);
        return query.getResultList();
    }

    public List<Supplier> getProdSuppliers(Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT DISTINCT s.sup FROM " + SuppliesProdToFac.class.getName() + " s WHERE s.fac = :fac");
        query.setParameter("fac", fac);
        return query.getResultList();
    }

    //@Override
    public List<SuppliesProdToFac> getSuppliesProdToFac(Facility fac, Supplier sup) {
        System.err.println("function: getSuppliesProdToFac()");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        WeekHelper wh = new WeekHelper();
        Query query = em.createQuery("SELECT s FROM " + SuppliesProdToFac.class.getName() + " s WHERE s.fac = :fac AND s.sup = :sup");
        query.setParameter("fac", fac);
        query.setParameter("sup", sup);
        return query.getResultList();
    }

    public List<SuppliesMatToFac> getSuppliesMatToFac(Facility fac, Supplier sup) {
        System.err.println("function: getSuppliesMatToFac()");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        WeekHelper wh = new WeekHelper();
        Query query = em.createQuery("SELECT s FROM " + SuppliesMatToFac.class.getName() + " s WHERE s.fac = :fac AND s.sup = :sup");
        query.setParameter("fac", fac);
        query.setParameter("sup", sup);
        return query.getResultList();
    }

    public Boolean checkPoMatExist(Facility fac, Supplier sup, Material item, Date deliveryDate) {
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

    public Boolean checkPoProdExist(Facility fac, Supplier sup, Product item, Date deliveryDate) {
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

    //@Override
    public String getProductName(long productId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Product.findById");
        query.setParameter("id", productId);
        System.err.println("mat_id: " + productId);
        return ((Product) query.getSingleResult()).getName();
    }

    public Integer getMatQtyMaterial(Facility fac, Material mat, Integer week, Integer year) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT mr FROM " + MrpRecord.class.getName() + " mr WHERE mr.fac = :fac AND mr.mat = :mat AND mr.week = :week AND mr.year = :year");
        query.setParameter("fac", fac);
        query.setParameter("mat", mat);
        query.setParameter("week", week);
        query.setParameter("year", year);
        System.err.println("week: " + week);
        System.err.println("year: " + year);
        System.err.println("mat: " + mat.getName());
        MrpRecord mr = new MrpRecord();
        try {
            mr = ((MrpRecord) query.getSingleResult());
            System.err.println("mrp record found");
        } catch(Exception e) {
            System.err.println("no mrp record found");
            e.printStackTrace();
        }
        return mr.getPlanned();
    }

    public Integer getMatQtyProduct(Facility fac, Product mat, Integer week, Integer period, Integer year) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT p FROM " + PurchasePlanningRecord.class.getName() + " p WHERE p.store = :fac AND p.prod = :mat AND p.period = :period AND p.year = :year");
        query.setParameter("fac", fac);
        query.setParameter("mat", mat);
        query.setParameter("period", period);
        query.setParameter("year", year);
        try {
            System.err.println("purchase planning record found for period: " + period + " and week: " + week);
            week += 1;
            if (week == 1) {
                return ((PurchasePlanningRecord) query.getSingleResult()).getQuantityW1();
            }
            else if (week == 2) {
                return ((PurchasePlanningRecord) query.getSingleResult()).getQuantityW2();
            }
            else if (week == 3) {
                return ((PurchasePlanningRecord) query.getSingleResult()).getQuantityW3();
            }
            else if (week == 4) {
                return ((PurchasePlanningRecord) query.getSingleResult()).getQuantityW4();
            }
            else {
                return ((PurchasePlanningRecord) query.getSingleResult()).getQuantityW5();
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("no purchase planning records found");
            return 0;
        }
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

    public String getMatName(Long matId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Item.findById");
        query.setParameter("id", matId);
        return ((Item) query.getSingleResult()).getName();
    }

    public Item getItem(Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT i FROM " + Item.class.getName() + " i WHERE i.id = :id");
        query.setParameter("id", id);
        return (Item) query.getSingleResult();
    }

    public InventoryMaterial getInvMat(Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("InventoryMaterial.findById");
        query.setParameter("id", id);
        return ((InventoryMaterial) query.getSingleResult());
    }

    public InventoryProduct getInvProd(Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("InventoryProduct.findById");
        query.setParameter("id", id);
        return ((InventoryProduct) query.getSingleResult());
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

    public InventoryMaterial getInventoryMat(Item mat, Facility fac, InvenLoc location) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT im FROM " + InventoryMaterial.class.getName() + " im WHERE im.mat = :mat AND im.fac = :fac AND im.location = :location");
        query.setParameter("fac", fac);
        query.setParameter("mat", mat);
        query.setParameter("location", location);
        List<InventoryMaterial> imList = query.getResultList();
        if (imList.isEmpty()) {
            System.err.println("no invmat found");
            return null;
        }
        InventoryMaterial invMat = imList.get(0);
        if (imList.size() > 1) {
            System.err.println("multiple invmat found");
            for (InventoryMaterial im : imList) {
                if (invMat.getQuantity() < im.getQuantity()) {
                    invMat = im;
                }
            }
        }
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
        if (imList.isEmpty()) {
            System.err.println("no invmat found");
            return null;
        }
        InventoryProduct invMat = imList.get(0);
        if (imList.size() > 1) {
            System.err.println("multiple invmat found");
            for (InventoryProduct im : imList) {
                if (invMat.getQuantity() < im.getQuantity()) {
                    invMat = im;
                }
            }
        }
        return invMat;
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

    public ShelfSlot getAvailableShelfSlot(Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT s FROM " + ShelfSlot.class.getName() + " s WHERE "
                + "s.shelf.fac = :fac AND s.occupied = '0' AND s.shelf.location = :location");
        query.setParameter("fac", fac);
        query.setParameter("location", InvenLoc.MF);
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
                + "im.id = :id AND s.shelf.fac = :fac AND s.shelf.zone = im.zone AND s.shelf.location = im.location AND s.occupied = '0'");
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
                + "im.mat.id = :id AND s.shelf.fac = :fac AND s.shelf.location = im.location AND s.occupied = '0'");
        query.setParameter("fac", fac);
        query.setParameter("id", id);
        if (query.getResultList().isEmpty()) {
            System.err.println("query other shelf slot not found");
            return null;
        } else {
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
            //System.err.println("inventorymaterial persisted!");
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
            //System.err.println("inventorymaterial persisted!");
            return true;
        }
    }

    public int getItemType(Item item) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query1 = em.createNamedQuery("Item.findById");
        query1.setParameter("id", item.getId());
        try {
            Item result = (Item) query1.getSingleResult();
            switch (result.getItemType()) {
                case "Material": return 1;
                case "Product": return 2;
                case "Ingredient": return 3;
                default: return 0;
            }
        }
        catch (Exception e) {
            return 0;
        }
//        Query query1 = em.createNamedQuery("Material.findById");
//        query1.setParameter("id", item.getId());
//        if (query1.getSingleResult() != null) {
//            return 1;
//        }
//        Query query2 = em.createNamedQuery("Product.findById");
//        query2.setParameter("id", item.getId());
//        if (query2.getSingleResult() != null) {
//            return 2;
//        }
//        Query query3 = em.createNamedQuery("Ingredient.findById");
//        query3.setParameter("id", item.getId());
//        if (query3.getSingleResult() != null) {
//            return 3;
//        }
    }
    
    public List<MrpRecord> getMrpRecord(Facility fac, Integer week, Integer year) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT m FROM " + MrpRecord.class.getName() + " m WHERE m.week = :week AND m.year = :year AND m.fac = :fac");
        query.setParameter("fac", fac);
        query.setParameter("week", week);
        query.setParameter("year", year);
        return query.getResultList();
    }
    
    public InventoryMaterial getInventoryMat(MrpRecord mr) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT i FROM " + InventoryMaterial.class.getName() + " i, " + MrpRecord.class.getName() + " m WHERE i.mat = m.mat AND i.fac = m.fac");
       
        List<InventoryMaterial> imList = new ArrayList<InventoryMaterial>();
        try {
            imList = query.getResultList();
        } catch(Exception e) {
            e.printStackTrace();
        }
        if (imList.isEmpty()) {
            System.err.println("no invmat found");
            return null;
        }
        InventoryMaterial invMat = imList.get(0);
        if (imList.size() > 1) {
            System.err.println("multiple invmat found");
            for (InventoryMaterial im : imList) {
                if (invMat.getQuantity() < im.getQuantity()) {
                    invMat = im;
                }
            }
        }
        return invMat;
    }


    public List<ProductionRecord> getProductionRecord(Facility fac, Integer period, Integer year) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT p FROM " + ProductionRecord.class.getName() + " p WHERE p.period = :period AND p.year = :year AND p.store = :fac");
        query.setParameter("fac", fac);
        query.setParameter("period", period);
        query.setParameter("year", year);
        return query.getResultList();
    }


    public InventoryMaterial getInventoryMat(DeliverySchedule ds) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT i FROM " + InventoryMaterial.class.getName() + " i, " + DeliverySchedule.class.getName() + " d WHERE i.mat d.mat AND d.mat = d.mat AND d.store.id = d.store AND i.fac = d.mf");
        return (InventoryMaterial) query.getSingleResult();
    }

    public InventoryMaterial getInventoryMat(ProductionRecord pr) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT i FROM " + InventoryMaterial.class.getName() + " i, " + DistributionMFtoStore.class.getName() + " d WHERE i.mat = pr.mat AND d.mat = pr.mat AND d.store pr.store AND i.fac = d.mf");
        return (InventoryMaterial) query.getSingleResult();
    }
    
    public Supplier getSupplier(Facility fac, Item mat) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT DISTINCT s.sup FROM " + SuppliesMatToFac.class.getName() + " s WHERE s.fac = :fac AND s.mat = :mat");
        query.setParameter("fac", fac);
        query.setParameter("mat", mat);
        return (Supplier) query.getSingleResult();
    }

}
