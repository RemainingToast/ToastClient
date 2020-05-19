package toast.client.dontobfuscate.settings;

import com.google.gson.annotations.SerializedName;

public class Setting {
    @SerializedName("Boolean")
    private CheckSetting check;
    @SerializedName("Selection")
    private ComboSetting combo;
    @SerializedName("Number")
    private SliderSetting slider;
    @SerializedName("Type")
    private int type;

    public Setting(CheckSetting check) {
        this.check = check;
        this.combo = null;
        this.slider = null;
        type = 0;
    }

    public Setting(ComboSetting combo) {
        this.check = null;
        this.combo = combo;
        this.slider = null;
        type = 1;
    }

    public Setting(SliderSetting slider) {
        this.check = null;
        this.combo = null;
        this.slider = slider;
        type = 2;
    }

    public int getType() {
        return type;
    }

    public CheckSetting getCheck() {
        return check;
    }

    public ComboSetting getCombo() {
        return combo;
    }

    public SliderSetting getSlider() {
        return slider;
    }

    public void setCheck(CheckSetting check) {
        this.check = check;
    }

    public void setCombo(ComboSetting combo) {
        this.combo = combo;
    }

    public void setSlider(SliderSetting slider) {
        this.slider = slider;
    }
}
