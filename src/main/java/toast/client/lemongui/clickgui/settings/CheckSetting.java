package toast.client.lemongui.clickgui.settings;

import com.google.gson.annotations.SerializedName;
import toast.client.utils.Config;

public class CheckSetting {
	@SerializedName("enabled")
	private boolean bval;
	public CheckSetting(boolean bval){
		this.bval = bval;
	}

	public boolean getValBoolean(){
		return this.bval;
	}

	public void setValBoolean(boolean in){
		this.bval = in;
		Config.writeOptions();
	}
}
