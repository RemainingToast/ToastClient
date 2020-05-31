package toast.client.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
//    Path path = Paths.get("/Users/emceewheeze/Documents/ToastClient/src/main/java/toast/client/auth/hashfile.txt");
//    public static Stream<String> lines(Path path) throws IOException {
//        return lines(path);
//    }
//     public static void readFile() throws FileNotFoundException {
//         BufferedReader br = new BufferedReader(new FileReader("file.txt"));
//         try {
//             StringBuilder sb = new StringBuilder();
//             String line = br.readLine();
//
//             while (line != null) {
//                 sb.append(line);
//                 sb.append(System.lineSeparator());
//                 line = br.readLine();
//             }
//             String everything = sb.toString();
//         } catch (IOException e) {
//             e.printStackTrace();
//         } finally {
//             try {
//                 br.close();
//             } catch (IOException e) {
//                 e.printStackTrace();
//             }
//         }
//     }
}
