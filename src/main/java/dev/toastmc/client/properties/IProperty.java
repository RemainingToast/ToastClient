package dev.toastmc.client.properties;

/**
 * A value for a module or class.
 *
 * <p>
 * These can be numbers, booleans, enums.
 * </p>
 *
 * @author Kix
 * Created in Mar 2019
 */
public interface IProperty<T> {

    /**
     * @return The property's label.
     */
    String getLabel();

    /**
     * @return The property's current value.
     */
    T getValue();

    /**
     * Changes the property's current value.
     *
     * @param value The new value.
     */
    void setValue(T value);

    /**
     * Changes the property's current value with a string.
     *
     * @param value The new value to be parsed.
     */
    void setValue(String value);
}
