/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converters;

import entity.Product;
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
import managedbean.ProductAutoManagerBean;

/**
 *
 * @author Anna
 */
@FacesConverter("pdtAutoConverter")
public class PdtAutoConverter implements Converter{
    
    public Product getAsObject(FacesContext fc, UIComponent uic, String value) {
    
        System.out.println("VALUE =" + value);
        Long valueLong = Long.parseLong(value);
        if (value != null && value.trim().length() > 0) {
            try {
                 EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
                 EntityManager em = emf.createEntityManager();
                 Query q = em.createNamedQuery("Product.findById");
                 q.setParameter("id", valueLong);
                 Product prod = (Product)q.getSingleResult();
                 return prod;
            } catch (Exception e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid product."));
            }
        } else {
            return null;
        }
    }
 
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Product) object).getId());
        }
        else {
            return null;
        }
    }   
}
