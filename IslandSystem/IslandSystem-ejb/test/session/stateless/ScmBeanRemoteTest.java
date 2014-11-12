///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package session.stateless;
//
//import entity.Bill;
//import entity.DeliverySchedule;
//import entity.DistributionMFtoStore;
//import entity.Facility;
//import entity.Material;
//import entity.Poproduct;
//import entity.Porecord;
//import entity.Product;
//import entity.ProductionRecord;
//import entity.PurchasePlanningRecord;
//import entity.Supplier;
//import entity.SuppliesProdToFac;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import org.junit.AfterClass;
//import static org.junit.Assert.*;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
///**
// *
// * @author pern
// */
//public class ScmBeanRemoteTest {
//    
//    ScmBeanRemote scmBeanRemote = lookupEventSessionRemote();
//    
//    public ScmBeanRemoteTest() {
//    }
//    
//    @BeforeClass
//    public static void setUpClass() {
//    }
//    
//    @AfterClass
//    public static void tearDownClass() {
//    }
//    
//    @Test
//    public void testGetAllFurniture() {
//        assertTrue(scmBeanRemote.getAllFurniture().size() > 0);
//    }
//    
//    @Test
//    public void testGetAllProducts() {
//        assertTrue(scmBeanRemote.getAllProducts().size() > 0);
//    }
//    
////    @Test
////    public void testGetAllStores() {
////        assertTrue(scmBeanRemote.getAllStores().size() > 0);
////    }
//    
//    @Test
//    public void testGetFacility() {
//        assertEquals("Jurong MF", scmBeanRemote.getFacility(scmBeanRemote.getFacility("Jurong MF").getId()).getName());
//    }
//    
//    @Test
//    public void testGetDistribution() {
//        assertTrue(scmBeanRemote.getDistribution(scmBeanRemote.getFacility("Jurong MF")).size()>0);
//    }
//    
//    @Test
//    public void testGetItem() {
//        assertEquals("Worktable Willy",scmBeanRemote.getItem(scmBeanRemote.getItem("Worktable Willy").getId()).getName());
//    }
//    
////    @Test
////    public void testGetLeadTime() {
////        assertTrue(scmBeanRemote.getLeadTime(scmBeanRemote.getSupplier("Teck Soon Pte Ltd"),scmBeanRemote.getFacility("Jurong MF"),scmBeanRemote.getItem("Worktable Willy"))>0);
////    }
////    
////    @Test
////    public void testGetLotSize() {
////        assertTrue(scmBeanRemote.getLeadTime(scmBeanRemote.getSupplier("Teck Soon Pte Ltd"),scmBeanRemote.getFacility("Jurong MF"),scmBeanRemote.getItem("Worktable Willy"))>0);
////    }
////    
////    @Test
////    public void testGetMatQty() {
////        assertTrue(scmBeanRemote.getLeadTime(scmBeanRemote.getSupplier("Teck Soon Pte Ltd"),scmBeanRemote.getFacility("Jurong MF"),scmBeanRemote.getItem("Worktable Willy"))>0);
////    }
////    
////    @Test
////    public void testGetUnitPrice() {
////        assertTrue(scmBeanRemote.getUnitPrice(scmBeanRemote.getSupplier("Teck Soon Pte Ltd"),scmBeanRemote.getFacility("Jurong MF"),scmBeanRemote.getItem("Worktable Willy")) > 0.0);
////    }
//    
//    @Test
//    public void testGetSuppliers() {
//        assertTrue(scmBeanRemote.getSuppliers(scmBeanRemote.getFacility("Jurong MF")).size()>0);
//    }
//    
//    @Test
//    public void testGetProductionRecords() {
//        assertTrue(scmBeanRemote.getProductionRecords(scmBeanRemote.getFacility("Jurong MF"),2014,6).size()>0);
//    }
//    
//    @Test
//    public void testGetPurchasePlanningRecords() {
//        assertTrue(scmBeanRemote.getPurchasePlanningRecords(scmBeanRemote.getFacility("Jurong MF"),2014,6).size()>0);
//    }
//    
//    @Test
//    public void testGetSupplier() {
//        assertEquals("Teck Soon Pte Ltd", scmBeanRemote.getSupplier(scmBeanRemote.getSupplier("Teck Soon Pte Ltd").getId()).getName());
//    }
//    
//    @Test
//    public void testGetSuppliesProdToFac() {
//        assertTrue(scmBeanRemote.getSuppliesProdToFac(scmBeanRemote.getFacility("Jurong MF"),scmBeanRemote.getSupplier("Teck Soon Pte Ltd")).size()>0);
//    }
//    
//    @Test
//    public void testGetUnpaidBills() {
//        assertTrue(scmBeanRemote.getUnpaidBills().size()>0);
//    }
//    
//    @Test
//    public void testPersistBill() {
//        Bill bill = new Bill();
//        bill.setAmount(1000.0);
//        assertTrue(scmBeanRemote.persistBill(bill));
//    }
//    
//    @Test
//    public void testPersistPo() {
//        Porecord pr = new Porecord();
//        pr.setTotalPrice(1000.0);
//        assertTrue(scmBeanRemote.persistPo(pr));
//    }
//    
//    @Test
//    public void testPersistPoDetails() {
//        Poproduct pi = new Poproduct();
//        pi.setTotalPrice(1000.0);
//        Poproduct pi2 = new Poproduct();
//        pi2.setTotalPrice(1000.0);
//        assertTrue(scmBeanRemote.persistPoDetails(Arrays.asList(pi,pi2)));
//    }
//
//    private ScmBeanRemote lookupEventSessionRemote()
//    {
//        try 
//        {
//            Context c = new InitialContext();
//            return (ScmBeanRemote) c.lookup("java:global/IslandSystem/IslandSystem-ejb/ScmBean!session.stateless.ScmBeanRemote");
//        }
//        catch (NamingException ne)
//        {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
//            throw new RuntimeException(ne);
//        }
//    }
//}
