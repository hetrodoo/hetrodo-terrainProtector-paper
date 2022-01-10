package dev.hetrodo.config;

import dev.hetrodo.terrainprotector.misc.Util;

import java.util.function.Supplier;

public class Config extends YamlFile {
    public Config(String pathName) {
        super(pathName);
    }

    protected static <V, S extends YamlFile> Supplier<V> buildSupplier(Class<? extends V> clazz, S storage, String property, V defaultValue) {
        Supplier<V> supplier = () -> {
            V value = storage.getGeneric(clazz, property);

            if (value == null) {
                value = defaultValue;
                storage.set(property, value);
                storage.save();
            }

            return value;
        };

        Util.Println("Preloading property: " + property + " = " + supplier.get());
        return supplier;
    }
}
