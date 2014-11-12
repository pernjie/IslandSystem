/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converters;

import entity.Material;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import managedbean.RawMatAutoManagerBean;

/**
 *
 * @author Anna
 */
@FacesConverter("rawMatAutoConverter")
public class RawMatAutoConverter implements Converter{
    
    public Material getAsObject(FacesContext fc, UIComponent uic, String value) {
    
        System.out.println("VALUE =" + value);
        Long valueLong = Long.parseLong(value);
        if (value != null && value.trim().length() > 0) {
            try {
                 EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
                 EntityManager em = emf.createEntityManager();
                 Query q = em.createNamedQuery("Material.findById");
                 q.setParameter("id", valueLong);
                 Material mat = (Material)q.getSingleResult();
                 return mat;
            } catch (Exception e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid material."));
            }
        } else {
            return null;
        }
    }
 
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Material) object).getId());
        }
        else {
            return null;
        }
    }   
}
