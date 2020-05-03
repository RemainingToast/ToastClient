package toast.client.utils;

import net.minecraft.client.MinecraftClient;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;

public class ProxyUtil {
    public static boolean setProxy(Proxy proxy) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Field newField = null;
        for (Field field : mc.getClass().getDeclaredFields()) {
            if(field.getName().equalsIgnoreCase("netProxy")) {
                newField = field;
            }
        }
        try {
            newField.setAccessible(true);
            newField.set(mc, proxy);
            newField.setAccessible(false);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean connectProxy(String proxyIP, int proxyPort, Proxy.Type type) {
        Proxy proxy = new Proxy(type, new InetSocketAddress(proxyIP, proxyPort));
        return setProxy(proxy);
    }

}
