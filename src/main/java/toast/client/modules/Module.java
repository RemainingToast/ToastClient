package toast.client.modules;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import toast.client.event.EventImpl;
import toast.client.event.EventManager;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.config.ModuleSettings;
import toast.client.modules.config.Setting;

import java.util.Map;

import static toast.client.ToastClient.CONFIG_MANAGER;

@Environment(EnvType.CLIENT)
public class Module {
    public String name;
    public String description;
    public boolean enabled;
    public int key;
    public Category category;


    public ModuleSettings settings = new ModuleSettings();

    public MinecraftClient mc = MinecraftClient.getInstance();

    public Module(String name, String description, Category category, int key) {
        this.name = name;
        this.description = description;
        this.key = key;
        this.category = category;
        this.settings.addBoolean("Visible", true);
    }

    public String getDescription() {
        return description;
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
        CONFIG_MANAGER.writeModules();
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

    public void setSettings(Map<String, Setting> newSettings) {
        this.settings.setSettings(newSettings);
    }

    public boolean isMode(String mode) {
        return this.settings.getMode("Mode").equals(mode);
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

    public void disable() {
        setEnabled(false);
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public void onEnable() {}

    public void onDisable() {}

    public enum Category {
        PLAYER,
        MOVEMENT,
        RENDER,
        COMBAT,
        MISC
    }

}
