package toast.client.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FancyChatUtil {
    public static String rainbowText(String text) {
        String[] colors = {"&c", "&6", "&e", "&a", "&b", "&9", "&d"};
        final String[] result = {""};
        final int[] index = {-1};

        Arrays.stream(text.split(" ")).forEach(line -> {
            Arrays.stream(line.split("")).forEach((letter) -> {
                if (index[0] + 1 >= colors.length) index[0] -= colors.length;
                index[0]++;
                result[0] += colors[index[0]] + letter;
            });
            result[0] += " ";
        });
        return result[0];
    }

    public static String FaNcY(String text) {
        String result = "";
        boolean upperCase = true;
        for (String letter : text.split("")) {
            if(letter.equals(" ")) {
                result+=" ";
            } else {
                if(upperCase) {
                    result+=letter.toUpperCase();
                    upperCase = false;
                } else {
                    result+=letter.toLowerCase();
                    upperCase = true;
                }
            }
        }
        return result;
    }

    public static String classicFancy(String text) {
        Map<String, String> letters = getClassicLetters();
        StringBuilder finalString = new StringBuilder();
        for (String letter : text.split("")) {
            if(letter.equals(" ")) {
                finalString.append(" ");
            } else {
                String key = getValueFromKey(letters, letter, true);
                if(key == null) {
                    finalString.append(letter);
                } else {
                    finalString.append(key);
                }
            }
        }
        return finalString.toString();
    }

    public static String spaces(String text) {
        StringBuilder finalString = new StringBuilder();
        String text2 = text.replaceAll(" ", "");
        for (String letter : text2.split("")) {
            finalString.append(letter).append(" ");
        }
        return finalString.toString();
    }

    public static String Watermark(String text) {
        return text + " ⏐ ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ";
    }

    public static String Grammar(String text) {
        String newText = StringUtil.capitalize(text);
        if(newText.endsWith("?") || newText.endsWith(".") || newText.endsWith("!")) {
            return newText;
        } else {
            return newText+".";
        }
    }

    public static String getValueFromKey(Map<String, String> map, String key, boolean caseSensitive) {
        String valueFound = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (caseSensitive && k.equals(key)) {
                valueFound = v;
            } else if (!caseSensitive && k.equalsIgnoreCase(key)) {
                valueFound = v;
            }
        }
        return valueFound;
    }

    private static Map<String, String> getClassicLetters() {// TODO: find a easier way to do this
        Map<String, String> letters = Stream.of(new String[][] {
                { "0", "０" },
                { "1", "１" },
                { "2", "２" },
                { "3", "３" },
                { "4", "４" },
                { "5", "５" },
                { "6", "６" },
                { "7", "７" },
                { "8", "８" },
                { "9", "９" },
                { "A", "Ａ" },
                { "B", "Ｂ" },
                { "C", "Ｃ" },
                { "D", "Ｄ" },
                { "E", "Ｅ" },
                { "F", "Ｆ" },
                { "G", "Ｇ" },
                { "H", "Ｈ" },
                { "I", "Ｉ" },
                { "J", "Ｊ" },
                { "K", "Ｋ" },
                { "L", "Ｌ" },
                { "M", "Ｍ" },
                { "N", "Ｎ" },
                { "O", "Ｏ" },
                { "P", "Ｐ" },
                { "Q", "Ｑ" },
                { "R", "Ｒ" },
                { "S", "Ｓ" },
                { "T", "Ｔ" },
                { "U", "Ｕ" },
                { "V", "Ｖ" },
                { "W", "Ｗ" },
                { "X", "Ｘ" },
                { "Y", "Ｙ" },
                { "Z", "Ｚ" },
                { "a", "ａ" },
                { "b", "ｂ" },
                { "c", "ｃ" },
                { "d", "ｄ" },
                { "e", "ｅ" },
                { "f", "ｆ" },
                { "g", "ｇ" },
                { "h", "ｈ" },
                { "i", "ｉ" },
                { "j", "ｊ" },
                { "k", "ｋ" },
                { "l", "ｌ" },
                { "m", "ｍ" },
                { "n", "ｎ" },
                { "o", "ｏ" },
                { "p", "ｐ" },
                { "q", "ｑ" },
                { "r", "ｒ" },
                { "s", "ｓ" },
                { "t", "ｔ" },
                { "u", "ｕ" },
                { "v", "ｖ" },
                { "w", "ｗ" },
                { "x", "ｘ" },
                { "y", "ｙ" },
                { "z", "ｚ" },
                { "!", "！" },
                { "\"", "＂" },
                { "#", "＃" },
                { "$", "＄" },
                { "%", "％" },
                { "&", "＆" },
                { "'", "＇" },
                { "(", "（" },
                { ")", "）" },
                { "*", "＊" },
                { "+", "＋" },
                { ",", "，" },
                { "-", "－" },
                { ".", "．" },
                { "/", "／" },
                { ":", "：" },
                { ";", "；" },
                { "<", "＜" },
                { "=", "＝" },
                { ">", "＞" },
                { "?", "？" },
                { "@", "＠" },
                { "[", "［" },
                { "\\", "＼" },
                { "]", "］" },
                { "^", "＾" },
                { "_", "＿" },
                { "`", "｀" },
                { "{", "｛" },
                { "|", "｜" },
                { "}", "｝" },
                { "~", "～" },
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));
        return letters;
    }

}
