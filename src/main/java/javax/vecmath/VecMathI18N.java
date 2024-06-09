package javax.vecmath;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

class VecMathI18N {
    VecMathI18N() {
    }

    static String getString(String key) {
        String s2;
        try {
            s2 = ResourceBundle.getBundle("javax.vecmath.ExceptionStrings").getString(key);
        }
        catch (MissingResourceException e) {
            System.err.println("VecMathI18N: Error looking up: " + key);
            s2 = key;
        }
        return s2;
    }
}

