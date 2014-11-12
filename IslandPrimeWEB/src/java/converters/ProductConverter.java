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

 
@FacesConverter("productConverter")
public class ProductConverter implements Converter {
    @Override
    public Product getAsObject(FacesContext fc, UIComponent uic, String value) {
            try {                
                    List<Product> products = (List)fc.getExternalContext().getSessionMap().get("products");
                    Iterator itr = products.iterator();
                    while(itr.hasNext()){
                        Product product = (Product) itr.next();
                        if(product.getId().equals(Long.valueOf(value))){
                            return product;
                        }
                    }
                    return null;
                    
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid product."));
            }
    }
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Product) object).getId());
        }
        else {
            return null;
        }
    }   

}