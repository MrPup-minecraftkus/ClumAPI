package com.mrpup.clumapi.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigVal {
    String comment() default "";

    String value() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface InRangeInt {
        int max() default Integer.MAX_VALUE;

        int min() default Integer.MIN_VALUE;
    }
}
