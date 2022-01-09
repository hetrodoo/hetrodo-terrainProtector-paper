package dev.hetrodo.terrainprotector.events;

import dev.hetrodo.terrainprotector.TerrainProtector;
import dev.hetrodo.terrainprotector.dataTypes.classes.Vector3;
import dev.hetrodo.terrainprotector.dataTypes.enums.MsgType;
import dev.hetrodo.terrainprotector.misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (block != null) {
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            Player player = event.getPlayer();
            Vector3 position = new Vector3(x, y, z);
            String uuid = player.getUniqueId().toString();

            boolean isUnauthorized = TerrainProtector.CLAIM_MANAGER.IsInsideUnauthorizedArea(position, uuid);

            if (isUnauthorized) {
                event.setCancelled(true);
                Util.SendToPlayer(player, MsgType.Info, "Hey " + player.getName() + ", this area is protected!");
            } else {
                ItemStack itemStack = event.getItem();

                if (itemStack == null)
                    return;

                Material material = itemStack.getType();

                if (material.equals(Material.IRON_INGOT)) {
                    int amount = itemStack.getAmount();
                    boolean createResult = TerrainProtector.CLAIM_MANAGER.CreateArea(position, uuid, amount * 2);

                    if (createResult) {
                        Util.SendToPlayer(player, MsgType.Success, "You have successfully claimed this area.");
                        itemStack.setAmount(0);
                    } else {
                        Util.SendToPlayer(player, MsgType.Error, "A problem has occurred while claiming this area. Is this area already claimed?");
                    }

                    return;
                }

                if (material.equals(Material.PAPER) && itemStack.hasItemMeta()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    if (itemMeta.hasDisplayName()) {
                        Player memberPlayer = Bukkit.getServer().getPlayer(itemMeta.getDisplayName());

                        if (memberPlayer != null) {
                            UUID memberUUID = memberPlayer.getUniqueId();
                            boolean hasMember = TerrainProtector.CLAIM_MANAGER.HasMember(position, memberUUID.toString());

                            if (hasMember) {
                                boolean removeResult = TerrainProtector.CLAIM_MANAGER.RemoveMember(position, player.getUniqueId().toString(), memberUUID.toString());

                                if (removeResult) {
                                    Util.SendToPlayer(player, MsgType.Success, "Successfully removed a member from this area!");
                                    itemStack.setAmount(itemStack.getAmount() - 1);
                                } else {
                                    Util.SendToPlayer(player, MsgType.Success, "A problem has occurred while removing a member from area. Are you the owner of this area?");
                                }
                            } else {
                                boolean addResult = TerrainProtector.CLAIM_MANAGER.AddMember(position, player.getUniqueId().toString(), memberUUID.toString());

                                if (addResult) {
                                    Util.SendToPlayer(player, MsgType.Success, "Successfully added a new member to this area!");
                                    itemStack.setAmount(itemStack.getAmount() - 1);
                                } else {
                                    Util.SendToPlayer(player, MsgType.Success, "A problem has occurred while adding a new member to this area. Are you the owner of this area?");
                                }
                            }
                        } else {
                            Util.SendToPlayer(player, MsgType.Error, "Player not found.");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.WHITE_BANNER)) {
            int x = event.getBlock().getX();
            int y = event.getBlock().getY();
            int z = event.getBlock().getZ();
            Vector3 position = new Vector3(x, y, z);

            if (TerrainProtector.CLAIM_MANAGER.IsInsideUnauthorizedArea(position)) {
                Player player = event.getPlayer();
                String uuid = player.getUniqueId().toString();

                boolean freeResult = TerrainProtector.CLAIM_MANAGER.FreeArea(position, uuid);

                if (freeResult) {
                    Util.SendToPlayer(player, MsgType.Success, "You have successfully disbanded your claimed area.");
                } else {
                    event.setCancelled(true);
                    Util.SendToPlayer(player, MsgType.Error, "A problem has occurred while disbanding this area. Is this area yours?");
                }
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        int x = event.getBlock().getX();
        int y = event.getBlock().getY();
        int z = event.getBlock().getZ();
        Vector3 position = new Vector3(x, y, z);

        double distance = TerrainProtector.CLAIM_MANAGER.NearestAreaDistance(position);
        event.setCancelled(distance < 16);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        int x = event.getEntity().getLocation().getBlockX();
        int y = event.getEntity().getLocation().getBlockY();
        int z = event.getEntity().getLocation().getBlockZ();
        Vector3 position = new Vector3(x, y, z);

        double distance = TerrainProtector.CLAIM_MANAGER.NearestAreaDistance(position);
        event.setCancelled(distance < 16);
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        TerrainProtector.CLAIM_MANAGER.Save();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        TerrainProtector.CLAIM_MANAGER.Load();
    }
}
