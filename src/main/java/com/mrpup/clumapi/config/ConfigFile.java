package com.mrpup.clumapi.config;

import net.neoforged.fml.config.ModConfig.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ConfigFile {
    Type type() default Type.COMMON;

    String value() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface Child {
        Class value();
    }
}
