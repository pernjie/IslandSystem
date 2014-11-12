/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dyihoon90
 */
@Entity
@Table(name = "PAYMENTMETHOD")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PaymentMethod.findAll", query = "SELECT p FROM PaymentMethod p"),
    @NamedQuery(name = "PaymentMethod.findById", query = "SELECT p FROM PaymentMethod p WHERE p.id = :id"),
    @NamedQuery(name = "PaymentMethod.findByCc", query = "SELECT p FROM PaymentMethod p WHERE p.cc = :cc"),
    @NamedQuery(name = "PaymentMethod.findByNameOnCard", query = "SELECT p FROM PaymentMethod p WHERE p.nameOnCard = :nameOnCard"),
    @NamedQuery(name = "PaymentMethod.findByExpiryMonth", query = "SELECT p FROM PaymentMethod p WHERE p.expiryMonth = :expiryMonth"),
    @NamedQuery(name = "PaymentMethod.findByExpiryYear", query = "SELECT p FROM PaymentMethod p WHERE p.expiryYear = :expiryYear"),
    @NamedQuery(name = "PaymentMethod.findByBillingAdd", query = "SELECT p FROM PaymentMethod p WHERE p.billingAdd = :billingAdd"),
    @NamedQuery(name = "PaymentMethod.findByCity", query = "SELECT p FROM PaymentMethod p WHERE p.city = :city"),
    @NamedQuery(name = "PaymentMethod.findByState", query = "SELECT p FROM PaymentMethod p WHERE p.state = :state"),
    @NamedQuery(name = "PaymentMethod.findByPostalCode", query = "SELECT p FROM PaymentMethod p WHERE p.postalCode = :postalCode")})
public class PaymentMethod implements Serializable {
   private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "CC")
    private Integer cc;
    @Size(max = 255)
    @Column(name = "NAME_ON_CARD")
    private String nameOnCard;
    @Column(name = "EXPIRY_MONTH")
    private Integer expiryMonth;
    @Column(name = "EXPIRY_YEAR")
    private Integer expiryYear;
    @Size(max = 255)
    @Column(name = "BILLING_ADD")
    private String billingAdd;
    @Size(max = 255)
    @Column(name = "CITY")
    private String city;
    @Size(max = 255)
    @Column(name = "STATE")
    private String state;
    @Size(max = 64)
    @Column(name = "POSTAL_CODE")
    private String postalCode;
    @JoinColumn(name = "CUSTOMER", referencedColumnName = "ID")
    @ManyToOne
    private Customer customer;
    @JoinColumn(name = "COUNTRY", referencedColumnName = "ID")
    @ManyToOne
    private Country country;

    public PaymentMethod() {
    }

    public PaymentMethod(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCc() {
        return cc;
    }

    public void setCc(Integer cc) {
        this.cc = cc;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public Integer getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(Integer expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public Integer getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(Integer expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getBillingAdd() {
        return billingAdd;
    }

    public void setBillingAdd(String billingAdd) {
        this.billingAdd = billingAdd;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PaymentMethod)) {
            return false;
        }
        PaymentMethod other = (PaymentMethod) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PaymentMethod[ id=" + id + " ]";
    }
    
}
