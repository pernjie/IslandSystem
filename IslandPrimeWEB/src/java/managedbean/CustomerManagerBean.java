/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managedbean;

import entity.Customer;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.RowEditEvent;
import session.stateless.OpCrmBean;


@ManagedBean(name = "customerManagerBean")
@ViewScoped
public class CustomerManagerBean {
    @EJB
    OpCrmBean cb;
    List<Customer> customers;
    
    public CustomerManagerBean() {
    }
    
    @PostConstruct
    public void init() {
        customers = cb.getAllCustomers();
    }

    public List<Customer> getCustomers() {
        return customers;
    }
    
    public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Customer Edited", ((Customer) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
     
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Customer) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
