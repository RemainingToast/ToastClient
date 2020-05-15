package toast.client.modules;

import toast.client.event.EventManager;
import toast.client.lemongui.settings.Setting;
import net.minecraft.client.MinecraftClient;
import toast.client.utils.Config;

import java.util.ArrayList;
import java.util.Arrays;

public class Module {
    public String name;
    public boolean toggled;
    public int key;
    public Category category;
    public MinecraftClient mc = MinecraftClient.getInstance();
    public boolean hasModes;

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

    public int getModeInt() {
        if (ModuleManager.setmgr.getSettingByName("Mode", this) != null) {
            return ModuleManager.setmgr.getSettingByName("Mode", this).getValString().length();
        }
        return 0;
    }

    public String getMode() {
        if (ModuleManager.setmgr.getSettingByName("Mode", this) != null) {
            return ModuleManager.setmgr.getSettingByName("Mode", this).getValString();
        } else {
            return "";
        }
    }

    public void addBool(String string, boolean b) {
        ModuleManager.setmgr.rSetting(new Setting(string, this, b));
    }

    public void addModes(String... modes) {
        this.hasModes = true;
        ArrayList<String> modes2 = new ArrayList(Arrays.asList(modes));
        ModuleManager.setmgr.rSetting(new Setting("Mode", this, modes[0], modes2));
    }

    public void addModesCustomName(String modename, String... modes) {
        this.hasModes = true;
        ArrayList<String> modes2 = new ArrayList(Arrays.asList(modes));
        ModuleManager.setmgr.rSetting(new Setting(modename, this, modes[0], modes2));
    }

    public void addNumberOption(String string, double dval, double min, double max) {
        ModuleManager.setmgr.rSetting(new Setting(string, this, dval, min, max, false));
    }

    public void addNumberOption(String string, double dval, double min, double max, boolean onlyint) {
        ModuleManager.setmgr.rSetting(new Setting(string, this, dval, min, max, onlyint));
    }

    public boolean getBool(String string) {
        return ModuleManager.setmgr.getSettingByName(string, this).getValBoolean();
    }

    public double getDouble(String string) {
        return ModuleManager.setmgr.getSettingByName(string, this).getValDouble();
    }

    protected String getString(String string) {
        return ModuleManager.setmgr.getSettingByName(string, this).getValString();
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
