/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import enumerator.TenderType;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author dyihoon90
 */
@Entity
@Table(name = "TRANSACTIONRECORD")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionRecord.findAll", query = "SELECT t FROM TransactionRecord t"),
    @NamedQuery(name = "TransactionRecord.findById", query = "SELECT t FROM TransactionRecord t WHERE t.id = :id"),
    @NamedQuery(name = "TransactionRecord.findByTransactTime", query = "SELECT t FROM TransactionRecord t WHERE t.transactTime = :transactTime"),
    @NamedQuery(name = "TransactionRecord.findByCollected", query = "SELECT t FROM TransactionRecord t WHERE t.collected = :collected"),
    @NamedQuery(name = "TransactionRecord.findByRedemption", query = "SELECT t FROM TransactionRecord t WHERE t.redemption = :redemption")})
public class TransactionRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "TRANSACT_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactTime;
    @Column(name = "COLLECTED")
    private Boolean collected;
    @Column(name = "REDEMPTION")
    private Boolean redemption;
    @Column(name = "GROSS_TOTAL_AMOUNT")
    private Double grossTotalAmount;
    @Column(name = "TAX_AMOUNT")
    private Double taxAmount;
    @Column(name = "DISCOUNT_AMOUNT")
    private Double discountAmount;
    @Column(name = "RETURN_AMOUNT")
    private Double returnAmount;
    @Column(name = "NET_TOTAL_AMOUNT")
    private Double netTotalAmount;
    @Column(name = "TENDER_TYPE")
    private TenderType tenderType;
    @JoinColumn(name = "CAMPAIGN", referencedColumnName = "ID")
    private Campaign campaign;
    @JoinColumn(name = "FACILITY", referencedColumnName = "ID")
    @ManyToOne
    private Facility fac;
    @JoinColumn(name = "CUSTOMER", referencedColumnName = "ID")
    @ManyToOne
    private Customer cust;

    public TransactionRecord() {
    }

    public TransactionRecord(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTransactTime() {
        return transactTime;
    }

    public void setTransactTime(Date transactTime) {
        this.transactTime = transactTime;
    }

    public Boolean getCollected() {
        return collected;
    }

    public void setCollected(Boolean collected) {
        this.collected = collected;
    }

    public Boolean getRedemption() {
        return redemption;
    }

    public void setRedemption(Boolean redemption) {
        this.redemption = redemption;
    }

    public Double getGrossTotalAmount() {
        return grossTotalAmount;
    }

    public void setGrossTotalAmount(Double grossTotalAmount) {
        this.grossTotalAmount = grossTotalAmount;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getNetTotalAmount() {
        return netTotalAmount;
    }

    public void setNetTotalAmount(Double netTotalAmount) {
        this.netTotalAmount = netTotalAmount;
    }

    public Facility getFac() {
        return fac;
    }

    public void setFac(Facility fac) {
        this.fac = fac;
    }

    public Customer getCust() {
        return cust;
    }

    public void setCust(Customer cust) {
        this.cust = cust;
    }

    public TenderType getTenderType() {
        return tenderType;
    }

    public void setTenderType(TenderType tenderType) {
        this.tenderType = tenderType;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public Double getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(Double returnAmount) {
        this.returnAmount = returnAmount;
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
        if (!(object instanceof TransactionRecord)) {
            return false;
        }
        TransactionRecord other = (TransactionRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.TransactionRecord[ id=" + id + " ]";
    }
}
