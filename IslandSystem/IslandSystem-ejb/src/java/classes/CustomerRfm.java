package classes;

import entity.Customer;


public class CustomerRfm {
    private Customer cust;
    private Integer rfmScore;
    
    public CustomerRfm(){
        cust = new Customer();
    }
    
    public CustomerRfm(Customer custInput, Integer rfmScoreInput){
        cust = custInput;
        rfmScore = rfmScoreInput;
    }

    public Customer getCust() {
        return cust;
    }

    public void setCust(Customer cust) {
        this.cust = cust;
    }

    public Integer getRfmScore() {
        return rfmScore;
    }

    public void setRfmScore(Integer rfmScore) {
        this.rfmScore = rfmScore;
    }
    
    
    
}
