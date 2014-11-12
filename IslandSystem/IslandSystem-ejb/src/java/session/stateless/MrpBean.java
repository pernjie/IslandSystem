package session.stateless;

import classes.BasicComponent;
import classes.WeekHelper;
import entity.Component;
import entity.DistributionMFtoStore;
import entity.DistributionMFtoStoreProd;
import entity.Facility;
import entity.InventoryMaterial;
import entity.InventoryProduct;
import entity.Item;
import entity.Material;
import entity.MrpRecord;
import entity.Product;
import entity.ProductionOrder;
import entity.ProductionRecord;
import entity.PurchasePlanningOrder;
import entity.PurchasePlanningRecord;
import entity.RegionItemPrice;
import entity.Staff;
import entity.SuppliesIngrToFac;
import entity.SuppliesMatToFac;
import entity.SuppliesProdToFac;
import entity.TransactionItem;
import entity.TransactionRecord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.apache.commons.math3.stat.regression.SimpleRegression;

@Stateless
@LocalBean
public class MrpBean implements MrpBeanRemote {
     
    @Override
    public boolean persistRecord(ProductionRecord pr) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.merge(pr);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
            return true;
        }
    }
    
    
     
    @Override
    public boolean persistRecordProd(PurchasePlanningRecord pr) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.merge(pr);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
            return true;
        }
    }
    
    
     
    @Override
    public boolean persistMrpRecords(List<MrpRecord> records) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            for (int i = 0; i < records.size(); i++) {
                em.merge(records.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        } finally {
            em.close();
            return true;
        }
    }
    
     
    @Override
    public Integer getMatInventory(Facility fac, Material mat) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("SELECT SUM(im.quantity) FROM " + InventoryMaterial.class.getName() + " im WHERE "
                + "im.mat = :mat AND im.fac = :fac");
        query.setParameter("mat", mat);
        query.setParameter("fac", fac);

        try {
            Long result = (long) query.getSingleResult();
            return result.intValue();
        } catch (Exception e) {
            return 0;
        }
    }
    
     
    @Override
    public Integer getProdInventory(Facility fac, Product prod) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("SELECT SUM(ip.quantity) FROM " + InventoryProduct.class.getName() + " ip WHERE "
                + "ip.prod = :prod AND ip.fac = :fac");
        query.setParameter("prod", prod);
        query.setParameter("fac", fac);

        try {
            Long result = (long) query.getSingleResult();
            return result.intValue();
        } catch (Exception e) {
            return 0;
        }
    }
    
     
    @Override
    public List<ProductionOrder> getProductionOrders() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT p FROM " + ProductionOrder.class.getName() + " p "
        + "WHERE p.status = 'pending'");
        
        return query.getResultList();
    }
    
    
     
    @Override
    public List<PurchasePlanningOrder> getPurchasePlanningOrders() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT p FROM " + PurchasePlanningOrder.class.getName() + " p "
        + "WHERE p.status = 'pending'");
        
        return query.getResultList();
    }

    
     
    @Override
    public List<DistributionMFtoStore> getDistribution(Facility mf) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT d FROM " + DistributionMFtoStore.class.getName() + " d "
        + "WHERE d.mf = :mf");
        query.setParameter("mf", mf);

        List<DistributionMFtoStore> dist = query.getResultList();
        return dist;
    }
    
    
     
    @Override
    public List<DistributionMFtoStoreProd> getDistributionProd(Facility mf) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT d FROM " + DistributionMFtoStoreProd.class.getName() + " d "
        + "WHERE d.mf = :mf");
        query.setParameter("mf", mf);

        List<DistributionMFtoStoreProd> dist = query.getResultList();
        return dist;
    }
    
     
    @Override
    public List<Item> getDistributionKit(Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT d FROM " + RegionItemPrice.class.getName() + " d, "
        + Facility.class.getName() + " f WHERE d.region = f.region AND f = :fac");
        query.setParameter("fac", fac);

        List<RegionItemPrice> dist = query.getResultList();
        List<Item> items = new ArrayList<>();
        for (RegionItemPrice d : dist) {
            if (d.getItem().getItemType().equals("MenuItem") || d.getItem().getItemType().equals("SetItem"))
                items.add(d.getItem());
        }
        return items;
    }
    
    
     
    @Override
    public Facility getFacility(long fid) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findById");
        query.setParameter("id", fid);

        return (Facility) query.getSingleResult();
    }
     
    @Override
    public Facility getFacility(String user) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Staff.findByEmail");
        query.setParameter("email", user);

        Staff staff = (Staff) query.getSingleResult();
        return staff.getFac();
    }    
     
    @Override
    public Item getMat(long itemid) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Material.findById");
        query.setParameter("id", itemid);

        return (Item) query.getSingleResult();
    }
    
     
    @Override
    public Item getProd(long itemid) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Product.findById");
        query.setParameter("id", itemid);

        return (Item) query.getSingleResult();
    }
    
    @Override
    public Item getItem(long itemid) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Item.findById");
        query.setParameter("id", itemid);

        return (Item) query.getSingleResult();
    }
     
    @Override
    public Item getItem(String name) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Material.findByName");
        query.setParameter("name", name);
        try {
            query.getSingleResult();
        }
        catch (Exception e) {
            query = em.createNamedQuery("Product.findByName");
            query.setParameter("name", name);
        }

        return (Item) query.getSingleResult();
    }
    
     
    @Override
    public SuppliesMatToFac getSmtf(Facility mf,Item mat) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT s FROM " + SuppliesMatToFac.class.getName() + " s "
        + "WHERE s.fac = :mf AND s.mat = :mat");
        query.setParameter("mf", mf);
        query.setParameter("mat", mat);
        
        return (SuppliesMatToFac) query.getSingleResult();
    }
    
    @Override
    public SuppliesIngrToFac getSitf(Facility fac,Item ingredient) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT s FROM " + SuppliesIngrToFac.class.getName() + " s "
        + "WHERE s.fac = :fac AND s.ingredient = :ingredient");
        query.setParameter("fac", fac);
        query.setParameter("ingredient", ingredient);
        
        return (SuppliesIngrToFac) query.getSingleResult();
    }
     
    @Override
    public SuppliesProdToFac getSptf(Facility mf,Product prod) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT s FROM " + SuppliesProdToFac.class.getName() + " s "
        + "WHERE s.fac = :mf AND s.prod = :prod");
        query.setParameter("mf", mf);
        query.setParameter("prod", prod);
        
        return (SuppliesProdToFac) query.getSingleResult();
    }
    
    
     
    @Override
    public Map<Long,Integer> calcRawMats(Map<Long,Integer> rawmats) {
        List<BasicComponent> basiclist = new ArrayList<BasicComponent>();
        Map<Long,Integer> finalrawmats = new HashMap<Long,Integer>();
        
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        
        Iterator it = rawmats.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            //System.out.println(pairs.getKey() + " = " + pairs.getValue());
            BasicComponent bc = new BasicComponent();
            bc.setMatno((long)pairs.getKey());
            basiclist.add(bc);
            getBasicComps((long)pairs.getKey(),1,getItem((long)pairs.getKey()),em, basiclist);
        }
        
        Iterator it2 = rawmats.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry pairs = (Map.Entry) it2.next();
            //get mapping list for that material
            Map<Long, Integer> list = null;
            for (int i = 0; i < basiclist.size(); i++) {
                if (basiclist.get(i).getMatno() == (long) pairs.getKey()) {
                    list = basiclist.get(i).getComponents();
                    break;
                }
            }
            
            Iterator innerit = list.entrySet().iterator();
            while (innerit.hasNext()) {
                Map.Entry innerpairs = (Map.Entry) innerit.next();
                Integer val = finalrawmats.get(innerpairs.getKey());
                if (val != null) {
                    finalrawmats.put((long)innerpairs.getKey(), (int) finalrawmats.get(innerpairs.getKey()) + ((int)innerpairs.getValue() * (int)pairs.getValue()) );
                } else {
                    finalrawmats.put((long)innerpairs.getKey(), (int)innerpairs.getValue() * (int)pairs.getValue());
                }
            }
            
            System.out.println("final: " + finalrawmats.toString());
        }
        return finalrawmats;
    }
    
    private void getBasicComps(long firstmatno, int amt, Item main, EntityManager em, List<BasicComponent> basiclist) {
        Query query = em.createQuery(
                "SELECT c FROM " + Component.class.getName()
                + " c WHERE c.main = :main"
        );
        query.setParameter("main", main);
        
        List<Component> resultList = query.getResultList();
        
        //more components, perform recursion
        System.out.println("main name:" + main.getName());
        if (!resultList.isEmpty()) {
            System.out.println("main size:" + resultList.size());
            for (int i=0;i<resultList.size();i++) {
                getBasicComps(
                        firstmatno,
                        resultList.get(i).getQuantity() * amt,
                        resultList.get(i).getConsistOf(),
                        em,
                        basiclist
                );
            }
        }
        //no more components
        else {            
            //get mapping list for that material
            Map<Long,Integer> list = null;
            for (int i=0;i<basiclist.size();i++) {
                if (basiclist.get(i).getMatno()==firstmatno) {
                    list = basiclist.get(i).getComponents();
                    break;
                }
            }
            
            Integer val = list.get(main.getId());
            if (val != null) {
                list.put(main.getId(), list.get(main.getId())+amt);
            }
            else {
                list.put(main.getId(), amt);
            }
        }
    }
    
    
     
    @Override
    public List<Facility> getStores(Facility mfgid) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT DISTINCT f FROM " + Facility.class.getName() + " f, " + DistributionMFtoStore.class.getName() + " d "
                                + "WHERE d.mf = :mfgid AND f = d.store");
        query.setParameter("mfgid", mfgid);
        
        return query.getResultList();
    }

    //1: holt
    //2: winter
    
     
    @Override
    public List<Integer> getForecast(int type, Item item, Facility store) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        WeekHelper wh = new WeekHelper();
        
        System.out.println("forecasting:" + item.getName());

        List<Double> history = new ArrayList<Double>();
        for (int i = -6; i < 0; i++) {
            Query query = em.createQuery("SELECT SUM(tf.quantity) FROM " + TransactionItem.class.getName() + " tf, " + TransactionRecord.class.getName() + " t "
                    + "WHERE tf.transact = t "
                    + "AND tf.item = :item "
                    + "AND t.transactTime >= '" + wh.periodToDate(wh.getYear(i), wh.getPeriod(i)) + "' "
                    + "AND t.transactTime < '" + wh.periodToDate(wh.getYear(i + 1), wh.getPeriod(i + 1)) + "' "
                    + "AND t.fac = :store");
            query.setParameter("item", item);
            query.setParameter("store", store);

            if (query.getSingleResult() != null) {
                history.add(((Long) query.getSingleResult()).doubleValue());
            } else {
                history.add(0.1);
            }
        }
        System.out.println(history.toString());
        //holt
        if (type == 1) {
            return calculateHolt(6, history);
        } else if (type == 2) {
            return calculateWinter(6, history);
        }
        return null;
    }
    
    
     
    @Override
    public List<MrpRecord> getMrpRecord(Facility mfg,Item mat) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT m FROM " + MrpRecord.class.getName() + " m WHERE m.mat = :mat");
        query.setParameter("mat", mat);

        return query.getResultList();
    }
    
    
     
    @Override
    public ProductionOrder getProductionOrder(long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("ProductionOrder.findById");
        query.setParameter("id", id);
        
        return (ProductionOrder) query.getSingleResult();
    }
    
    
     
    @Override
    public PurchasePlanningOrder getPurchasePlanningOrder(long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("PurchasePlanningOrder.findById");
        query.setParameter("id", id);
        
        return (PurchasePlanningOrder) query.getSingleResult();
    }
    
    
     
    @Override
    public List<ProductionRecord> getProductionRecords(Facility mf, Item mat, int year, int period) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT DISTINCT p FROM " + ProductionRecord.class.getName() + " p,"
                + DistributionMFtoStore.class.getName() + " d "
                + "WHERE d.mf = :mf"
                + " AND d.store = p.store"
                + " AND p.mat = :mat"
                + " AND p.year > " + year
                + " OR (p.year = " + year
                + " AND p.period > " + period
                + " )");
        query.setParameter("mf", mf);
        query.setParameter("mat", mat);
        
        return query.getResultList();
    }
    
    
     
    @Override
    public List<PurchasePlanningRecord> getPurchasePlanningRecords(Facility mf, Product prod, int year, int period) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT DISTINCT p FROM " + PurchasePlanningRecord.class.getName() + " p,"
                + DistributionMFtoStore.class.getName() + " d "
                + "WHERE d.mf = :mf"
                + " AND d.store = p.store"
                + " AND p.prod = :prod"
                + " AND p.year > " + year
                + " OR (p.year = " + year
                + " AND p.period > " + period
                + " )");
        query.setParameter("mf", mf);
        query.setParameter("prod", prod);
        
        return query.getResultList();
    }
    
    
     
    @Override
    public List<Integer> getProductionValues(Facility store, Item mat) {
        WeekHelper wh = new WeekHelper();
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        List<Integer> values = new ArrayList<Integer>();
        for (int i = 0; i < 6; i++) {
            Query query = em.createQuery("SELECT p FROM " + ProductionRecord.class.getName() + " p"
                    + " WHERE p.mat = :mat"
                    + " AND p.store = :store"
                    + " AND p.year = " + wh.getYear(i)
                    + " AND p.period = " + wh.getPeriod(i)
            );
            query.setParameter("store", store);
            query.setParameter("mat", mat);

            if (query.getResultList().size() > 0) {
                ProductionRecord pr = ((ProductionRecord) query.getSingleResult());
                values.add(pr.getQuantityW1() + pr.getQuantityW2() + pr.getQuantityW3() + pr.getQuantityW4());
            } else {
                values.add(0);
            }
        }
        return values;
    }
    
    
     
    @Override
    public List<Integer> getPurchaseValues(Facility store, Item item) {
        WeekHelper wh = new WeekHelper();
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        List<Integer> values = new ArrayList<Integer>();
        for (int i = 0; i < 6; i++) {
            Query query = em.createQuery("SELECT p FROM " + PurchasePlanningRecord.class.getName() + " p"
                    + " WHERE p.prod = :prod"
                    + " AND p.store = :store"
                    + " AND p.year = " + wh.getYear(i)
                    + " AND p.period = " + wh.getPeriod(i)
            );
            query.setParameter("store", store);
            query.setParameter("prod", item);

            if (query.getResultList().size() > 0) {
                PurchasePlanningRecord pr = ((PurchasePlanningRecord) query.getSingleResult());
                values.add(pr.getQuantityW1() + pr.getQuantityW2() + pr.getQuantityW3() + pr.getQuantityW4());
            } else {
                values.add(0);
            }
        }
        return values;
    }
     
    @Override
    public int getInventoryIndiv(Item mat, Facility fac) {
        return 0;
    }
    
    private static List<Integer> calculateWinter(int p, List<Double> data) {
        System.out.println("Initialising Winter");
        final double a = 0.1;
        final double b = 0.2;
        final double g = 0.1;
        ArrayList<Integer> forecastList = new ArrayList<Integer>();
        ArrayList<Double> errorList = new ArrayList<Double>();
        SimpleRegression regression = new SimpleRegression();

        double x = 0.0;
        double y = 0.0;
        for (int i = 2; i < data.size() - 2; i++) {
            x = i + 1;
            y = (data.get(i - 2) + data.get(i + 2) + 2 * (data.get(i - 1) + data.get(i) + data.get(i + 1))) / 8;

            regression.addData(x, y);
        }

        double lead = regression.getIntercept();
        // displays intercept of regression line

        double trend = regression.getSlope();
        // displays slope of regression line

        // displays slope standard error
        ArrayList<Double> deseasonDemand = new ArrayList<Double>();
        for (int i = 0; i < data.size(); i++) {
            //System.out.println(i);    
            deseasonDemand.add(lead + (trend * (i + 1)));
        }
        
        ArrayList<Double> seasonArr = new ArrayList<Double>();
        for (int i = 0; i < data.size(); i++) {
            seasonArr.add(data.get(i) / deseasonDemand.get(i));
        }

        double numSeasonalCycles = data.size() / p;
        //find for the first 4 periods using average of 1,5,9 for 1, 2,6,10 for 2 so on until 12.
        for (int i = 0; i < p; i++) {
            double newSeasonVal = 0.0;
            for (int j = i; j < seasonArr.size(); j += p) {
                newSeasonVal += seasonArr.get(j);
            }
            if (p < seasonArr.size()) {
                seasonArr.set(i, newSeasonVal / numSeasonalCycles);
            } else {
                seasonArr.add(i, newSeasonVal / numSeasonalCycles);
            }

        }
        List<Double> seasonList = new ArrayList<Double>();
        for (int i = 0; i < numSeasonalCycles; i++) {
            for (int j = 0; j < p; j++) {
                seasonList.add(seasonArr.get(j));
                //     System.out.println("seasonList: "+j+"value: "+seasonList.get(j));
            }
        }

        //Part 2
        int i;
        for (i = 0; i < data.size(); i++) {
            double forecast = (lead + trend) * seasonList.get(i);
            //forecastList.add(forecast);
            double error = forecast - data.get(i);
            errorList.add(error);
            double oldLead = lead;
            lead = a * (data.get(i) / seasonList.get(i)) + (1 - a) * (oldLead + trend);
            double oldTrend = trend;
            trend = b * (lead - oldLead) + (1 - b) * oldTrend;
            //System.out.println("check: " + i);
            if (i + p < seasonList.size()) {
                seasonList.set((i + p), g * (data.get(i) / lead) + (1 - g) * seasonList.get(i));
            } else {
                seasonList.add((i + p), g * (data.get(i) / lead) + (1 - g) * seasonList.get(i));
            }
        }
        for (int j = i; j < i + p; j++) {
            Double forecast = (lead + trend) * seasonList.get(j);
            forecastList.add(forecast.intValue());
        }
        
        return forecastList;
    }

    private static List<Integer> calculateHolt(int p, List<Double> data) {
        System.out.println("Initialising Holt");
        final double a = 0.1;
        final double b = 0.2;
//        final double g = 0.1;
        ArrayList<Integer> forecastList = new ArrayList<Integer>();
        ArrayList<Double> errorList = new ArrayList<Double>();
        SimpleRegression regression = new SimpleRegression();
        //      List<Double> data = Arrays.asList(8000.0, 13000.0, 23000.0, 34000.0, 10000.0, 18000.0, 23000.0, 38000.0, 12000.0, 13000.0, 32000.0, 41000.0);

        double x = 0.0;
        double y = 0.0;
        //calculate lead and trend
        for (int i = 2; i < data.size() - 2; i++) {
            x = i + 1;
            y = (data.get(i - 2) + data.get(i + 2) + 2 * (data.get(i - 1) + data.get(i) + data.get(i + 1))) / 8;
//            System.out.println("x: " + x);
//            System.out.println("y: " + y);
            regression.addData(x, y);
        }

        double lead = regression.getIntercept();
// displays intercept of regression line

        double trend = regression.getSlope();
// displays slope of regression line

        //calculate deseasonalDemand
        ArrayList<Double> deseasonDemand = new ArrayList<Double>();
        for (int i = 0; i < data.size(); i++) {
            //System.out.println(i);    
            deseasonDemand.add(lead + (trend * (i + 1)));
        }
//        System.out.println("deseason demand: " + deseasonDemand);

        //System.out.println(deseasonDemand);
//        System.out.println("lead 0: " + lead);
//        System.out.println("trend 0: " + trend);
//Part 2
        int i;
        for (i = 0; i < data.size(); i++) {
            double forecast = lead + trend;
            //forecastList.add(forecast);
            double error = forecast - data.get(i);
            errorList.add(error);
            double oldLead = lead;
            lead = a * (data.get(i)) + (1 - a) * (oldLead + trend);
            double oldTrend = trend;
            trend = b * (lead - oldLead) + (1 - b) * oldTrend;
        }
        for (int j = 1; j <= p; j++) {
            Double forecast = lead + j * trend;
            forecastList.add(forecast.intValue());
        }
        
        return forecastList;
    }

    
     
    @Override
    public boolean persistProductionRecords(List<ProductionRecord> prs) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            for (int i = 0; i < prs.size(); i++) {
                em.persist(prs.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
            return true;
        }
    }
    
    
     
    @Override
    public boolean persistPurchasePlanningRecords(List<PurchasePlanningRecord> prs) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            for (int i = 0; i < prs.size(); i++) {
                em.persist(prs.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
            return true;
        }
    }
    
    
     
    @Override
    public boolean checkMfDone(int year, int period, Facility mf) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT d FROM " + Facility.class.getName() +
                " f, " + DistributionMFtoStore.class.getName() + " d WHERE "
                + "f = d.store AND d.mf = :mf");
        query.setParameter("mf", mf);
        DistributionMFtoStore d = (DistributionMFtoStore) query.getResultList().get(0);
        
        query = em.createQuery("SELECT 1 FROM " + ProductionRecord.class.getName() + 
                " p WHERE p.year = " + year +
                        " AND p.period = " + period +
                                " AND p.store = :store "
                + "AND p.mat = :mat");
        query.setParameter("store", d.getStore());
        query.setParameter("mat", d.getMat());
        return (!query.getResultList().isEmpty());
    }
    
    
     
    @Override
    public boolean checkMfDoneProd(int year, int period, Facility mf) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT f FROM " + Facility.class.getName() +
                " f, " + DistributionMFtoStoreProd.class.getName() + " d WHERE "
                + "f = d.store AND d.mf = :mf");
        query.setParameter("mf", mf);
        Facility s = (Facility) query.getResultList().get(0);
        
        query = em.createQuery("SELECT 1 FROM " + PurchasePlanningRecord.class.getName() + 
                " p WHERE p.year = " + year +
                        " AND p.period = " + period +
                                " AND p.store = :store");
        query.setParameter("store", s);
        return (!query.getResultList().isEmpty());
    }

    
     
    @Override
    public void updateProductionOrder(ProductionOrder currOrder) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.merge(currOrder);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    
     
    @Override
    public void updatePurchasePlanningOrder(PurchasePlanningOrder currOrder) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.merge(currOrder);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
     
    @Override
    public Item getItem(Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT i FROM " + Item.class.getName() + " i WHERE i.id = :id");
        query.setParameter("id", id);
        return (Item) query.getSingleResult();
    }

    @Override
    public boolean checkMfDoneKit(int year, int period, Facility fac) {
        Item item = getDistributionKit(fac).get(0);
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        
        Query query = em.createQuery("SELECT 1 FROM " + ProductionRecord.class.getName() + 
                " p WHERE p.year = " + year +
                        " AND p.period = " + period +
                                " AND p.store = :store "
                + "AND p.mat = :mat");
        query.setParameter("store", fac);
        query.setParameter("mat", item);
        return (!query.getResultList().isEmpty());
    }
}
