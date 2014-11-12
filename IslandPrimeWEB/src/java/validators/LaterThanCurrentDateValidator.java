/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.lang.Integer;
import java.lang.NumberFormatException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@FacesValidator("laterThanCurrentDateValidator")
public class LaterThanCurrentDateValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        // ...
        Date input = (Date)value;
        System.out.println("validate for: " + input);
        try {
            Date todayDate = new Date();
            if (!input.after(todayDate)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Input is invalid: Date must be later than current date.", null));
            }
        } catch (NumberFormatException ex) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Input is invalid: Date must be later than current date.", null));
        }
    }
}
