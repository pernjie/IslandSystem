package managedbean;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import entity.Facility;
import entity.Log;
import entity.Staff;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import session.stateless.CIBeanLocal;

/**
 *
 * @author nataliegoh
 */
@Named(value = "viewLogManagedBean")
@Dependent
public class ViewAllLogBean {
    
    @EJB
    private CIBeanLocal fmsbean;
    private List<Log> allLog;
    private String loggedInEmail;
    private Staff staff;
    private Facility fac;

    /**
     * Creates a new instance of viewLogManagedBean
     */
    public ViewAllLogBean() {
    }
    
    public List<Log> getAllLog() {
        allLog = fmsbean.getAllLog(getFac(), getStaff());
        return allLog;
    }

    public void setAllLog(List<Log> allLog) {
        this.allLog = allLog;
    }

    public Staff getStaff() {
        return (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
    }

    public Facility getFac() {
        return (Facility) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("facility");
    }
    
}
