package toast.client.modules;

import net.minecraft.client.MinecraftClient;
import toast.client.event.EventManager;
import toast.client.dontobfuscate.settings.*;
import toast.client.dontobfuscate.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Module {
    public String name;
    public boolean toggled;
    public int key;
    public Category category;
    public MinecraftClient mc = MinecraftClient.getInstance();
    public boolean hasModes;
    public Settings settings = new Settings();

    public Module(String name, Category category, int key) {
        this.name = name;
        this.key = key;
        this.category = category;
    }

    public boolean isEnabled() {
        return toggled;
    }

    public int getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        if(toggled) {
            EventManager.register(this);
            onEnable();
        } else {
            EventManager.unregister(this);
            onDisable();
        }
        Config.writeModules();
    }

    public void toggle() {
        this.toggled = !toggled;
        if(toggled) {
            EventManager.register(this);
            onEnable();
        } else {
            EventManager.unregister(this);
            onDisable();
        }
        Config.writeModules();
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public Map<String, Setting> getSettings() {
        return this.settings.getSettings();
    }

    public void setSettings(Map<String, Setting> settings) {
        this.settings.setSettings(settings);
    }

    public int getModeInt() {
        if (this.settings.getSetting("Mode") != null) {
            ComboSetting mode = (ComboSetting) this.settings.getSetting("Mode");
            return mode.getValString().length();
        }
        return 0;
    }

    public String getMode() {
        if (this.settings.getSetting("Mode") != null) {
            ComboSetting mode = (ComboSetting) this.settings.getSetting("Mode");
            return mode.getValString();
        } else {
            return "";
        }
    }

    public void addBool(String string, boolean b) {
        this.settings.addSetting(string, new CheckSetting(b));
    }

    public void addModes(String... modes) {
        this.hasModes = true;
        ArrayList<String> modes2 = new ArrayList(Arrays.asList(modes));
        this.settings.addSetting("Mode", new ComboSetting(modes[0], modes2));
    }

    public void addModesCustomName(String modename, String... modes) {
        this.hasModes = true;
        ArrayList<String> modes2 = new ArrayList(Arrays.asList(modes));
        this.settings.addSetting(modename, new ComboSetting(modes[0], modes2));
    }

    public void addNumberOption(String string, double dval, double min, double max) {
        this.settings.addSetting(string, new SliderSetting(dval, min, max, false));
    }

    public void addNumberOption(String string, double dval, double min, double max, boolean onlyint) {
        this.settings.addSetting(string, new SliderSetting(dval, min, max, onlyint));
    }

    public boolean getBool(String string) {
        Setting setting = (Setting) this.settings.getSetting(string);
        return setting.getCheck().getValBoolean();
    }

    public double getDouble(String string) {
        Setting setting = (Setting) this.settings.getSetting(string);
        return setting.getSlider().getValDouble();
    }

    protected String getString(String string) {
        Setting setting = (Setting) this.settings.getSetting(string);
        return setting.getCombo().getValString();
    }

    protected boolean isMode(String m) {
        return this.getString("Mode").equalsIgnoreCase(m);
    }

    public enum Category {
        PLAYER,
        MOVEMENT,
        RENDER,
        COMBAT,
        MISC,
        DEV
    }

}
