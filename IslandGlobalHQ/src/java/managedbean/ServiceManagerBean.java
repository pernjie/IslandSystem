/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Service;
import enumerator.SvcCat;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.event.RowEditEvent;
import services.ServiceService;
import session.stateless.GlobalHqBean;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "serviceManagerBean")
@ViewScoped
public class ServiceManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private ServiceService serviceService;
    private Long id;
    private String name;
    private String description;
    private SvcCat category;
    private Long newServiceId;
    private String statusMessage;
    private Service selectedService;
    private List<Service> filteredServices;
    private List<Service> services;
    private Service newService = new Service();

    @PostConstruct

    public void init() {
        services = globalHqBean.getAllServices();
        serviceService = new ServiceService();
    }

    public void saveNewService(ActionEvent event) {
        try {
            newService = new Service();
            newService.setName(name);
            newService.setCategory(category);
            newService.setDescription(description);
            newServiceId = globalHqBean.addNewService(newService);
            statusMessage = "New Service Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Service Result: "
                    + statusMessage + " (New Service ID is " + newServiceId + ")", ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newServiceId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New Service Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newServiceId = -1L;
            statusMessage = "New Service Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Service Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteService() {
        try {
            globalHqBean.removeService(selectedService);
            services = globalHqBean.getAllServices();
            FacesMessage msg = new FacesMessage("Service Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            services = globalHqBean.getAllServices();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateService((Service) event.getObject());
            services = globalHqBean.getAllServices();
            FacesMessage msg = new FacesMessage("Service Edited", ((Service) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            services = globalHqBean.getAllServices();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Service) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public ServiceService getServiceService() {
        return serviceService;
    }

    public Service getNewService() {
        return newService;
    }

    public void setNewService(Service newService) {
        this.newService = newService;
    }

    public List<SvcCat> getCategories() {
        return serviceService.getCategories();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Service> getFilteredServices() {
        return filteredServices;
    }

    public void setFilteredServices(List<Service> filteredServices) {
        this.filteredServices = filteredServices;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public GlobalHqBean getGlobalHqBean() {
        return globalHqBean;
    }

    public void setGlobalHqBean(GlobalHqBean globalHqBean) {
        this.globalHqBean = globalHqBean;
    }

    public List<Service> getServices() {
        return services;
    }

    public Service getSelectedService() {
        return selectedService;
    }

    public void setSelectedService(Service selectedService) {
        this.selectedService = selectedService;
    }

    /**
     * Creates a new instance of ServiceManagerBean
     */
    public ServiceManagerBean() {

    }

    public Long getNewServiceId() {
        return newServiceId;
    }

    public void setNewServiceId(Long newServiceId) {
        this.newServiceId = newServiceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SvcCat getCategory() {
        return category;
    }

    public void setCategory(SvcCat category) {
        this.category = category;
    }

}
