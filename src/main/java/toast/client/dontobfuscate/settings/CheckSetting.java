package toast.client.dontobfuscate.settings;

import com.google.gson.annotations.SerializedName;
import toast.client.dontobfuscate.Config;

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
