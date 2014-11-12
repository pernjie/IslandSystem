package managedbean;
//COUNTRY INPUT IS NOT WORKING
import entity.Country;
import entity.Customer;
import enumerator.Gender;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.event.RowEditEvent;
import session.stateless.OpCrmBean;
import util.exception.DetailsConflictException;

/**
 *
 * @author jiameiii
 */
@ManagedBean(name = "CustomerBean")
@ViewScoped
public class CustomerBean implements Serializable {

    @EJB
    private OpCrmBean ocb;
    private String unsubscribeEmail;
    private Customer customer;
    private Long custId;
    List<Customer> customers;
    private String address;
    private Date DOB;
    private String password;
    private String confirmPassword;
    private String email;
    private String mobile;
    private String name;
    private Gender gender;
    private Long newCustomerId;
    private Customer newCustomer;
    private String statusMessage;
    private Country country;
    private String city;
    private List<Country> countrys;

    @PostConstruct
    public void init() {
        customers = ocb.getAllCustomers();
        countrys = ocb.getAllCountries();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("countrys", countrys);
    }
    
    public Boolean matchPasswords(){
       return password.equals(confirmPassword);
    }

//    public void saveNewCustomer(ActionEvent event) {
//        try {
//            if (!password.equals(confirmPassword)) {
//                statusMessage = "Passwords do not match";
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please try again: "
//                        + statusMessage, ""));
//            }
//            newCustomer = new Customer();
//            newCustomer.setName(name);
//            newCustomer.setEmail(email);
//            newCustomer.setAddress(address);
//            newCustomer.setGender(gender);
//            newCustomer.setPassword(password);
//            newCustomer.setMobile(mobile);
//            newCustomer.setDob(DOB);
//            newCustomer.setCity(city);
//            newCustomer.setCountry(country);
//            newCustomer.setUnsubscribed(false);
//            
//            newCustomerId = ocb.addNewCustomer(newCustomer);
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Account created successfully", ""));
//        } catch (DetailsConflictException dcx) {
//            statusMessage = dcx.getMessage();
//            newCustomerId = -1L;
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please try again: "
//                    + statusMessage, ""));
//        } catch (Exception ex) {
//            newCustomerId = -1L;
//            statusMessage = "New Customer Failed.";
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please try again: "
//                    + statusMessage, ""));
//            ex.printStackTrace();
//        }
//    }

    public void save(ActionEvent event) throws IOException {
        unsubscribeEmail = getUnsubscribeEmail();
        custId = getCustId();
        ocb.unsubscribe(custId);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You've successfully unregistered!"));
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

    public List<Gender> getGenders() {
        Gender[] genders = new Gender[2];
        genders[0] = Gender.MALE;
        genders[1] = Gender.FEMALE;
        return Arrays.asList(genders);

    }

    public CustomerBean() {
    }

    public String getUnsubscribeEmail() {
        return unsubscribeEmail;
    }

    public void setUnsubscribeEmail(String unsubscribeEmail) {
        this.unsubscribeEmail = unsubscribeEmail;
    }

    public Long getCustId() {
        unsubscribeEmail = getUnsubscribeEmail();
        customer = ocb.getCustomerDetails(unsubscribeEmail);
        custId = customer.getId();
        return custId;
    }

    public void setCustId(long custId) {
        this.custId = custId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDOB() {
        return DOB;
    }

    public void setDOB(Date DOB) {
        this.DOB = DOB;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Long getNewCustomerId() {
        return newCustomerId;
    }

    public void setNewCustomerId(Long newCustomerId) {
        this.newCustomerId = newCustomerId;
    }

    public Customer getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(Customer newCustomer) {
        this.newCustomer = newCustomer;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Country> getCountrys() {
        return countrys;
    }

    public void setCountrys(List<Country> countrys) {
        this.countrys = countrys;
    }
    
    

}
