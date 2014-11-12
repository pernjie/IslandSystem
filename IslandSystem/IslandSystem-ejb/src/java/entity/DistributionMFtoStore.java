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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dyihoon90
 */
@Entity
@Table(name = "DISTRIBUTIONMFTOSTORE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DistributionMFtoStore.findAll", query = "SELECT m FROM DistributionMFtoStore m"),
    @NamedQuery(name = "DistributionMFtoStore.findById", query = "SELECT m FROM DistributionMFtoStore m WHERE m.id = :id")})
public class DistributionMFtoStore implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @JoinColumn(name = "MAT", referencedColumnName = "ID")
    @ManyToOne
    private Material mat;
    @JoinColumn(name = "MF", referencedColumnName = "ID")
    @ManyToOne
    private Facility mf;
    @JoinColumn(name = "STORE", referencedColumnName = "ID")
    @ManyToOne
    private Facility store;

    public DistributionMFtoStore() {
    }

    public DistributionMFtoStore(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Material getMat() {
        return mat;
    }

    public void setMat(Material mat) {
        this.mat = mat;
    }

    public Facility getMf() {
        return mf;
    }

    public void setMf(Facility mf) {
        this.mf = mf;
    }

    public Facility getStore() {
        return store;
    }

    public void setStore(Facility store) {
        this.store = store;
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
        if (!(object instanceof DistributionMFtoStore)) {
            return false;
        }
        DistributionMFtoStore other = (DistributionMFtoStore) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.DistributionMFtoStore[ id=" + id + " ]";
    }
    
}
