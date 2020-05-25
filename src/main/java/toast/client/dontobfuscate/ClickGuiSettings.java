package toast.client.dontobfuscate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import toast.client.modules.Module;
import toast.client.utils.FileManager;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClickGuiSettings {
    public static final String guiColorsFile = "guicolors.json";
    public static final String clickguiPosFile = "clickGuiPositions.json";
    public static final int defaultOnTextColor = new Color(255, 255, 255, 255).getRGB();
    public static final int defaultOffTextColor = new Color(177, 177, 177, 255).getRGB();
    public static final int defaultBgColor = new Color(0, 0, 0, 64).getRGB();
    public static final int defaultHoverBgColor = new Color(131, 212, 252, 92).getRGB();
    public static final int defaultBoxColor = new Color(0, 0, 0, 255).getRGB();
    public static final int defaultPrefixColor = new Color(8, 189, 8, 255).getRGB();
    public static final int defaultClickColor = new Color(121, 205, 255, 128).getRGB();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Colors colors = new Colors();
    public static Map<String, CategorySetting> categoryPositions = new HashMap<>();

    public static CategorySetting getPositions(String category) {
        return categoryPositions.get(category);
    }

    public static Map<String, CategorySetting> getCategoryPositions() {
        return categoryPositions;
    }

    public static void setCategoryPositions(Map<String, CategorySetting> categoryPositions) {
        ClickGuiSettings.categoryPositions = categoryPositions;
    }

    public static void initCategoryPositions() {
        int i = 0;
        for (Module.Category category : Module.Category.values()) {
            int x = 10 + (100 * i) + (10 * i);
            categoryPositions.put(category.toString(), new CategorySetting(x, 10, false, new String[]{}));
            i++;
        }
    }

    public static void loadPositions() {
        try {
            categoryPositions = gson.fromJson(new FileReader(FileManager.createFile(clickguiPosFile)), new TypeToken<Map<String, CategorySetting>>() {
            }.getType());
            if (categoryPositions == null) {
                initCategoryPositions();
                savePositions();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void savePositions() {
        try {
            FileWriter fw = new FileWriter(FileManager.createFile(clickguiPosFile));
            gson.toJson(categoryPositions, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveColors() {
        try {
            FileWriter fw = new FileWriter(FileManager.createFile(guiColorsFile));
            gson.toJson(colors, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void allDefaults() {
        setCategoryDefaults();
        setDescriptionDefaults();
        setModuleDefaults();
        setSettingsDefaults();
    }

    public static void setCategoryDefaults() {
        colors.categoryBgColor = defaultBgColor;
        colors.categoryBoxColor = defaultBoxColor;
        colors.categoryClickColor = defaultClickColor;
        colors.categoryHoverBgColor = defaultHoverBgColor;
        colors.categoryPrefix = "> ";
        colors.categoryPrefixColor = defaultPrefixColor;
        colors.categoryTextColor = defaultOnTextColor;
    }

    public static void setDescriptionDefaults() {
        colors.descriptionBgColor = new Color(83, 83, 83, 255).getRGB();
        colors.descriptionBoxColor = defaultBoxColor;
        colors.descriptionPrefix = "";
        colors.descriptionPrefixColor = defaultPrefixColor;
        colors.descriptionTextColor = defaultOnTextColor;
    }

    public static void setModuleDefaults() {
        colors.moduleBoxColor = defaultBoxColor;
        colors.moduleClickColor = defaultClickColor;
        colors.moduleHoverBgColor = defaultHoverBgColor;
        colors.moduleOffBgColor = defaultBgColor;
        colors.moduleOffTextColor = defaultOffTextColor;
        colors.moduleOnBgColor = defaultBgColor;
        colors.moduleOnTextColor = defaultOnTextColor;
        colors.modulePrefix = " > ";
        colors.modulePrefixColor = defaultPrefixColor;
    }

    public static void setSettingsDefaults() {
        colors.settingBoxColor = defaultBoxColor;
        colors.settingClickColor = defaultClickColor;
        colors.settingHoverBgColor = defaultHoverBgColor;
        colors.settingOffBgColor = defaultBgColor;
        colors.settingOffTextColor = defaultOffTextColor;
        colors.settingOnBgColor = defaultBgColor;
        colors.settingOnTextColor = defaultOnTextColor;
        colors.settingPrefix = "  > ";
        colors.settingPrefixColor = defaultPrefixColor;
    }

    public static void loadColors() {
        try {
            colors = gson.fromJson(new FileReader(FileManager.createFile(guiColorsFile)), new TypeToken<Colors>() {
            }.getType());
            if (colors == null) {
                allDefaults();
                saveColors();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static class Colors {
        public String categoryPrefix;
        public int categoryTextColor;
        public int categoryHoverBgColor;
        public int categoryBgColor;
        public int categoryBoxColor;
        public int categoryPrefixColor;
        public int categoryClickColor;
        public String descriptionPrefix;
        public int descriptionTextColor;
        public int descriptionBoxColor;
        public int descriptionBgColor;
        public int descriptionPrefixColor;
        public String modulePrefix;
        public int moduleOnTextColor;
        public int moduleOnBgColor;
        public int moduleOffTextColor;
        public int moduleOffBgColor;
        public int moduleHoverBgColor;
        public int moduleBoxColor;
        public int modulePrefixColor;
        public int moduleClickColor;
        public String settingPrefix;
        public int settingOnTextColor;
        public int settingOnBgColor;
        public int settingOffTextColor;
        public int settingOffBgColor;
        public int settingHoverBgColor;
        public int settingBoxColor;
        public int settingPrefixColor;
        public int settingClickColor;
    }
}

