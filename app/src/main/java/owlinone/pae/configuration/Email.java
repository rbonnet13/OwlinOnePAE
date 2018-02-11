package owlinone.pae.configuration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AnthonyCOPPIN on 11/02/2018.
 */

public class Email {

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"; //Regex pour savoir si le mail est de bonne forme
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
