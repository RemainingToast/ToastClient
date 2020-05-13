package toast.client.utils;

import joptsimple.internal.Strings;
import java.util.Arrays;

public class StringUtil {
    public static String joinAllButFirst(String[] strings, String separator) {
        return Strings.join(Arrays.copyOfRange(strings, 1, strings.length), separator);
    }

    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
