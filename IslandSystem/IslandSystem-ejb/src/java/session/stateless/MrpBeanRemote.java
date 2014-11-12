/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package session.stateless;

import entity.DistributionMFtoStore;
import entity.DistributionMFtoStoreProd;
import entity.Facility;
import entity.Item;
import entity.Material;
import entity.MrpRecord;
import entity.Product;
import entity.ProductionOrder;
import entity.ProductionRecord;
import entity.PurchasePlanningOrder;
import entity.PurchasePlanningRecord;
import entity.SuppliesIngrToFac;
import entity.SuppliesMatToFac;
import entity.SuppliesProdToFac;
import java.util.List;
import java.util.Map;
import javax.ejb.Remote;

/**
 *
 * @author pern
 */
@Remote
public interface MrpBeanRemote {

    Map<Long, Integer> calcRawMats(Map<Long, Integer> rawmats);

    boolean checkMfDone(int year, int period, Facility mf);

    boolean checkMfDoneKit(int year, int period, Facility fac);

    boolean checkMfDoneProd(int year, int period, Facility mf);

    List<DistributionMFtoStore> getDistribution(Facility mf);

    List<Item> getDistributionKit(Facility fac);

    List<DistributionMFtoStoreProd> getDistributionProd(Facility mf);

    Facility getFacility(long fid);

    Facility getFacility(String user);

    List<Integer> getForecast(int type, Item item, Facility store);

    int getInventoryIndiv(Item mat, Facility fac);

    Item getItem(long itemid);

    Item getItem(String name);

    Item getItem(Long id);

    Item getMat(long itemid);

    Integer getMatInventory(Facility fac, Material mat);

    List<MrpRecord> getMrpRecord(Facility mfg, Item mat);

    Item getProd(long itemid);

    Integer getProdInventory(Facility fac, Product prod);

    ProductionOrder getProductionOrder(long id);

    List<ProductionOrder> getProductionOrders();

    List<ProductionRecord> getProductionRecords(Facility mf, Item mat, int year, int period);

    List<Integer> getProductionValues(Facility store, Item mat);

    PurchasePlanningOrder getPurchasePlanningOrder(long id);

    List<PurchasePlanningOrder> getPurchasePlanningOrders();

    List<PurchasePlanningRecord> getPurchasePlanningRecords(Facility mf, Product prod, int year, int period);

    List<Integer> getPurchaseValues(Facility store, Item item);

    SuppliesIngrToFac getSitf(Facility fac, Item ingredient);

    SuppliesMatToFac getSmtf(Facility mf, Item mat);

    SuppliesProdToFac getSptf(Facility mf, Product prod);

    List<Facility> getStores(Facility mfgid);
    //1: holt
    //2: winter

    boolean persistMrpRecords(List<MrpRecord> records);

    boolean persistProductionRecords(List<ProductionRecord> prs);

    boolean persistPurchasePlanningRecords(List<PurchasePlanningRecord> prs);

    boolean persistRecord(ProductionRecord pr);

    boolean persistRecordProd(PurchasePlanningRecord pr);

    void updateProductionOrder(ProductionOrder currOrder);

    void updatePurchasePlanningOrder(PurchasePlanningOrder currOrder);
    
}
