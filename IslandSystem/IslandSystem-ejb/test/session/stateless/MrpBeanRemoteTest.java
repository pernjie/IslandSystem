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
import entity.MrpRecord;
import entity.Product;
import entity.ProductionOrder;
import entity.ProductionRecord;
import entity.PurchasePlanningOrder;
import entity.PurchasePlanningRecord;
import entity.SuppliesMatToFac;
import entity.SuppliesProdToFac;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author pern
 */
public class MrpBeanRemoteTest {
    
    MrpBeanRemote mrpBeanRemote = lookupEventSessionRemote();
    
    public MrpBeanRemoteTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Test
    public void testCalcRawMats1() {
        System.out.println("calcRawMats: 2 Tablesets (test 2 layer hierarchy)");
        // Tableset : 1 Worktable, 2 Folding Chair
        // Worktable : 5 Wooden Planks, 10 Rounded Screws
        // Folding Chair : 2 Wooden Planks, 6 Square Screws
        // Expected Raw Materials of 2 Tablesets: 
        // Wooden Plank = 2*(5+2(2)) = 18
        // Square Screw = 2*(2(6)) = 24
        // Rounded Screw = 2*(10) = 20
        
        Map<Long,Integer> rawmats = new HashMap<>();
        rawmats.put(mrpBeanRemote.getItem("Tableset Teck").getId(), 2);
        Map<Long,Integer> result = mrpBeanRemote.calcRawMats(rawmats);
        assertEquals(20,(int)result.get(mrpBeanRemote.getItem("Rounded Screw").getId()));
        assertEquals(24,(int)result.get(mrpBeanRemote.getItem("Square Screw").getId()));
        assertEquals(18,(int)result.get(mrpBeanRemote.getItem("Wooden Plank").getId()));
    }
    
    @Test
    public void testCalcRawMats2() {
        System.out.println("calcRawMats: 5 Tupperware Box (test items with no components)");
        // Tupperware Box : no components
        // Expected Raw Materials of 2 Tablesets: 
        // Tupperware Box = 5
        
        Map<Long,Integer> rawmats = new HashMap<>();
        rawmats.put(mrpBeanRemote.getItem("Tupperware Box").getId(), 5);
        Map<Long,Integer> result = mrpBeanRemote.calcRawMats(rawmats);
        assertEquals(5,(int)result.get(mrpBeanRemote.getItem("Tupperware Box").getId()));
    }
    
    @Test
    public void testCalcRawMats3() {
        System.out.println("calcRawMats: 1 Funchair, 1 Swivelchair (test overlapping raw materials)");
        // Funchair : 4 Wheels, 6 Square Screws
        // Swivelchair : 4 Wheels, 4 Rounded Screws
        // Expected Raw Materials of 2 Tablesets: 
        // Wheel : 4+4 = 8
        // Square Screws : 6
        // Rounded Screws : 4
        
        Map<Long,Integer> rawmats = new HashMap<>();
        rawmats.put(mrpBeanRemote.getItem("Funchair Felicia").getId(), 1);
        rawmats.put(mrpBeanRemote.getItem("Spinny Timmy").getId(), 1);
        Map<Long,Integer> result = mrpBeanRemote.calcRawMats(rawmats);
        assertEquals(8,(int)result.get(mrpBeanRemote.getItem("Wheel 10x10").getId()));
        assertEquals(4,(int)result.get(mrpBeanRemote.getItem("Rounded Screw").getId()));
        assertEquals(6,(int)result.get(mrpBeanRemote.getItem("Square Screw").getId()));
    }
    
    @Test
    public void testGetDistribution() {
        System.out.println("getDistribution : normal case");
        assertTrue(mrpBeanRemote.getDistribution(mrpBeanRemote.getFacility("pernjie@gmail.com")).size() > 0);
    }
    
    @Test
    public void testGetFacility() {
        System.out.println("getFacility : normal case");
        assertEquals("Jurong MF", mrpBeanRemote.getFacility(mrpBeanRemote.getFacility("pernjie@gmail.com").getId()).getName());
    }
    
    @Test
    public void testGetForecast() {
        System.out.println("getForecast : normal case");
        assertTrue(mrpBeanRemote.getForecast(1,mrpBeanRemote.getItem("Worktable Willy"),mrpBeanRemote.getFacility("tampines@if.com")).size() > 0);
    }
 
    @Test
    public void testGetInventoryIndiv() {
        System.out.println("getInventoryIndiv : normal case");
        assertTrue(mrpBeanRemote.getInventoryIndiv(mrpBeanRemote.getItem("Worktable Willy"),mrpBeanRemote.getFacility("tampines@if.com")) == 0);
    }
    
    @Test
    public void testGetMat() {
        System.out.println("getMat : normal case");
        assertEquals("Worktable Willy", mrpBeanRemote.getMat(mrpBeanRemote.getItem("Worktable Willy").getId()).getName());
    }
    
    @Test
    public void testGetProd() {
        System.out.println("getProd : normal case");
        assertEquals("Lays Potato Chips", mrpBeanRemote.getProd(mrpBeanRemote.getItem("Lays Potato Chips").getId()).getName());
    }

    @Test
    public void testGetMrpRecord() {
        System.out.println("getMrpRecord : normal case");
        assertTrue(mrpBeanRemote.getMrpRecord(mrpBeanRemote.getFacility("tampines@if.com"),mrpBeanRemote.getItem("Wooden Plank")).size() > 0);
    }
    
    @Test
    public void testGetSmtf() {
        System.out.println("getSmtf : normal case");
        assertTrue(mrpBeanRemote.getSmtf(mrpBeanRemote.getFacility("pernjie@gmail.com"),mrpBeanRemote.getItem("Rounded Screw")) != null);
    }
    
    @Test
    public void testGetSptf() {
        System.out.println("getSptf : normal case");
        assertTrue(mrpBeanRemote.getSptf(mrpBeanRemote.getFacility("pernjie@gmail.com"), (Product) mrpBeanRemote.getItem("Lays Potato Chips")) != null);
    }
    
    @Test
    public void testGetProductionOrders() {
        System.out.println("getProductionOrders : normal case");
        assertTrue(mrpBeanRemote.getProductionOrders().size() > 0);
    }
    
    @Test
    public void testGetPurchasePlanningOrders() {
        System.out.println("getPurchasePlanningOrders : normal case");
        assertTrue(mrpBeanRemote.getPurchasePlanningOrders().size() > 0);
    }
    
    @Test
    public void testGetStores() {
        System.out.println("getStores : normal case");
        assertTrue(mrpBeanRemote.getStores(mrpBeanRemote.getFacility("pernjie@gmail.com")).size() > 0);
    }
    
    @Test
    public void testGetProductionValues() {
        System.out.println("getProductionValues : normal case");
        assertTrue(mrpBeanRemote.getProductionValues(mrpBeanRemote.getFacility("tampines@if.com"),mrpBeanRemote.getItem("Worktable Willy")).size() > 0);
    }
    
    @Test
    public void testGetPurchaseValues() {
        System.out.println("getPurchaseValues : normal case");
        assertTrue(mrpBeanRemote.getPurchaseValues(mrpBeanRemote.getFacility("tampines@if.com"),mrpBeanRemote.getItem("Lays Potato Chips")).size() > 0);
    }
    
    @Test
    public void testGetProductionRecords() {
        System.out.println("getProductionRecords : normal case");
        assertTrue(mrpBeanRemote.getProductionRecords(mrpBeanRemote.getFacility("tampines@if.com"),mrpBeanRemote.getItem("Worktable Willy"),2014,6).size() > 0);
    }
    
    @Test
    public void testGetPurchasePlanningRecords() {
        System.out.println("getPurchasePlanning : normal case");
        assertTrue(mrpBeanRemote.getPurchasePlanningRecords(mrpBeanRemote.getFacility("tampines@if.com"), (Product) mrpBeanRemote.getItem("Lays Potato Chips"),2014,6).size() > 0);
    }
    
    @Test
    public void testGetProductionOrder() {
        System.out.println("getProductionOrder : normal case");
        assertTrue(mrpBeanRemote.getProductionOrder(mrpBeanRemote.getProductionOrders().get(0).getId()) != null);
    }
    
    @Test
    public void testGetPurchasePlanningOrder() {
        System.out.println("getPurchasePlanningOrder : normal case");
        assertTrue(mrpBeanRemote.getPurchasePlanningOrder(mrpBeanRemote.getPurchasePlanningOrders().get(0).getId()) != null);
    }
    
    @Test
    public void testPersistProductionOrder() {
        System.out.println("persistProductionOrder : normal case");
        ProductionRecord pr = new ProductionRecord();
        pr.setYear(2000);
        assertTrue(mrpBeanRemote.persistRecord(pr));
    }
    
    @Test
    public void testPersistPurchasePlanningOrder() {
        System.out.println("persistPurchasePlanningOrder : normal case");
        PurchasePlanningRecord pr = new PurchasePlanningRecord();
        pr.setYear(2000);
        assertTrue(mrpBeanRemote.persistRecordProd(pr));
    }
    
    @Test
    public void testPersistProductionRecords() {
        System.out.println("persistProductionRecords : normal case");
        ProductionRecord pr = new ProductionRecord();
        pr.setYear(2000);
        ProductionRecord pr2 = new ProductionRecord();
        pr2.setYear(2000);
        assertTrue(mrpBeanRemote.persistProductionRecords(Arrays.asList(pr, pr2)));
    }
    
    @Test
    public void testPersistPurchasePlanningRecords() {
        System.out.println("persistPurchasePlanningRecords : normal case");
        PurchasePlanningRecord pr = new PurchasePlanningRecord();
        pr.setYear(2000);
        PurchasePlanningRecord pr2 = new PurchasePlanningRecord();
        pr2.setYear(2000);
        assertTrue(mrpBeanRemote.persistPurchasePlanningRecords(Arrays.asList(pr, pr2)));
    }
    
    @Test
    public void testPersistMrpRecords() {
        System.out.println("persistMrpRecords : normal case");
        MrpRecord mr = new MrpRecord();
        mr.setYear(2000);
        MrpRecord mr2 = new MrpRecord();
        mr2.setYear(2000);
        assertTrue(mrpBeanRemote.persistMrpRecords(Arrays.asList(mr, mr2)));
    }
    
    @Test
    public void testUpdateProductionOrder() {
        System.out.println("updateProductionOrder : normal case");
        ProductionOrder temp = mrpBeanRemote.getProductionOrders().get(0);
        int qty = temp.getQuantity();
        temp.setQuantity(qty+10);
        mrpBeanRemote.updateProductionOrder(temp);
        assertEquals(mrpBeanRemote.getProductionOrder(temp.getId()).getQuantity().intValue(),qty+10);
        temp.setQuantity(qty);
        mrpBeanRemote.updateProductionOrder(temp);
    }
    
    @Test
    public void testUpdatePurchasePlanningOrder() {
        System.out.println("updatePurchasePlanningOrder : normal case");
        PurchasePlanningOrder temp = mrpBeanRemote.getPurchasePlanningOrders().get(0);
        int qty = temp.getQuantity();
        temp.setQuantity(qty+10);
        mrpBeanRemote.updatePurchasePlanningOrder(temp);
        assertEquals(mrpBeanRemote.getPurchasePlanningOrder(temp.getId()).getQuantity().intValue(),qty+10);
        temp.setQuantity(qty);
        mrpBeanRemote.updatePurchasePlanningOrder(temp);
    }
    
    @Test
    public void testCheckMfDone1() {
        System.out.println("checkMfDone : done");
        assertTrue(mrpBeanRemote.checkMfDone(2014, 12, mrpBeanRemote.getFacility("pernjie@gmail.com")));
    }
    
    @Test
    public void testCheckMfDone2() {
        System.out.println("checkMfDone : not done");
        assertFalse(mrpBeanRemote.checkMfDone(2013, 12, mrpBeanRemote.getFacility("pernjie@gmail.com")));
    }
    
    @Test
    public void testCheckMfDoneProd1() {
        System.out.println("checkMfDoneProd : done");
        assertTrue(mrpBeanRemote.checkMfDoneProd(2014, 12, mrpBeanRemote.getFacility("pernjie@gmail.com")));
    }
    
    @Test
    public void testCheckMfDoneProd2() {
        System.out.println("checkMfDoneProd : not done");
        assertFalse(mrpBeanRemote.checkMfDoneProd(2013, 12, mrpBeanRemote.getFacility("pernjie@gmail.com")));
    }
    
    private MrpBeanRemote lookupEventSessionRemote()
    {
        try 
        {
            Context c = new InitialContext();
            return (MrpBeanRemote) c.lookup("java:global/IslandSystem/IslandSystem-ejb/MrpBean!session.stateless.MrpBeanRemote");
        }
        catch (NamingException ne)
        {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
