package converters;
 
import entity.Region;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

 
@FacesConverter("regionConverter")
public class RegionConverter implements Converter {
    @Override
    public Region getAsObject(FacesContext fc, UIComponent uic, String value) {
            try {                
                    List<Region> regions = (List)fc.getExternalContext().getSessionMap().get("regions");
                    Iterator itr = regions.iterator();
                    while(itr.hasNext()){
                        Region region = (Region) itr.next();
                        if(region.getId().equals(Long.valueOf(value))){
                            return region;
                        }
                    }
                    return null;
                    
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid region."));
            }
    }
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Region) object).getId());
        }
        else {
            return null;
        }
    }   

}