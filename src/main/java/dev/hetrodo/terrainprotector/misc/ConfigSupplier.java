package dev.hetrodo.terrainprotector.misc;

import dev.hetrodo.config.Config;

import java.util.function.Supplier;

public class ConfigSupplier extends Config {
    public final Supplier<String> CurrencyMaterial;
    public final Supplier<Integer> MaxClaims;
    public final Supplier<Integer> SizeFactor;
    public final Supplier<String> ClaimBlock;

    public ConfigSupplier(String pathName) {
        super(pathName);

        this.CurrencyMaterial = Config.buildSupplier(String.class, this, "CurrencyMaterial", "IRON_INGOT");
        this.MaxClaims = Config.buildSupplier(Integer.class, this, "MaxClaims", 5);
        this.SizeFactor = Config.buildSupplier(Integer.class, this, "SizeFactor", 2);
        this.ClaimBlock = Config.buildSupplier(String.class, this, "ClaimBlock", "WHITE_BANNER");
    }
}
