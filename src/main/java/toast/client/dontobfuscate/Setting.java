package toast.client.dontobfuscate;

import com.google.gson.annotations.SerializedName;

public class Setting {
    @SerializedName("Current Mode")
    private String mode;
    @SerializedName("Enabled")
    private Boolean enabled;
    @SerializedName("Current Value")
    private Double value;

    public Setting(String mode) {
        setMode(mode);
    }

    public Setting(Double value) {
        setValue(value);
    }

    public Setting(Boolean enabled) {
        setEnabled(enabled);
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String newMode) {
        this.mode = newMode;
        this.value = null;
        this.enabled = null;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double newValue) {
        this.mode = null;
        this.value = newValue;
        this.enabled = null;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.mode = null;
        this.value = null;
        this.enabled = enabled;
    }

    public String getType() {
        if (mode != null) return "mode";
        else if (value != null) return "value";
        else if (enabled != null) return "boolean";
        else return null;
    }
}
