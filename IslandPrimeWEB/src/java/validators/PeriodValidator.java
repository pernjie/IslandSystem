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

@FacesValidator("periodValidator")
public class PeriodValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        // ...
        String input = value.toString();
        System.out.println("validate for: " + input);
        try {
            Integer foo = Integer.parseInt(input);
            if (foo < 1 || foo>13) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Input is invalid: Periods are values 1 to 13.", null));
            }
        } catch (NumberFormatException ex) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Input is invalid: Periods are values 1 to 13.", null));
        }
    }
}
