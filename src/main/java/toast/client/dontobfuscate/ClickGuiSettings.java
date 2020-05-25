package toast.client.dontobfuscate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.MinecraftClient;
import toast.client.modules.Module;
import toast.client.utils.FileManager;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static toast.client.ToastClient.clickGui;

public class ClickGuiSettings {
    public static final String clickguiColorsFile = "guicolors.json";
    public static final String clickguiPosFile = "clickGuiPositions.json";
    public static final int defaultOnTextColor = new Color(255, 255, 255, 255).getRGB();
    public static final int defaultOffTextColor = new Color(177, 177, 177, 255).getRGB();
    public static final int defaultBgColor = new Color(0, 0, 0, 64).getRGB();
    public static final int defaultHoverBgColor = new Color(131, 212, 252, 92).getRGB();
    public static final int defaultBoxColor = new Color(0, 0, 0, 255).getRGB();
    public static final int defaultPrefixColor = new Color(8, 189, 8, 255).getRGB();
    public static final int defaultClickColor = new Color(121, 205, 255, 128).getRGB();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public Colors colors = new Colors();
    public static Map<String, CategorySetting> categoryPositions = new HashMap<>();

    public CategorySetting getPositions(String category) {
        if (categoryPositions == null) {
            loadPositions();
        }
        return categoryPositions.get(category);
    }

    public Map<String, CategorySetting> getCategoryPositions() {
        return categoryPositions;
    }

    public void setCategoryPositions(Map<String, CategorySetting> categoryPositions) {
        ClickGuiSettings.categoryPositions = categoryPositions;
    }

    public void initCategoryPositions() {
        categoryPositions = new HashMap<>();
        int i = 0;
        int y = 5;
        System.out.println(MinecraftClient.getInstance().getWindow().getWidth());
        for (Module.Category category : Module.Category.values()) {
            int x = 5 + (clickGui.width * i) + (5 * i);
            if (x + clickGui.width + 10 > MinecraftClient.getInstance().getWindow().getWidth()/2) {
                y = y + (MinecraftClient.getInstance().getWindow().getHeight()/2)/3;
                x = 5;
                i = 0;
            }
            categoryPositions.put(category.toString(), new CategorySetting(x, y, false, new ArrayList<>()));
            i++;
        }
    }

    public Colors getColors() {
        return colors;
    }

    public void loadPositions() {
        try {
            categoryPositions = gson.fromJson(new FileReader(FileManager.createFile(clickguiPosFile)), new TypeToken<Map<String, CategorySetting>>() {
            }.getType());
            if (categoryPositions == null && clickGui != null) {
                initCategoryPositions();
                savePositions();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void savePositions() {
        FileManager.writeFile(clickguiPosFile, gson.toJson(categoryPositions));
    }

    public void saveColors() {
        FileManager.writeFile(clickguiColorsFile, gson.toJson(colors));
    }

    public void allDefaults() {
        colors = new Colors();
        setCategoryDefaults();
        setDescriptionDefaults();
        setModuleDefaults();
        setSettingsDefaults();
    }

    public void setCategoryDefaults() {
        colors.categoryBgColor = defaultBgColor;
        colors.categoryBoxColor = defaultBoxColor;
        colors.categoryClickColor = defaultClickColor;
        colors.categoryHoverBgColor = defaultHoverBgColor;
        colors.categoryPrefix = "> ";
        colors.categoryPrefixColor = defaultPrefixColor;
        colors.categoryTextColor = defaultOnTextColor;
    }

    public void setDescriptionDefaults() {
        colors.descriptionBgColor = new Color(83, 83, 83, 255).getRGB();
        colors.descriptionBoxColor = defaultBoxColor;
        colors.descriptionPrefix = "";
        colors.descriptionPrefixColor = defaultPrefixColor;
        colors.descriptionTextColor = defaultOnTextColor;
    }

    public void setModuleDefaults() {
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

    public void setSettingsDefaults() {
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

    public void loadColors() {
        try {
            colors = gson.fromJson(new FileReader(FileManager.createFile(clickguiColorsFile)), new TypeToken<Colors>() {
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

