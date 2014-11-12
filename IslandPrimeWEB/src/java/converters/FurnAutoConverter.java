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

 
@FacesConverter("furnAutoConverter")
public class FurnAutoConverter implements Converter {
    @Override
    public Material getAsObject(FacesContext fc, UIComponent uic, String value) {
            try {                
                    List<Material> materials = (List)fc.getExternalContext().getSessionMap().get("materials");
                    Iterator itr = materials.iterator();
                    while(itr.hasNext()){
                        Material material = (Material) itr.next();
                        if(material.getId().equals(Long.valueOf(value))){
                            return material;
                        }
                    }
                    return null;
                    
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid furniture."));
            }
    }
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Material) object).getId());
        }
        else {
            return null;
        }
    }   

}