package dev.toastmc.client.properties.impl;

import dev.toastmc.client.properties.AbstractProperty;
import dev.toastmc.client.properties.IProperty;
import dev.toastmc.client.utils.MathUtil;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Field;

/**
 * An implementation of {@link IProperty} for numbers.
 *
 * @author Kix
 * Created in Mar 2019
 */
public class NumberProperty<T extends Number> extends AbstractProperty<T> {

    private final Class cls;

    /**
     * The boundaries for the number.
     */
    private final T minimum, maximum;

    public NumberProperty(String label, Object parentObject, Field value, T minimum, T maximum) {
        super(label, parentObject, value);
        this.minimum = minimum;
        this.maximum = maximum;
        cls = value.getType();
    }

    public T getMaximum() {
        return maximum;
    }

    public T getMinimum() {
        return minimum;
    }

    @Override
    public void setValue(T value) {
        super.setValue(MathUtil.clamp(value, minimum, maximum));
    }

    @Override
    public void setValue(String value) {
        if (cls == Integer.class || cls == Integer.TYPE) {
            setValue((T) NumberUtils.createInteger(value));
        } else if (cls == Double.class || cls == Double.TYPE) {
            setValue((T) NumberUtils.createDouble(value));
        } else if (cls == Float.class || cls == Float.TYPE) {
            setValue((T) NumberUtils.createFloat(value));
        } else if (cls == Long.class || cls == Long.TYPE) {
            setValue((T) NumberUtils.createLong(value));
        }
    }
}
