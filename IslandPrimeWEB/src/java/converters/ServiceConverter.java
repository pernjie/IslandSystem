package converters;
 
import entity.Service;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

 
@FacesConverter("serviceConverter")
public class ServiceConverter implements Converter {
    @Override
    public Service getAsObject(FacesContext fc, UIComponent uic, String value) {
            try {                
                    List<Service> services = (List)fc.getExternalContext().getSessionMap().get("services");
                    Iterator itr = services.iterator();
                    while(itr.hasNext()){
                        Service service = (Service) itr.next();
                        if(service.getId().equals(Long.valueOf(value))){
                            return service;
                        }
                    }
                    return null;
                    
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid service."));
            }
    }
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Service) object).getId());
        }
        else {
            return null;
        }
    }   

}