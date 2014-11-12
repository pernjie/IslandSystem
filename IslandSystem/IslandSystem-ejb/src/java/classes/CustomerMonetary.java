package classes;

import entity.Customer;


public class CustomerMonetary {
    private Customer cust;
    private Double money;
    
    public CustomerMonetary(){
        cust = new Customer();
    }
    
    public CustomerMonetary(Customer custInput, Double moneyInput){
        cust = custInput;
        money = moneyInput;
    }

    public Customer getCust() {
        return cust;
    }

    public void setCust(Customer cust) {
        this.cust = cust;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }
    
    
    
}
