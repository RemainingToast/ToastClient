package dev.toastmc.client.properties.impl;


import dev.toastmc.client.properties.AbstractProperty;
import dev.toastmc.client.properties.IProperty;

import java.lang.reflect.Field;

/**
 * An implementation of {@link IProperty} for booleans.
 *
 * @author Kix
 * Created in Mar 2019
 */
public class BooleanProperty extends AbstractProperty<Boolean> {

    public BooleanProperty(String label, Object parentObject, Field value) {
        super(label, parentObject, value);
    }

    @Override
    public void setValue(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on")) {
            setValue(true);
        } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("off")) {
            setValue(false);
        }
    }
}
