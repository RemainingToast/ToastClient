package toast.client.dontobfuscate;

import java.util.HashMap;
import java.util.Map;

public class ModuleSettings {
    private Map<String, Setting> settings = new HashMap<>();
    private Map<String, SettingDef> settingsDef = new HashMap<>();

    public void addBoolean(String name, boolean defaultValue) {
        settings.put(name, new Setting(defaultValue));
        settingsDef.put(name, new SettingDef());
    }

    public void addMode(String name, String defaultMode, String... modes) {
        settings.put(name, new Setting(defaultMode));
        settingsDef.put(name, new SettingDef(modes));
    }

    public void addSlider(String name, double minimumValue, double defaultValue, double maximumValue) {
        settings.put(name, new Setting(defaultValue));
        settingsDef.put(name, new SettingDef(minimumValue, maximumValue));
    }

    public Setting getSetting(String name) {
        return settings.get(name);
    }

    public SettingDef getSettingDef(String name) {
        return settingsDef.get(name);
    }

    public boolean getBoolean(String name) {
        return settings.get(name).isEnabled();
    }

    public String getMode(String name) {
        return settings.get(name).getMode();
    }

    public double getValue(String name) {
        return settings.get(name).getValue();
    }

    public String[] getModes(String name) {
        return settingsDef.get(name).getModes();
    }

    public double getMax(String name) {
        return settingsDef.get(name).getMaxValue();
    }

    public double getMin(String name) {
        return settingsDef.get(name).getMinValue();
    }

    public Map<String, Setting> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Setting> settings) {
        this.settings = settings;
    }

    public Map<String, SettingDef> getSettingsDef() {
        return settingsDef;
    }

    public void setSettingsDef(Map<String, SettingDef> settingsDef) {
        this.settingsDef = settingsDef;
    }
}
