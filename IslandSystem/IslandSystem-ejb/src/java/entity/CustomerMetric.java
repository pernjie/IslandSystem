/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import enumerator.BusinessArea;
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

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dyihoon90
 */
@Entity
@Table(name = "CUSTOMERMETRIC")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CustomerMetric.findAll", query = "SELECT c FROM CustomerMetric c"),
    @NamedQuery(name = "CustomerMetric.findById", query = "SELECT c FROM CustomerMetric c WHERE c.id = :id")})
public class CustomerMetric implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "CURRENT_RFM")
    private Integer currentRfm;
    @Column(name = "HIGHEST_RFM")
    private Integer highestRfm;
    @Column(name = "INACTIVE")
    private Boolean inactive;
    @Column(name = "BUSINESS_AREA")
    private BusinessArea businessArea;
    @JoinColumn(name = "CUSTOMER", referencedColumnName = "ID")
    @ManyToOne
    private Customer customer;

    public CustomerMetric() {
    }

    public CustomerMetric(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCurrentRfm() {
        return currentRfm;
    }

    public void setCurrentRfm(Integer currentRfm) {
        this.currentRfm = currentRfm;
    }

    public Integer getHighestRfm() {
        return highestRfm;
    }

    public void setHighestRfm(Integer highestRfm) {
        this.highestRfm = highestRfm;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public BusinessArea getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(BusinessArea businessArea) {
        this.businessArea = businessArea;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
        if (!(object instanceof CustomerMetric)) {
            return false;
        }
        CustomerMetric other = (CustomerMetric) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CustomerMetric[ id=" + id + " ]";
    }

}
