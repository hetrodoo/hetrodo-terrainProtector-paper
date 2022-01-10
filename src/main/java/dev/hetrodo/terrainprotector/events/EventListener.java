package dev.hetrodo.terrainprotector.events;

import dev.hetrodo.terrainprotector.TerrainProtector;
import dev.hetrodo.terrainprotector.behaviours.ClaimBlockBehaviour;
import dev.hetrodo.terrainprotector.dataTypes.classes.Vector3;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;

public class EventListener implements Listener {
    private static final Material ClaimBlock = Material.getMaterial(TerrainProtector.CONFIG_SUPPLIER.ClaimBlock.get());

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (block != null) {
            Player player = event.getPlayer();
            Vector3 position = Vector3.FromBlock(block);
            boolean result = ClaimBlockBehaviour.validateInteractionEvent(position, player, block, event.getItem());
            event.setCancelled(result);
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent event) {
        if (event.getBlock().getType().equals(ClaimBlock)) {
            boolean result = ClaimBlockBehaviour.validateDestroyEvent(event.getPlayer(), Vector3.FromBlock(event.getBlock()));
            event.setCancelled(result);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        Vector3 position = Vector3.FromBlock(event.getBlock());
        boolean result = ClaimBlockBehaviour.validateExplosionEvent(position, event.getBlock().getLocation(), event.blockList());
        event.setCancelled(result);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Vector3 position = Vector3.FromBlock(event.getEntity().getLocation().getBlock());
        boolean result = ClaimBlockBehaviour.validateExplosionEvent(position, event.getLocation(), event.blockList());
        event.setCancelled(result);
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        if (event.getWorld().getName().equals("World")) {
            TerrainProtector.CLAIM_MANAGER.Save();
        }
    }
}
