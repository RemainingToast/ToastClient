package toast.client.lemongui.clickgui.settings;

import toast.client.utils.Config;

import java.util.HashMap;
import java.util.Map;

public class Settings {
	private Map<String, Setting> settings = new HashMap<>();

	public void addSetting(String name, Object setting) {
		if (setting instanceof CheckSetting) {
			settings.put(name, new Setting((CheckSetting) setting));
		} else if (setting instanceof ComboSetting) {
			settings.put(name, new Setting((ComboSetting) setting));
	    } else if (setting instanceof SliderSetting) {
			settings.put(name, new Setting((SliderSetting) setting));
		}
		Config.writeOptions();
	}

	public Object getSetting(String name) {
		return this.settings.get(name);
	}

	public Map<String, Setting> getSettings() {
		return this.settings;
	}

	public void setSettings(Map<String, Setting> settings) {
		this.settings = settings;
	}
}
