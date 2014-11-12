package converters;
 
import entity.Supplier;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

 
@FacesConverter("supplierConverter")
public class SupplierConverter implements Converter {
    @Override
    public Supplier getAsObject(FacesContext fc, UIComponent uic, String value) {
            try {                
                    List<Supplier> suppliers = (List)fc.getExternalContext().getSessionMap().get("suppliers");
                    Iterator itr = suppliers.iterator();
                    while(itr.hasNext()){
                        Supplier supplier = (Supplier) itr.next();
                        if(supplier.getId().equals(Long.valueOf(value))){
                            return supplier;
                        }
                    }
                    return null;
                    
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid supplier."));
            }
    }
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Supplier) object).getId());
        }
        else {
            return null;
        }
    }   

}