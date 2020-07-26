package dev.toastmc.client.properties.annotations.child;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author auto on 4/8/2020
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Child {

    /**
     * @return The boolean that has to be true.
     */
    String value();

    /**
     * @return The field name for modes.
     */
    String mode() default "";

}
