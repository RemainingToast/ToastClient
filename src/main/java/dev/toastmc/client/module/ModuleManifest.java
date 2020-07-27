package dev.toastmc.client.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleManifest {
    String label();
    String[] aliases() default {};
    String description() default "";
    String usage() default "";

    boolean hidden() default false;
    int key() default -1;
}
