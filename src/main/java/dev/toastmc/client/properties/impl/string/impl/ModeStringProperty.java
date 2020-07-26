package dev.toastmc.client.properties.impl.string.impl;

import dev.toastmc.client.properties.impl.string.StringProperty;

import java.lang.reflect.Field;

/**
 * A form of {@link StringProperty} for modes.
 *
 * @author Kix
 * Created in Mar 2019
 */
public class ModeStringProperty extends StringProperty {

    /**
     * The possible modes that the property can be.
     */
    private final String[] modes;

    public ModeStringProperty(String label, Object parentObject, Field value, String[] modes) {
        super(label, parentObject, value);
        this.modes = modes;
    }

    @Override
    public void setValue(String value) {
        for (String mode : modes) {
            if (value.equalsIgnoreCase(mode)) {
                super.setValue(value);
            }else{
                super.setValue(this.getValue());
            }
        }
    }

    public String[] getModes() {
        return modes;
    }

    public void increment() {
        String currentMode = getValue();

        for (String mode : modes) {
            if (!mode.equalsIgnoreCase(currentMode)) {
                continue;
            }

            String newValue;

            int ordinal = getOrdinal(mode, modes);
            if (ordinal == modes.length - 1) {
                newValue = modes[0];
            } else {
                newValue = modes[ordinal + 1];
            }

            setValue(newValue);
            return;
        }
    }

    public void decrement() {
        String currentMode = getValue();

        for (String mode : modes) {
            if (!mode.equalsIgnoreCase(currentMode)) {
                continue;
            }

            String newValue;

            int ordinal = getOrdinal(mode, modes);
            if (ordinal == 0) {
                newValue = modes[modes.length - 1];
            } else {
                newValue = modes[ordinal - 1];
            }

            setValue(newValue);
            return;
        }
    }

    private int getOrdinal(String value, String[] array) {
        for (int i = 0; i <= array.length - 1; i++) {
            String indexString = array[i];
            if (indexString.equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }

}
