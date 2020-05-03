package toast.client.lemongui.settings;

import java.util.ArrayList;

import toast.client.ToastClient;
import toast.client.modules.Module;

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit him
 *
 *  @author HeroCode
 */
public class SettingsManager {
	
	private ArrayList<Setting> settings;
	
	public SettingsManager() {
		this.settings = new ArrayList<>();
	}
	
	public void rSetting(Setting in) {
		this.settings.add(in);
	}
	
	public ArrayList<Setting> getSettings() {
		return this.settings;
	}
	
	public ArrayList<Setting> getSettingsByMod(Module mod) {
		ArrayList<Setting> out = new ArrayList<>();
		for(Setting s : getSettings()) {
			if(s.getParentMod().equals(mod)) {
				out.add(s);
			}
		}
		if(out.isEmpty()) {
			return null;
		}
		return out;
	}
	
	public Setting getSettingByName(String name) {
		for(Setting set : getSettings()) {
			if(set.getName().equalsIgnoreCase(name)) {
				return set;
			}
		}
		System.err.println("["+ ToastClient.cleanPrefix + "] Error Setting NOT found: '" + name +"'!");
		return null;
	}
	public Setting getSettingByName(String name, Module m) {
		for(Setting set : getSettings()) {
			if(set.getName().equalsIgnoreCase(name) && set.getParentMod() == m) {
				return set;
			}
		}
		return null;
	}

}