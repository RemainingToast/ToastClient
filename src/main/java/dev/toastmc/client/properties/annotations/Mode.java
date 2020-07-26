package dev.toastmc.client.properties.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows us to mark a property as a mode.
 *
 * @author Kix
 * Created in Mar 2019
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mode {

    /**
     * @return The modes that the property can be.
     */
    String[] value();

}
