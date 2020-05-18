package toast.client.lemongui.clickgui.settings;

import com.google.gson.annotations.SerializedName;
import toast.client.utils.Config;

public class SliderSetting {
	@SerializedName("Value")
	private double dval;

	@SerializedName("Minimum value")
	private double min;
	@SerializedName("Maximum value")
	private double max;
	@SerializedName("Only integers")
	private boolean onlyint = false;

	public SliderSetting(double dval, double min, double max, boolean onlyint) {
		this.dval = dval;
		this.min = min;
		this.max = max;
		this.onlyint = onlyint;
	}

	public SliderSetting(double dval, double min, double max) {
		this.dval = dval;
		this.min = min;
		this.max = max;
		this.onlyint = false;
	}

	public double getValDouble(){
		if(this.onlyint){
			this.dval = (int)dval;
		}
		return this.dval;
	}

	public void setValDouble(double in){
		this.dval = in;
		Config.writeOptions();
	}

	public double getMin(){
		return this.min;
	}

	public double getMax(){
		return this.max;
	}

	public boolean onlyInt(){
		return this.onlyint;
	}
}
