package dev.toastmc.client.properties;

import java.lang.reflect.Field;

/**
 * An abstracted form of {@link IProperty}.
 *
 * @author Kix
 * Created in Mar 2019
 */
public abstract class AbstractProperty<T> implements IProperty<T> {

    /**
     * The property's label.
     */
    private final String label;

    /**
     * The object that contains the property.
     */
    private final Object parentObject;

    /**
     * The reflections form of the value.
     *
     * <p>
     * Later in this class, we will grab the information from this field.
     * </p>
     */
    private final Field value;

    public AbstractProperty(String label, Object parentObject, Field value) {
        this.label = label;
        this.parentObject = parentObject;
        this.value = value;
    }

    @Override
    public T getValue() {
        Object foundValue = null;
        boolean accessible = value.isAccessible();
        value.setAccessible(true);

        try {
            foundValue = value.get(parentObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        value.setAccessible(accessible);
        return (T) foundValue;
    }

    @Override
    public void setValue(T value) {
        boolean accessible = this.value.isAccessible();
        this.value.setAccessible(true);

        try {
            this.value.set(parentObject, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        this.value.setAccessible(accessible);
    }

    @Override
    public String getLabel() {
        return label;
    }
}
