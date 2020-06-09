package toast.client.modules.config;

import java.util.ArrayList;

public class SettingDef {
    private final ArrayList<String> modes;
    private final boolean isBool;
    private final Double maxvalue;
    private final Double minvalue;

    public SettingDef(ArrayList<String> modes) {
        this.modes = modes;
        this.isBool = false;
        this.maxvalue = this.minvalue = null;
    }

    public SettingDef(Double minvalue, Double maxvalue) {
        this.modes = null;
        this.isBool = false;
        this.minvalue = minvalue;
        this.maxvalue = maxvalue;
    }

    public SettingDef() {
        this.modes = null;
        this.isBool = true;
        this.maxvalue = this.minvalue = null;
    }

    public ArrayList<String> getModes() {
        return modes;
    }

    public Double getMinValue() {
        return minvalue;
    }

    public Double getMaxValue() {
        return maxvalue;
    }

    public String getType() {
        if (modes != null) return "mode";
        else if (minvalue != null && maxvalue != null) return "value";
        else if (isBool) return "boolean";
        else return null;
    }
}
