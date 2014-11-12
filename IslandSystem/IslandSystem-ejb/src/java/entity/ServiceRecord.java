/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import enumerator.SvcRecStatus;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author dyihoon90
 */
@Entity
@Table(name = "SERVICERECORD")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ServiceRecord.findAll", query = "SELECT s FROM ServiceRecord s"),
    @NamedQuery(name = "ServiceRecord.findById", query = "SELECT s FROM ServiceRecord s WHERE s.id = :id"),
    @NamedQuery(name = "ServiceRecord.findByOrderTime", query = "SELECT s FROM ServiceRecord s WHERE s.orderTime = :orderTime"),
    @NamedQuery(name = "ServiceRecord.findByCustName", query = "SELECT s FROM ServiceRecord s WHERE s.custName = :custName"),
    @NamedQuery(name = "ServiceRecord.findByAddress", query = "SELECT s FROM ServiceRecord s WHERE s.address = :address"),
    @NamedQuery(name = "ServiceRecord.findBySvcDate", query = "SELECT s FROM ServiceRecord s WHERE s.svcDate = :svcDate"),
    @NamedQuery(name = "ServiceRecord.findByStatus", query = "SELECT s FROM ServiceRecord s WHERE s.status = :status")})
public class ServiceRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id     
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "ORDER_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderTime;
    @Column(name = "CUST_NAME")
    private String custName;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "SVC_DATE")
    @Temporal(TemporalType.DATE)
    private Date svcDate;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS")
    private SvcRecStatus status;
    @JoinColumn(name = "TRANSACT", referencedColumnName = "ID")
    @ManyToOne
    private TransactionRecord transact;
    @JoinColumn(name = "STORE", referencedColumnName = "ID")
    @ManyToOne
    private Facility store;
    @OneToMany(mappedBy = "svcRec")
    private Collection<ServiceRecordItem> serviceRecordItemCollection;

    public ServiceRecord() {
    }

    public ServiceRecord(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getSvcDate() {
        return svcDate;
    }

    public void setSvcDate(Date svcDate) {
        this.svcDate = svcDate;
    }

    public SvcRecStatus getStatus() {
        return status;
    }

    public void setStatus(SvcRecStatus status) {
        this.status = status;
    }

    public TransactionRecord getTransact() {
        return transact;
    }

    public void setTransact(TransactionRecord transact) {
        this.transact = transact;
    }

    public Facility getStore() {
        return store;
    }

    public void setStore(Facility store) {
        this.store = store;
    }

    @XmlTransient
    public Collection<ServiceRecordItem> getServiceRecorditemCollection() {
        return serviceRecordItemCollection;
    }

    public void setServiceRecorditemCollection(Collection<ServiceRecordItem> serviceRecordItemCollection) {
        this.serviceRecordItemCollection = serviceRecordItemCollection;
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
        if (!(object instanceof ServiceRecord)) {
            return false;
        }
        ServiceRecord other = (ServiceRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ServiceRecord[ id=" + id + " ]";
    }
    
}
