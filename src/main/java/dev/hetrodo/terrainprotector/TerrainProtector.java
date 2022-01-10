package dev.hetrodo.terrainprotector;

import dev.hetrodo.terrainprotector.behaviours.ClaimManager;
import dev.hetrodo.terrainprotector.events.EventListener;
import dev.hetrodo.terrainprotector.misc.ConfigSupplier;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TerrainProtector extends JavaPlugin {
    public static final ConfigSupplier CONFIG_SUPPLIER = new ConfigSupplier("TerrainProtector");
    public static final ClaimManager CLAIM_MANAGER = new ClaimManager();
    public static TerrainProtector Instance;

    @Override
    public void onEnable() {
        Instance = this;
        TerrainProtector.CLAIM_MANAGER.Load();
        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        TerrainProtector.CLAIM_MANAGER.Save();
    }
}
