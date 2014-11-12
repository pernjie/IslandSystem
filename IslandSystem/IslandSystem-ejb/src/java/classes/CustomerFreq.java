package classes;

import entity.Customer;


public class CustomerFreq {
    private Customer cust;
    private Integer freq;
    
    public CustomerFreq(){
        cust = new Customer();
    }
    
    public CustomerFreq(Customer custInput, Integer freqInput){
        cust = custInput;
        freq = freqInput;
    }

    public Customer getCust() {
        return cust;
    }

    public void setCust(Customer cust) {
        this.cust = cust;
    }

    public Integer getFreq() {
        return freq;
    }

    public void setFreq(Integer freq) {
        this.freq = freq;
    }
    
    
    
}
