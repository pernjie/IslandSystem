/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Basic;
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dyihoon90
 */
@Entity
@Table(name = "SERVICERECORDITEM")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ServiceRecordItem.findAll", query = "SELECT s FROM ServiceRecordItem s"),
    @NamedQuery(name = "ServiceRecordItem.findById", query = "SELECT s FROM ServiceRecordItem s WHERE s.id = :id"),
    @NamedQuery(name = "ServiceRecordItem.findByQuantity", query = "SELECT s FROM ServiceRecordItem s WHERE s.quantity = :quantity")})
public class ServiceRecordItem implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id     
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @JoinColumn(name = "SVC_REC", referencedColumnName = "ID")
    @ManyToOne
    private ServiceRecord svcRec;
    @JoinColumn(name = "SVC", referencedColumnName = "ID")
    @ManyToOne
    private Service svc;
    @JoinColumn(name = "MAT", referencedColumnName = "ID")
    @ManyToOne
    private Material mat;

    public ServiceRecordItem() {
    }

    public ServiceRecordItem(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ServiceRecord getSvcRec() {
        return svcRec;
    }

    public void setSvcRec(ServiceRecord svcRec) {
        this.svcRec = svcRec;
    }

    public Service getSvc() {
        return svc;
    }

    public void setSvc(Service svc) {
        this.svc = svc;
    }

    public Material getMat() {
        return mat;
    }

    public void setMat(Material mat) {
        this.mat = mat;
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
        if (!(object instanceof ServiceRecordItem)) {
            return false;
        }
        ServiceRecordItem other = (ServiceRecordItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ServiceRecordItem[ id=" + id + " ]";
    }
    
}
