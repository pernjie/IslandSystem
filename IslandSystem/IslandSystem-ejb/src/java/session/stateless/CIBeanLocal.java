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
import javax.ejb.Local;
import javax.ejb.Remote;
import util.exception.DetailsConflictException;

/**
 *
 * @author nataliegoh
 */
@Remote
public interface CIBeanLocal {
    
    public int verifyUser(String email, String password);
    
    public Staff getStaffDetails(String email);
    
    public String encryptPassword(String email, String password);

    public boolean staffExists(String email);

    public List<Staff> getAllAcounts();

    public String generateRandomPassword();

    public List<Log> getUserLog(String email);

    public void changeContact(String email, String contact);

    List<Facility> getAllFacilities();

    Staff getStaffByEmail(String email);

    public String announcementDate();

    public String announcementDetails();

    public void createStaff(Staff newStaff) throws DetailsConflictException;

    public void addLog(Staff staff, String L);

    public void changePassword(String email, String currpassword, String newpassword) throws DetailsConflictException;
    
    public void remove(Staff staff);
    
    public List<Log> getAllLog(Facility fac, Staff staff);
}
