package dev.toastmc.toastclient.api.setting;

import dev.toastmc.toastclient.api.module.Module;
import dev.toastmc.toastclient.api.setting.types.*;
import dev.toastmc.toastclient.api.util.ToastColor;
import dev.toastmc.toastclient.api.util.UtilKt;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Setting<T> {

    private final String name;
    private final String configName;
    private final Module parent;
    private final Module.Category faxCategory;
    private final Type type;
    private boolean hidden;
    private boolean grouped;
    private Consumer<T> changedListener;
    private T value;
    private int priority;

    public Setting(
            final String name,
            final Module parent,
            final Module.Category faxCategory,
            final Type type) {
        this.name = name;
        this.configName = name.replace(" ", "");
        this.parent = parent;
        this.type = type;
        this.faxCategory = faxCategory;
        this.hidden = false;
        this.grouped = false;
        this.priority = 0;
    }

    public String getName() {
        return this.name;
    }

    public String getConfigName() {
        return this.configName;
    }

    public Module getParent() {
        return this.parent;
    }

    public Type getType() {
        return this.type;
    }

    public Module.Category getCategory() {
        return this.faxCategory;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setChangedListener(Consumer<T> listener) {
        this.changedListener = listener;
    }

    public void changed() {
        if (changedListener != null) changedListener.accept(value);
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getStringValue() {
        return "";
    }

    public boolean setValueFromString(String value) { return false; }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setGrouped(boolean grouped) {
        this.grouped = grouped;
    }

    public boolean isGrouped() {
        return grouped;
    }

    public enum Type {
        BOOLEAN,
        DOUBLE,
        GROUP,
        COLOR,
        MODE
    }

    public static class Double extends Setting<Double> implements NumberSetting {

        private double value;
        private final double min;
        private final double max;

        public Double(
                final String name,
                final Module parent,
                final double value,
                final double min,
                final double max) {
            super(name, parent, parent.getCategory(), Type.DOUBLE);
            this.value = value;
            this.min = min;
            this.max = max;
        }

        public double getValue() {
            return value;
        }

        public int getIntValue() { return (int) this.value; }

        public float getFloatValue() { return (float) this.value; }

        public void setValue(final double value) {
            this.value = value;
            setValue(this);
            changed();
        }

        public double getMin() {
            return this.min;
        }

        public double getMax() {
            return this.max;
        }

        @Override
        public double getNumber() {
            return this.value;
        }

        @Override
        public void setNumber(double value) {
            setValue(value);
        }

        @Override
        public double getMaximumValue() {
            return this.max;
        }

        @Override
        public double getMinimumValue() {
            return this.min;
        }

        @Override
        public int getPrecision() {
            return 2;
        }

        @Override
        public boolean setValueFromString(String value) {
            try {
                setValue(java.lang.Double.parseDouble(value));
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public String getStringValue() {
            return String.valueOf(value);
        }

        public Double onChanged(Consumer<Double> listener){
            setChangedListener(listener);
            return this;
        }
    }

    public static class Boolean extends Setting<Boolean> implements BooleanSetting {

        private boolean value;

        public Boolean(
                final String name,
                final Module parent,
                final boolean value) {
            super(name, parent, parent.getCategory(), Type.BOOLEAN);
            this.value = value;
        }

        public boolean getValue(){
            return this.value;
        }

        public void setValue(final boolean value) {
            this.value = value;
            setValue(this);
            changed();
        }

        @Override
        public boolean setValueFromString(String value) {
            boolean bool = java.lang.Boolean.parseBoolean(value);
            setValue(bool);
            return bool;
        }

        @Override
        public String getStringValue() {
            return String.valueOf(value);
        }

        @Override
        public void toggle() {
            setValue(!this.value);
        }

        @Override
        public boolean enabled() {
            return this.value;
        }

        public Boolean onChanged(Consumer<Boolean> listener){
            setChangedListener(listener);
            return this;
        }
    }

    public static class Mode extends Setting<Mode> implements EnumSetting {

        private String value;
        private final List<String> modes;

        public Mode(final String name, final Module parent, final String value, final String... modes) {
            super(name, parent, parent.getCategory(), Type.MODE);
            this.value = value;
            this.modes = Arrays.asList(modes);
        }

        public Mode(final String name, final Module parent, final String value, final List<String> modes) {
            super(name, parent, parent.getCategory(), Type.MODE);
            this.value = value;
            this.modes = modes;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(final String value) {
             this.value = value;
             setValue(this);
             changed();
        }

        @Override
        public boolean setValueFromString(String value) {
            if(modes.stream().anyMatch(value::equalsIgnoreCase)) {
                setValue(UtilKt.capitalize(value));
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getStringValue() {
            return value;
        }

        public List<String> getModes() {
            return modes;
        }

        public boolean toggled(String mode){
            return getValue().equalsIgnoreCase(mode);
        }

        @Override
        public void increment() {
            int modeIndex = modes.indexOf(value);
            modeIndex = (modeIndex + 1) % modes.size();
            setValue(modes.get(modeIndex));
        }

        @Override
        public String getValueName() {
            return this.value;
        }

        public Mode onChanged(Consumer<Mode> listener){
            setChangedListener(listener);
            return this;
        }
    }

    public static class ColorSetting extends Setting<ColorSetting> implements dev.toastmc.toastclient.api.setting.types.ColorSetting {

        private boolean rainbow;
        private ToastColor value;

        public ColorSetting(final String name, final Module parent, boolean rainbow, final ToastColor value) {
            super(name, parent, parent.getCategory(), Type.COLOR);
            this.rainbow=rainbow;
            this.value=value;
        }

        public ToastColor getValue() {
            if (rainbow) {
                return ToastColor.fromHSB((System.currentTimeMillis()%(360*32))/(360f * 32),1,1);
            }
            return this.value;
        }

        public void setValue(boolean rainbow, final ToastColor value) {
            this.rainbow = rainbow;
            this.value = value;
            changed();
        }

        @Override
        public boolean setValueFromString(String value) {
            try {
                setValue(Color.decode(value));
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public String getStringValue() {
            return String.valueOf(value);
        }

        public int toInteger() {
            return this.value.getRGB()&0xFFFFFF+(rainbow?1:0)*0x1000000;
        }

        public void fromInteger(int number) {
            this.value = new ToastColor(number&0xFFFFFF);
            this.rainbow = ((number&0x1000000)!=0);
        }

        public ToastColor getColor() {
            return this.value;
        }

        @Override
        public boolean getRainbow() {
            return this.rainbow;
        }

        @Override
        public void setValue(Color value) {
            setValue(getRainbow(), new ToastColor(value));
        }

        @Override
        public void setRainbow(boolean rainbow) {
            this.rainbow=rainbow;
        }

        public ColorSetting onChanged(Consumer<ColorSetting> listener){
            setChangedListener(listener);
            return this;
        }
    }

    public static class Group extends Setting<Group> {

        List<Setting<?>> settings;
        boolean expanded;

        public Group(final String name, final Module parent, final Setting<?>... settings) {
            super(name, parent, parent.getCategory(), Type.GROUP);
            this.settings = new ArrayList<>();
            for(Setting<?> setting : settings){
                setting.setGrouped(true);
                this.settings.add(setting);
            }
        }

        public Group(final String name, final Module parent, final List<Setting<?>> settings) {
            super(name, parent, parent.getCategory(), Type.GROUP);
            this.settings = new ArrayList<>();
            for(Setting<?> setting : settings){
                setting.setGrouped(true);
                this.settings.add(setting);
            }
        }

        public List<Setting<?>> getSettings() {
            return settings;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        public boolean isExpanded() {
            return expanded;
        }
    }

//    public static class KeyBind extends Setting<KeyBind> implements KeySetting {
//
//        private final Module module;
//
//        public KeyBind(final Module parent) {
//            super("Bind", parent, parent.getCategory(), Type.BIND);
//            this.module = parent;
//        }
//
//        @Override
//        public int getPriority() {
//            return 1;
//        }
//
//        @Override
//        public InputUtil.Key getKey() {
//            return module.getKey();
//        }
//
//        @Override
//        public String getKeyName() {
//            switch (module.key.getCode()) {
//                case GLFW.GLFW_KEY_UNKNOWN: return "NONE";
//                case GLFW.GLFW_KEY_ESCAPE: return "ESC";
//                case GLFW.GLFW_KEY_PRINT_SCREEN: return "PRINTSCRN";
//                case GLFW.GLFW_KEY_PAUSE: return "PAUSE";
//                case GLFW.GLFW_KEY_INSERT: return "INSERT";
//                case GLFW.GLFW_KEY_DELETE: return "DELETE";
//                case GLFW.GLFW_KEY_HOME: return "HOME";
//                case GLFW.GLFW_KEY_PAGE_UP: return "PAGE UP";
//                case GLFW.GLFW_KEY_PAGE_DOWN: return "PAGE DOWN";
//                case GLFW.GLFW_KEY_END: return "END";
//                case GLFW.GLFW_KEY_TAB: return "TAB";
//                case GLFW.GLFW_KEY_LEFT_CONTROL: return "LCTRL";
//                case GLFW.GLFW_KEY_RIGHT_CONTROL: return "RCTRL";
//                case GLFW.GLFW_KEY_LEFT_ALT: return "LALT";
//                case GLFW.GLFW_KEY_RIGHT_ALT: return "RALT";
//                case GLFW.GLFW_KEY_LEFT_SHIFT: return "LSHIFT";
//                case GLFW.GLFW_KEY_RIGHT_SHIFT: return "RSHIFT";
//                case GLFW.GLFW_KEY_UP: return "UP";
//                case GLFW.GLFW_KEY_DOWN: return "DOWN";
//                case GLFW.GLFW_KEY_LEFT: return "LEFT";
//                case GLFW.GLFW_KEY_RIGHT: return "RIGHT";
//                case GLFW.GLFW_KEY_APOSTROPHE: return "APOSTROPHE";
//                case GLFW.GLFW_KEY_BACKSPACE: return "BACKSPACE";
//                case GLFW.GLFW_KEY_CAPS_LOCK: return "CAPSLOCK";
//                case GLFW.GLFW_KEY_MENU: return "MENU";
//                case GLFW.GLFW_KEY_LEFT_SUPER: return "LSUPER";
//                case GLFW.GLFW_KEY_RIGHT_SUPER: return "RSUPER";
//                case GLFW.GLFW_KEY_ENTER: return "ENTER";
//                case GLFW.GLFW_KEY_NUM_LOCK: return "NUMLOCK";
//                case GLFW.GLFW_KEY_SPACE: return "SPACE";
//                case GLFW.GLFW_KEY_F1: return "F1";
//                case GLFW.GLFW_KEY_F2: return "F2";
//                case GLFW.GLFW_KEY_F3: return "F3";
//                case GLFW.GLFW_KEY_F4: return "F4";
//                case GLFW.GLFW_KEY_F5: return "F5";
//                case GLFW.GLFW_KEY_F6: return "F6";
//                case GLFW.GLFW_KEY_F7: return "F7";
//                case GLFW.GLFW_KEY_F8: return "F8";
//                case GLFW.GLFW_KEY_F9: return "F9";
//                case GLFW.GLFW_KEY_F10: return "F10";
//                case GLFW.GLFW_KEY_F11: return "F11";
//                case GLFW.GLFW_KEY_F12: return "F12";
//                case GLFW.GLFW_KEY_F13: return "F13";
//                case GLFW.GLFW_KEY_F14: return "F14";
//                case GLFW.GLFW_KEY_F15: return "F15";
//                case GLFW.GLFW_KEY_F16: return "F16";
//                case GLFW.GLFW_KEY_F17: return "F17";
//                case GLFW.GLFW_KEY_F18: return "F18";
//                case GLFW.GLFW_KEY_F19: return "F19";
//                case GLFW.GLFW_KEY_F20: return "F20";
//                case GLFW.GLFW_KEY_F21: return "F21";
//                case GLFW.GLFW_KEY_F22: return "F22";
//                case GLFW.GLFW_KEY_F23: return "F23";
//                case GLFW.GLFW_KEY_F24: return "F24";
//                case GLFW.GLFW_KEY_F25: return "F25";
//                default:
//                    String keyName = GLFW.glfwGetKeyName(module.key.getCode(), -1);
//                    if (keyName == null) return "NONE";
//                    return keyName.toUpperCase();
//            }
//        }
//
//        @Override
//        public void setKey(InputUtil.Key key) {
//            module.setKey(key);
//        }
//    }
}
