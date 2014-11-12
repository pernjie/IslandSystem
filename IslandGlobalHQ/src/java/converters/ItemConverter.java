package converters;
 
import entity.Item;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

 
@FacesConverter("itemConverter")
public class ItemConverter implements Converter {
    @Override
    public Item getAsObject(FacesContext fc, UIComponent uic, String value) {
            try {                
                    List<Item> items = (List)fc.getExternalContext().getSessionMap().get("items");
                    Iterator itr = items.iterator();
                    while(itr.hasNext()){
                        Item item = (Item) itr.next();
                        if(item.getId().equals(Long.valueOf(value))){
                            return item;
                        }
                    }
                    return null;
                    
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid item."));
            }
    }
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Item) object).getId());
        }
        else {
            return null;
        }
    }   

}