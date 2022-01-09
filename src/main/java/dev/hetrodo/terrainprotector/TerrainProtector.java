package dev.hetrodo.terrainprotector;

import dev.hetrodo.terrainprotector.behaviours.ClaimManager;
import dev.hetrodo.terrainprotector.events.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TerrainProtector extends JavaPlugin {
    public static final ClaimManager CLAIM_MANAGER = new ClaimManager();

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this);
        TerrainProtector.CLAIM_MANAGER.Load();
    }

    @Override
    public void onDisable() {
        TerrainProtector.CLAIM_MANAGER.Save();
    }
}
