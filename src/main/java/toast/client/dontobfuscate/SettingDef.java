package toast.client.dontobfuscate;

import java.util.ArrayList;

public class SettingDef {
    private ArrayList<String> modes;
    private boolean isBool;
    private Double maxvalue;
    private Double minvalue;

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
