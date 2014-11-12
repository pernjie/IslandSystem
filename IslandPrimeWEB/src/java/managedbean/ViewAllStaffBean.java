package managedbean;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */ 







import session.stateless.CIBeanLocal;
import entity.Staff;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

/**
 *
 * @author nataliegoh
 */
@Named(value = "viewAllStaffManagedBean")
@Dependent  
public class ViewAllStaffBean implements Serializable {

    /**
     * Creates a new instance of viewAllStaffManagedBean
     */
    public ViewAllStaffBean() {
    }
    
    @EJB
    private CIBeanLocal fmsbean;
    private List<Staff> staff;
    private Staff selectedStaff;

    public List<Staff> getStaff() {
        staff = fmsbean.getAllAcounts();
        return staff;
    }

    public void setStaff(List<Staff> staff) {
        this.staff = staff;
    }

    public Staff getSelectedStaff() {
        return selectedStaff;
    }

    public void setSelectedStaff(Staff selectedStaff) {
        this.selectedStaff = selectedStaff;
    }
    
    public void deleteStaff() {
        fmsbean.remove(selectedStaff);
        selectedStaff = null;
    }
    
}
