package converters;
 
import entity.Ingredient;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

 
@FacesConverter("ingredientConverter")
public class IngredientConverter implements Converter {
    @Override
    public Ingredient getAsObject(FacesContext fc, UIComponent uic, String value) {
            try {                
                    List<Ingredient> ingredients = (List)fc.getExternalContext().getSessionMap().get("ingredients");
                    Iterator itr = ingredients.iterator();
                    while(itr.hasNext()){
                        Ingredient ingredient = (Ingredient) itr.next();
                        if(ingredient.getId().equals(Long.valueOf(value))){
                            return ingredient;
                        }
                    }
                    return null;
                    
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid ingredient."));
            }
    }
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Ingredient) object).getId());
        }
        else {
            return null;
        }
    }   

}