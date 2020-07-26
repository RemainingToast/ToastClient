package dev.toastmc.client.properties.impl.string;


import dev.toastmc.client.properties.AbstractProperty;
import dev.toastmc.client.properties.IProperty;

import java.lang.reflect.Field;

/**
 * An implementation of {@link IProperty} for strings.
 *
 * @author Kix
 * Created in Mar 2019
 */
public class StringProperty extends AbstractProperty<String> {

    public StringProperty(String label, Object parentObject, Field value) {
        super(label, parentObject, value);
    }
}
