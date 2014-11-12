/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session.stateless;

import entity.Facility;
import entity.Log;
import entity.Staff;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.exception.DetailsConflictException;

/**
 *
 * @author pern
 */
public class CIBeanLocalTest {
    
    CIBeanLocal ciBeanRemote = lookupEventSessionRemote();

    public CIBeanLocalTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception 
    {        
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception 
    {
    }
    
    @Before
    public void setUp() 
    {
    }
    
    @After
    public void tearDown() 
    {
    }
    
    @Test(expected = DetailsConflictException.class)
    public void testStaffPersist() throws DetailsConflictException {
        System.out.println("staffPersist: same email");
        Staff staff = new Staff();
        staff.setEmail("pernjie@gmail.com");
        staff.setPassword("ilikecakes");
        staff.setContact("995");
        ciBeanRemote.createStaff(staff);
    }
    
    @Test
    public void testEncryptPassword1() {
        System.out.println("encryptPassword: normal case");
        assertEquals("73dc32ff9670a32d5ecf30029a9ab06f", ciBeanRemote.encryptPassword("pernjie@gmail.com", "123"));
    }
    
    @Test
    public void testEncryptPassword2() {
        System.out.println("encryptPassword: different casing");
        assertFalse("73dc32ff9670a32d5ecf30029a9ab06f".equals(ciBeanRemote.encryptPassword("PERNJIE@gmail.com", "123")));
    }
    
    @Test
    public void testEncryptPassword3() {
        System.out.println("encryptPassword: different password");
        assertFalse("73dc32ff9670a32d5ecf30029a9ab06f".equals(ciBeanRemote.encryptPassword("pernjie@gmail.com", "1234")));
    }
    
    @Test
    public void testVerifyUser1() {
        System.out.println("verifyUser: normal case");
        assertEquals(1, ciBeanRemote.verifyUser("pernjie@gmail.com", "73dc32ff9670a32d5ecf30029a9ab06f"));
    }
    
    @Test
    public void testVerifyUser2() {
        System.out.println("verifyUser: wrong password");
        assertEquals(-1, ciBeanRemote.verifyUser("pernjie@gmail.com", "wrongpassword"));
    }
    
    @Test
    public void testVerifyUser3() {
        System.out.println("verifyUser: wrong user");
        assertEquals(0, ciBeanRemote.verifyUser("wronguser@gmail.com", "wrongpassword"));
    }
    
    @Test
    public void testGenerateRandomPassword() {
        System.out.println("generateRandomPassword: two not the same");
        assertFalse(ciBeanRemote.generateRandomPassword().equals(ciBeanRemote.generateRandomPassword()));
    }
    
    @Test
    public void testGetAndAddUserLog() {
        System.out.println("getUserLog and addUserLog: normal case");
        Staff staff = new Staff();
        try {
            staff.setEmail("test@if.com");
            staff.setPassword("ilikecakes");
            staff.setContact("995");
            ciBeanRemote.createStaff(staff);
        } catch (DetailsConflictException e) {
            System.out.println("create error");
        }
        ciBeanRemote.addLog(staff, "log1");
        ciBeanRemote.addLog(staff, "log2");
        //get(0)would return the "Account created" log
        assertEquals(ciBeanRemote.getUserLog("test@if.com").get(1).getLogDetails(),"log1");
        assertEquals(ciBeanRemote.getUserLog("test@if.com").get(2).getLogDetails(),"log2");
        ciBeanRemote.remove(staff);
    }
    
    @Test
    public void testGetAllAccounts() {
        System.out.println("getAllAccounts: normal case");
        assertTrue(ciBeanRemote.getAllAcounts().size()>0);
    }
    
    @Test
    public void testGetAllFacilities() {
        System.out.println("getAllFacilities: normal case");
        assertTrue(ciBeanRemote.getAllFacilities().size()>0);
    }
    
    @Test
    public void testGetAllLog() {
        System.out.println("getAllLog: normal case");
        Staff staff = new Staff();
        try {
            staff.setEmail("test@if.com");
            staff.setPassword("ilikecakes");
            staff.setContact("995");
            staff.setFac(ciBeanRemote.getAllFacilities().get(0));
            ciBeanRemote.createStaff(staff);
        } catch (DetailsConflictException e) {
            System.out.println("create error");
        }
        assertTrue(ciBeanRemote.getAllLog(staff.getFac(), staff).size()>0);
        ciBeanRemote.remove(staff);
    }
    
    @Test
    public void testChangePassword() throws DetailsConflictException {
        Staff staff = new Staff();
        try {
            staff.setEmail("test@if.com");
            staff.setPassword("ilikecakes");
            staff.setContact("995");
            ciBeanRemote.createStaff(staff);
        } catch (DetailsConflictException e) {
            System.out.println("create error");
        }
        System.out.println("changePassword: normal case");
        ciBeanRemote.changePassword("ilikecakes","test@if.com", "flowerpower");
        assertEquals(ciBeanRemote.getStaffDetails("test@if.com").getPassword(),ciBeanRemote.encryptPassword("test@if.com","flowerpower"));
        ciBeanRemote.remove(staff);
    }
    
    @Test
    public void testChangeContact() {
        Staff staff = new Staff();
        try {
            staff.setEmail("test@if.com");
            staff.setPassword("ilikecakes");
            staff.setContact("995");
            ciBeanRemote.createStaff(staff);
        } catch (DetailsConflictException e) {
            System.out.println("create error");
        }
        System.out.println("changeContact: normal case");
        ciBeanRemote.changeContact("test@if.com", "999");
        assertEquals(ciBeanRemote.getStaffDetails("test@if.com").getContact(),"999");
        ciBeanRemote.remove(staff);
    }

    private CIBeanLocal lookupEventSessionRemote() 
    {
        try 
        {
            Context c = new InitialContext();
            return (CIBeanLocal) c.lookup("java:global/IslandSystem/IslandSystem-ejb/CIBean!session.stateless.CIBeanLocal");
        }
        catch (NamingException ne)
        {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
