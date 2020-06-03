package toast.client.utils;

public class KeyUtil {
    public static boolean isNumeric(String str)
    {
        try
        {
            int i = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
