/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package session.stateless;

import entity.Customer;
import entity.Facility;
import entity.Item;
import entity.Region;
import entity.Service;
import entity.TransactionItem;
import entity.TransactionRecord;
import entity.TransactionService;
import java.util.ArrayList;
import java.util.List;
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
public class PosBeanRemoteTest {
    
    PosBeanRemote posBeanRemote = lookupEventSessionRemote();
    
    public PosBeanRemoteTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Test
    public void testAddTransactionRecord() {
        System.out.println("addTransactionRecord : normal case");
        assertTrue(posBeanRemote.addTransactionRecord(posBeanRemote.getStore("pernjie@gmail.com"), new ArrayList<TransactionItem>(), new ArrayList<TransactionService>(), null, null, 1.0, null, 0, null, false)!=null);
    }
    
    @Test
    public void testGetCustomer() {
        System.out.println("getCustomer: normal case");
        assertTrue(posBeanRemote.getCustomer("62E9AA5D")!=null);
    }
    
    @Test
    public void testGetCustomer2() {
        System.out.println("getCustomer: invalid card ID");
        assertTrue(posBeanRemote.getCustomer("62E9AA5DD")==null);
    }
    
    @Test
    public void testGetItem() {
        System.out.println("getItem: normal case");
        assertTrue(posBeanRemote.getItem("1", "Furniture")!=null);
    }
    
    @Test
    public void testGetItem2() {
        System.out.println("getItem: correct item, wrong location (furniture ID in kitchen POS)");
        assertTrue(posBeanRemote.getItem("1", "Kitchen")==null);
    }
    
    @Test
    public void testGetItem3() {
        System.out.println("getItem: invalid item ID");
        assertTrue(posBeanRemote.getItem("12345", "Furniture")==null);
    }
    
    @Test
    public void testGetItemPrice() {
        System.out.println("getItemPrice: invalid item ID");
        assertTrue(posBeanRemote.getItemPrice(posBeanRemote.getItem("1", "Furniture"), posBeanRemote.getRegion(posBeanRemote.getStore("pernjie@gmail.com")))>0.0);
    }
    
    @Test
    public void testRegion() {
        System.out.println("getRegion: normal case");
        assertTrue(posBeanRemote.getRegion(posBeanRemote.getStore("pernjie@gmail.com"))!=null);
    }
    
    @Test
    public void testGetService() {
        System.out.println("getService: normal case");
        assertTrue(posBeanRemote.getService("1")!=null);
    }
    
    @Test
    public void testGetService2() {
        System.out.println("getService: no service found");
        assertTrue(posBeanRemote.getService("1234")==null);
    }
    
    @Test
    public void testGetServicePrice() {
        System.out.println("getServicePrice: normal case");
        assertTrue(posBeanRemote.getServicePrice(posBeanRemote.getService("1"), posBeanRemote.getRegion(posBeanRemote.getStore("pernjie@gmail.com")))>0.0);
    }
    
    @Test
    public void testGetStore() {
        System.out.println("getStore: normal case");
        assertTrue(posBeanRemote.getStore("pernjie@gmail.com")!=null);
    }
    
    @Test
    public void testVerifyPromo() {
        System.out.println("verifyPromo: normal case");
        assertTrue(posBeanRemote.verifyPromo("SG50", posBeanRemote.getRegion(posBeanRemote.getStore("pernjie@gmail.com")), "Furniture"));
    }
    
    @Test
    public void testVerifyPromo2() {
        System.out.println("verifyPromo: invalid region");
        assertFalse(posBeanRemote.verifyPromo("SG50", posBeanRemote.getRegion(posBeanRemote.getStore("tecksoon@if.com")), "Furniture"));
    }
    
    @Test
    public void testVerifyPromo3() {
        System.out.println("verifyPromo: invalid type (furniture discount code for kitchen)");
        assertFalse(posBeanRemote.verifyPromo("SG50", posBeanRemote.getRegion(posBeanRemote.getStore("pernjie@gmail.com")), "Kitchen"));
    }
    
    @Test
    public void testVerifyPromo4() {
        System.out.println("verifyPromo: invalid promo code");
        assertFalse(posBeanRemote.verifyPromo("SG51", posBeanRemote.getRegion(posBeanRemote.getStore("pernjie@gmail.com")), "Furniture"));
    }
    
    private PosBeanRemote lookupEventSessionRemote()
    {
        try 
        {
            Context c = new InitialContext();
            return (PosBeanRemote) c.lookup("java:global/IslandSystem/IslandSystem-ejb/PosBean!session.stateless.PosBeanRemote");
        }
        catch (NamingException ne)
        {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
}
