    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import enumerator.BusinessArea;
import enumerator.Gender;
import java.io.Serializable;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nataliegoh
 */
@Entity
@Table(name = "campaign")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Campaign.findAll", query = "SELECT c FROM Campaign c"),
    @NamedQuery(name = "Campaign.findById", query = "SELECT c FROM Campaign c WHERE c.id = :id"),
    @NamedQuery(name = "Campaign.findByStartDate", query = "SELECT c FROM Campaign c WHERE c.startDate = :startDate"),
    @NamedQuery(name = "Campaign.findByEndDate", query = "SELECT c FROM Campaign c WHERE c.endDate = :endDate"),
    @NamedQuery(name = "Campaign.findByPromocode", query = "SELECT c FROM Campaign c WHERE c.promoCode = :promoCode"),
    @NamedQuery(name = "Campaign.findByPercentDisc", query = "SELECT c FROM Campaign c WHERE c.percentDisc = :percentDisc"),
    @NamedQuery(name = "Campaign.findByTargetGender", query = "SELECT c FROM Campaign c WHERE c.targetGender = :targetGender"),
    @NamedQuery(name = "Campaign.findByTargetActive", query = "SELECT c FROM Campaign c WHERE c.targetActive = :targetActive"),
    @NamedQuery(name = "Campaign.findByTargetInactive", query = "SELECT c FROM Campaign c WHERE c.targetInactive = :targetInactive"),
    @NamedQuery(name = "Campaign.findByTargetNew", query = "SELECT c FROM Campaign c WHERE c.targetNew = :targetNew"),
    @NamedQuery(name = "Campaign.findByBusinessArea", query = "SELECT c FROM Campaign c WHERE c.businessArea = :businessArea"),
    @NamedQuery(name = "Campaign.findByRfmThreshold", query = "SELECT c FROM Campaign c WHERE c.rfmThreshold = :rfmThreshold"),
    @NamedQuery(name = "Campaign.findByHighestRfmThreshold", query = "SELECT c FROM Campaign c WHERE c.highestRfmThreshold = :highestRfmThreshold"),
    @NamedQuery(name = "Campaign.findByRegion", query = "SELECT c FROM Campaign c WHERE c.region = :region")})
public class Campaign implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "startDate")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Column(name = "endDate")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Column(name = "promoCode")
    private String promoCode;
    @Column(name = "percentDisc")
    private Double percentDisc;
    @Column(name = "lowerAge")
    private Integer lowerAge;
    @Column(name = "upperAge")
    private Integer upperAge;
    @Column(name = "targetGender")
    private Gender targetGender;
    @Column(name = "targetActive")
    private Boolean targetActive;
    @Column(name = "targetInactive")
    private Boolean targetInactive;
    @Column(name = "targetNew")
    private Boolean targetNew;
    @Column(name = "businessArea")
    private BusinessArea businessArea;
    @Column(name = "rfmThreshold")
    private Integer rfmThreshold;
    @Column(name = "highestRfmThreshold")
    private Integer highestRfmThreshold;
    @JoinColumn(name = "Region", referencedColumnName = "ID")
    @ManyToOne
    private Region region;

    public Campaign() {
    }

    public Campaign(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }


    public Double getPercentDisc() {
        return percentDisc;
    }

    public void setPercentDisc(Double percentDisc) {
        this.percentDisc = percentDisc;
    }



    public Boolean getTargetActive() {
        return targetActive;
    }

    public void setTargetActive(Boolean targetActive) {
        this.targetActive = targetActive;
    }

    public Boolean getTargetInactive() {
        return targetInactive;
    }

    public void setTargetInactive(Boolean targetInactive) {
        this.targetInactive = targetInactive;
    }

    public Boolean getTargetNew() {
        return targetNew;
    }

    public void setTargetNew(Boolean targetNew) {
        this.targetNew = targetNew;
    }

    public Integer getRfmThreshold() {
        return rfmThreshold;
    }

    public void setRfmThreshold(Integer rfmThreshold) {
        this.rfmThreshold = rfmThreshold;
    }

    public Integer getHighestRfmThreshold() {
        return highestRfmThreshold;
    }

    public void setHighestRfmThreshold(Integer highestRfmThreshold) {
        this.highestRfmThreshold = highestRfmThreshold;
    }
    
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Integer getLowerAge() {
        return lowerAge;
    }

    public void setLowerAge(Integer lowerAge) {
        this.lowerAge = lowerAge;
    }

    public Integer getUpperAge() {
        return upperAge;
    }

    public void setUpperAge(Integer upperAge) {
        this.upperAge = upperAge;
    }

    public Gender getTargetGender() {
        return targetGender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTargetGender(Gender targetGender) {
        this.targetGender = targetGender;
    }

    public BusinessArea getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(BusinessArea businessArea) {
        this.businessArea = businessArea;
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
        if (!(object instanceof Campaign)) {
            return false;
        }
        Campaign other = (Campaign) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Campaign[ id=" + id + " ]";
    }
    
}
