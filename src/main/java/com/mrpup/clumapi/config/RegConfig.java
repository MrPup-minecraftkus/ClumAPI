package com.mrpup.clumapi.config;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class RegConfig {

    private interface FieldBinding {
        void apply();
    }

    public static void init(String modId, IEventBus modEventBus) {
        IModFileInfo modFileInfo = ModList.get().getModFileById(modId);

        if (modFileInfo == null) {
            throw new IllegalStateException("Could not find mod file for modId " + modId);
        }

        ModFileScanData scanData = modFileInfo.getFile().getScanResult();

        List<Class<?>> rootConfigClasses = new ArrayList<>();

        Map<String, List<Class<?>>> childrenByParent = new HashMap<>();

        for (ModFileScanData.AnnotationData annotationData : scanData.getAnnotations()) {
            String annotationName = annotationData.annotationType().getClassName();
            String className = annotationData.clazz().getClassName();

            if (annotationName.equals(ConfigFile.class.getName())) {
                rootConfigClasses.add(loadClass(className));

            } else if (annotationName.equals(ConfigFile.Child.class.getName())) {
                Type parentType = (Type) annotationData.annotationData().get("value");
                String parentClassName = parentType.getClassName();

                childrenByParent
                        .computeIfAbsent(parentClassName, k -> new ArrayList<>())
                        .add(loadClass(className));
            }
        }

        for (Class<?> rootClass : rootConfigClasses) {
            register(rootClass, childrenByParent, modId, modEventBus);
        }
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className, false, RegConfig.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load scanned config class " + className, e);
        }
    }

    private static void register(Class<?> rootClass, Map<String, List<Class<?>>> childrenByParent,
                                 String modId, IEventBus modEventBus) {
        ConfigFile fileAnnotation = rootClass.getAnnotation(ConfigFile.class);
        if (fileAnnotation == null) {
            throw new IllegalArgumentException("Class " + rootClass.getName() + " is missing @ConfigFile");
        }

        ModConfig.Type type = fileAnnotation.type();
        String rawName = fileAnnotation.value().isEmpty()
                ? modId + "-" + type.name().toLowerCase()
                : fileAnnotation.value();

        String fileName = rawName.endsWith(".toml") ? rawName : rawName + ".toml";

        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        List<FieldBinding> bindings = new ArrayList<>();

        processFields(rootClass, builder, bindings);

        List<Class<?>> children = childrenByParent.get(rootClass.getName());
        if (children != null) {
            for (Class<?> child : children) {
                builder.push(child.getSimpleName());
                processFields(child, builder, bindings);
                builder.pop();
            }
        }

        ModConfigSpec spec = builder.build();
        ModLoadingContext.get().getActiveContainer().registerConfig(type, spec, fileName);

        modEventBus.addListener((ModConfigEvent.Loading event) -> {
            if (event.getConfig().getSpec() == spec) bindings.forEach(FieldBinding::apply);
        });
        modEventBus.addListener((ModConfigEvent.Reloading event) -> {
            if (event.getConfig().getSpec() == spec) bindings.forEach(FieldBinding::apply);
        });
    }

    private static void processFields(Class<?> clazz, ModConfigSpec.Builder builder, List<FieldBinding> bindings) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;

            ConfigVal configVal = field.getAnnotation(ConfigVal.class);
            if (configVal != null) {
                bindField(field, configVal, builder, bindings);
            }
        }
    }

    private static void bindField(Field field, ConfigVal configVal, ModConfigSpec.Builder builder, List<FieldBinding> bindings) {
        field.setAccessible(true);

        String key = configVal.value().isEmpty() ? field.getName() : configVal.value();
        String comment = configVal.comment();

        if (!comment.isEmpty()) {
            builder.comment(comment);
        }

        Class<?> fieldType = field.getType();

        try {
            if (fieldType == int.class || fieldType == Integer.class) {
                int defaultValue = (int) field.get(null);
                ConfigVal.InRangeInt range = field.getAnnotation(ConfigVal.InRangeInt.class);
                int min = range != null ? range.min() : Integer.MIN_VALUE;
                int max = range != null ? range.max() : Integer.MAX_VALUE;

                ModConfigSpec.IntValue value = builder.defineInRange(key, defaultValue, min, max);
                bindings.add(() -> setField(field, value.get()));

            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                boolean defaultValue = (boolean) field.get(null);
                ModConfigSpec.BooleanValue value = builder.define(key, defaultValue);
                bindings.add(() -> setField(field, value.get()));

            } else if (fieldType == String.class) {
                String defaultValue = (String) field.get(null);
                ModConfigSpec.ConfigValue<String> value = builder.define(key, defaultValue);
                bindings.add(() -> setField(field, value.get()));

            } else if (fieldType.isEnum()) {
                bindEnumField(field, key, builder, bindings);

            } else if (List.class.isAssignableFrom(fieldType)) {
                @SuppressWarnings("unchecked")
                List<String> defaultValue = (List<String>) field.get(null);

                ModConfigSpec.ConfigValue<List<? extends String>> value =
                        builder.defineListAllowEmpty(key, defaultValue, () -> "", obj -> obj instanceof String);
                bindings.add(() -> setField(field, new ArrayList<>(value.get())));

            } else {
                throw new IllegalArgumentException("Unsupported @ConfigVal field type: " + fieldType.getName() + " on " + field);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to read default value for config field " + field, e);
        }
    }

    private static <V extends Enum<V>> void bindEnumField(
            Field field, String key, ModConfigSpec.Builder builder, List<FieldBinding> bindings) throws IllegalAccessException {

        @SuppressWarnings("unchecked")
        V defaultValue = (V) field.get(null);

        ModConfigSpec.EnumValue<V> value = builder.defineEnum(key, defaultValue);
        bindings.add(() -> setField(field, value.get()));
    }

    private static void setField(Field field, Object value) {
        try {
            field.set(null, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set config field " + field, e);
        }
    }
}