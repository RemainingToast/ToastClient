package toast.client.lemongui.clickgui.settings;

import toast.client.utils.Config;

import java.util.HashMap;
import java.util.Map;

public class Settings {
	private Map<String, Object> settings = new HashMap<>();

	public void addSetting(String name, Object setting) {
		settings.put(name, setting);
		Config.writeOptions();
	}

	public Object getSetting(String name) {
		return this.settings.get(name);
	}

	public Map<String, Object> getSettings() {
		return this.settings;
	}

	public void setSettings(Map<String, Object> settings) {
		this.settings = settings;
	}

	public static boolean isCombo(Object setting){
		return setting instanceof ComboSetting;
	}
	
	public static boolean isCheck(Object setting){
		return setting instanceof CheckSetting;
	}
	
	public static boolean isSlider(Object setting){
		return setting instanceof SliderSetting;
	}
}
