package converters;
 
import entity.Facility;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

 
@FacesConverter("facilityConverter")
public class FacilityConverter implements Converter {
    @Override
    public Facility getAsObject(FacesContext fc, UIComponent uic, String value) {
            try {                
                    List<Facility> facilities = (List)fc.getExternalContext().getSessionMap().get("facilities");
                    Iterator itr = facilities.iterator();
                    while(itr.hasNext()){
                        Facility facility = (Facility) itr.next();
                        if(facility.getId().equals(Long.valueOf(value))){
                            return facility;
                        }
                    }
                    return null;
                    
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid facility."));
            }
    }
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Facility) object).getId());
        }
        else {
            return null;
        }
    }   

}