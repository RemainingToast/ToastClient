package toast.client.modules;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import toast.client.dontobfuscate.Config;
import toast.client.dontobfuscate.ModuleSettings;
import toast.client.event.EventManager;

@Environment(EnvType.CLIENT)
public class Module {
    public String name;
    public boolean enabled;
    public int key;
    public Category category;

    public ModuleSettings settings = new ModuleSettings();

    public MinecraftClient mc = MinecraftClient.getInstance();

    public Module(String name, Category category, int key) {
        this.name = name;
        this.key = key;
        this.category = category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (isEnabled()) {
            EventManager.register(this);
            onEnable();
        } else {
            EventManager.unregister(this);
            onDisable();
        }
        Config.writeModules();
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public ModuleSettings getSettings() {
        return settings;
    }

    public void setSettings(ModuleSettings settings) {
        this.settings = settings;
    }

    public boolean isMode(String mode) {
        if (this.settings.getMode("Mode").equals(mode)) return true;
        return false;
    }

    public String getMode() {
        return this.settings.getMode("Mode");
    }

    public double getDouble(String name) {
        return this.settings.getValue(name);
    }

    public boolean getBool(String name) {
        return this.settings.getBoolean(name);
    }

    public void enable() {
        setEnabled(true);
    }

    public void disable() {
        setEnabled(false);
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public void onEnable() {
    }

    public void onDisable() {
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
