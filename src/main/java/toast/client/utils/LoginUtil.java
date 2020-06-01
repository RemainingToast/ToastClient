package toast.client.utils;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import org.apache.commons.codec.binary.Hex;
import toast.client.ToastClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static net.minecraft.client.util.Session.AccountType.LEGACY;

public class LoginUtil {
    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static String hashWith256(String textToHash) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] byteOfTextToHash = textToHash.getBytes(StandardCharsets.UTF_8);
        byte[] hashedByetArray = digest.digest(byteOfTextToHash);
        String encoded = Hex.encodeHexString(hashedByetArray);
        return encoded;
    }

//    public static boolean isAuthorized(String playerUUID) {
//        if (ToastClient.devMode) return true;
//        if (MinecraftClient.getInstance().getSession().equals(LEGACY)) return false;
//        try {
//            ArrayList<String> uuidList = new ArrayList<>();
//            URL url = new URL("http://192.99.198.36/hashes");
//            Scanner s = new Scanner(url.openStream());
//            while (s.hasNext()) {
//                uuidList.add(s.next());
//            }
//            s.close();
//            String uuidHASH = hashWith256(playerUUID);
//            if (uuidList.contains(uuidHASH)) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (NoSuchAlgorithmException | IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public static boolean setSession(Session newSession) {
        if (mc == null) return false;
        if (mc.getClass() == null) return false;
//        if (mc.getClass().getDeclaredFields() == null) return false;
        Field newField = null;
        for (Field field : mc.getClass().getDeclaredFields()) {
            if(field.getName().equalsIgnoreCase("session") || field.getName().equalsIgnoreCase("field_1726")) {
                newField = field;
            }
        }
        try {
            assert newField != null;
            newField.setAccessible(true);
            newField.set(mc, newSession);
            newField.setAccessible(false);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean loginWithPass(String mail, String pass) {
        if(mail.isEmpty() || pass.isEmpty()) return false;
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(mail);
        auth.setPassword(pass);
        try {
            auth.logIn();
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return false;
        }
        Session account = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        return setSession(account);
    }

    public static boolean loginCracked(String username) {
        if(username.isEmpty()) return false;
        Session session = new Session(username,
                UUID.randomUUID().toString(),
                "0", "legacy");
        return setSession(session);
    }
}
