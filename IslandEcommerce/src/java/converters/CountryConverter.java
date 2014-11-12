package converters;
 
import entity.Country;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

 
@FacesConverter("countryConverter")
public class CountryConverter implements Converter {
    @Override
    public Country getAsObject(FacesContext fc, UIComponent uic, String value) {
            try {                
                    List<Country> countrys = (List)fc.getExternalContext().getSessionMap().get("countries");
                    Iterator itr = countrys.iterator();
                    while(itr.hasNext()){
                        Country country = (Country) itr.next();
                        if(country.getId().equals(Integer.valueOf(value))){
                            return country;
                        }
                    }
                    return null;
                    
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid country."));
            }
    }
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Country) object).getId());
        }
        else {
            return null;
        }
    }   

}