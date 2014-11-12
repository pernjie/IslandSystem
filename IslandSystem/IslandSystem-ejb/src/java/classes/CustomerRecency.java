package classes;

import entity.Customer;


public class CustomerRecency {
    private Customer cust;
    private Long transactId;
    
    public CustomerRecency(){
        cust = new Customer();
    }
    
    public CustomerRecency(Customer custInput, Long transactIdInput){
        cust = custInput;
        transactId = transactIdInput;
    }

    public Customer getCust() {
        return cust;
    }

    public void setCust(Customer cust) {
        this.cust = cust;
    }

    public Long getTransactId() {
        return transactId;
    }

    public void setTransactId(Long transactId) {
        this.transactId = transactId;
    }
    
    
    
}
