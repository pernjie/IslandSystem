/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author AdminNUS
 */
@Entity
@Table(name = "delivery_schedule")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DeliverySchedule.findAll", query = "SELECT d FROM DeliverySchedule d"),
    @NamedQuery(name = "DeliverySchedule.findByScheduleId", query = "SELECT d FROM DeliverySchedule d WHERE d.scheduleId = :scheduleId"),
    @NamedQuery(name = "DeliverySchedule.findByMfgId", query = "SELECT d FROM DeliverySchedule d WHERE d.mf = :mf"),
    @NamedQuery(name = "DeliverySchedule.findByMatId", query = "SELECT d FROM DeliverySchedule d WHERE d.mat = :mat"),
    @NamedQuery(name = "DeliverySchedule.findByQuantity", query = "SELECT d FROM DeliverySchedule d WHERE d.quantity = :quantity"),
    @NamedQuery(name = "DeliverySchedule.findByStoreId", query = "SELECT d FROM DeliverySchedule d WHERE d.store = :store"),
    @NamedQuery(name = "DeliverySchedule.findByDeliveryDate", query = "SELECT d FROM DeliverySchedule d WHERE d.deliveryDate = :deliveryDate")})
public class DeliverySchedule implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;
    @JoinColumn(name = "mfg_id")
    @ManyToOne
    private Facility mf;
    @JoinColumn(name = "mat_id")
    @ManyToOne
    private Item mat;
    @Column(name = "quantity")
    private Integer quantity;
    @JoinColumn(name = "store_id")
    @ManyToOne
    private Facility store;
    @Column(name = "delivery_date")
    @Temporal(TemporalType.DATE)
    private Date deliveryDate;
    private String status;

    public DeliverySchedule() {
    }

    public DeliverySchedule(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Facility getMf() {
        return mf;
    }

    public void setMf(Facility mf) {
        this.mf = mf;
    }

    public Item getMat() {
        return mat;
    }

    public void setMat(Item mat) {
        this.mat = mat;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Facility getStore() {
        return store;
    }

    public void setStore(Facility store) {
        this.store = store;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (scheduleId != null ? scheduleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DeliverySchedule)) {
            return false;
        }
        DeliverySchedule other = (DeliverySchedule) object;
        if ((this.scheduleId == null && other.scheduleId != null) || (this.scheduleId != null && !this.scheduleId.equals(other.scheduleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.DeliverySchedule[ scheduleId=" + scheduleId + " ]";
    }
    
}
