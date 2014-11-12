package converters;
 
import entity.Campaign;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

 
@FacesConverter("campaignConverter")
public class CampaignConverter implements Converter {
    @Override
    public Campaign getAsObject(FacesContext fc, UIComponent uic, String value) {
            try {                
                    List<Campaign> campaigns = (List)fc.getExternalContext().getSessionMap().get("campaigns");
                    Iterator itr = campaigns.iterator();
                    while(itr.hasNext()){
                        Campaign campaign = (Campaign) itr.next();
                        if(campaign.getId().equals(Long.valueOf(value))){
                            return campaign;
                        }
                    }
                    return null;
                    
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid campaign."));
            }
    }
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Campaign) object).getId());
        }
        else {
            return null;
        }
    }   

}