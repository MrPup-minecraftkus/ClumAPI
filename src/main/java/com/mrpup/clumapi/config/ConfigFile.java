package com.mrpup.clumapi.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfig.Type;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ConfigFile {
    ModConfig.Type type() default Type.COMMON;

    String value() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface Child {
        Class value();
    }
}
