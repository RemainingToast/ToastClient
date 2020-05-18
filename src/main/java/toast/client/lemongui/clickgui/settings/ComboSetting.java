package toast.client.lemongui.clickgui.settings;

import com.google.gson.annotations.SerializedName;
import toast.client.utils.Config;

import java.util.ArrayList;

public class ComboSetting {
	@SerializedName("Selected")
	private String sval;
	@SerializedName("Available")
	private ArrayList<String> options;

	public ComboSetting(String sval, ArrayList<String> options) {
		this.sval = sval;
		this.options = options;
	}

	public String getValString(){
		return this.sval;
	}

	public void setValString(String in){
		this.sval = in;
		Config.writeOptions();
	}

	public ArrayList<String> getOptions(){
		return this.options;
	}
}
