package dev.toastmc.client.properties.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Clamps a property with a minimum and maximum.
 *
 * @author Kix
 * Created in Mar 2019
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Clamp {

    /**
     * @return The minimum value the property can have.
     */
    String minimum() default "0";

    /**
     * @return The maximum value the property can have.
     */
    String maximum() default "20";
}
