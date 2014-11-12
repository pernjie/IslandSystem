/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import enumerator.CustomerCampaignCat;
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
@Table(name = "CUSTOMERCAMPAIGNMETRIC")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CustomerCampaignMetric.findAll", query = "SELECT c FROM CustomerCampaignMetric c"),
    @NamedQuery(name = "CustomerCampaignMetric.findById", query = "SELECT c FROM CustomerCampaignMetric c WHERE c.id = :id")})
public class CustomerCampaignMetric implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NUM_HITS")
    private Integer numHits;
    @Column(name = "NUM_PROMO_CODE_USED")
    private Integer numPromoCodeUsed;
    @Column(name = "CUSTOMER_CAT")
    private CustomerCampaignCat customerCampaignCat;
    @JoinColumn(name = "CUSTOMER", referencedColumnName = "ID")
    @ManyToOne
    private Customer customer;
    @JoinColumn(name = "CAMPAIGN", referencedColumnName = "ID")
    @ManyToOne
    private Campaign campaign;

    public CustomerCampaignMetric() {
    }

    public CustomerCampaignMetric(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumHits() {
        return numHits;
    }

    public void setNumHits(Integer numHits) {
        this.numHits = numHits;
    }

    public Integer getNumPromoCodeUsed() {
        return numPromoCodeUsed;
    }

    public void setNumPromoCodeUsed(Integer numPromoCodeUsed) {
        this.numPromoCodeUsed = numPromoCodeUsed;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
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

    public CustomerCampaignCat getCustomerCampaignCat() {
        return customerCampaignCat;
    }

    public void setCustomerCampaignCat(CustomerCampaignCat customerCampaignCat) {
        this.customerCampaignCat = customerCampaignCat;
    }


    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CustomerCampaignMetric)) {
            return false;
        }
        CustomerCampaignMetric other = (CustomerCampaignMetric) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CustomerCampaignMetric[ id=" + id + " ]";
    }

}
