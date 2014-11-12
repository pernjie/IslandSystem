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

@FacesValidator("yearValidator")
public class YearValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        // ...
        String input = value.toString();
        System.out.println("validate for: " + input);
        try {
            Integer foo = Integer.parseInt(input);
            if (foo < 2000 || foo>2100) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Input is invalid: Current valid years are 2000 to 2100.", null));
            }
        } catch (NumberFormatException ex) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Input is invalid: Current valid years are 2000 to 2100.", null));
        }
    }
}
