package dev.hetrodo.terrainprotector.behaviours;

import dev.hetrodo.terrainprotector.TerrainProtector;
import dev.hetrodo.terrainprotector.dataTypes.classes.Vector3;
import dev.hetrodo.terrainprotector.dataTypes.enums.MsgType;
import dev.hetrodo.terrainprotector.misc.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ClaimBlockBehaviour {
    private static final Material Currency = Material.getMaterial(TerrainProtector.CONFIG_SUPPLIER.CurrencyMaterial.get());
    private static final Material ClaimBlock = Material.getMaterial(TerrainProtector.CONFIG_SUPPLIER.ClaimBlock.get());

    public static boolean validateInteractionEvent(Vector3 position, @NotNull Player player, Block block, @Nullable ItemStack itemStack) {
        String uuid = player.getUniqueId().toString();

        boolean isUnauthorized = TerrainProtector.CLAIM_MANAGER.IsInsideUnauthorizedArea(position, uuid);

        if (isUnauthorized) {
            Util.SendToPlayer(player, MsgType.Info, "Hey " + player.getName() + ", this area is protected!");
            return true;
        } else {
            if (!block.getType().equals(ClaimBlock))
                return false;

            if (itemStack == null)
                return false;

            if (itemStack.getType().equals(Material.PAPER)) {
                ClaimBlockBehaviour.onClickUsingPaper(itemStack, position, player);
            } else if (itemStack.getType().equals(Currency)) {
                onClickUsingCurrency(itemStack, position, player);
            }
        }

        return false;
    }

    public static boolean validateExplosionEvent(Vector3 position, Location location, List<Block> blockList) {
        double distance = TerrainProtector.CLAIM_MANAGER.NearestAreaDistance(position);

        boolean cancelEvent = distance < TerrainProtector.CONFIG_SUPPLIER.ExplosionProtectionDistance.get();
        if (cancelEvent) AreaExplosionBehaviour.Handle(location, blockList);

        return cancelEvent;
    }

    public static boolean validateDestroyEvent(Player player, Vector3 position) {
        if (TerrainProtector.CLAIM_MANAGER.IsInsideUnauthorizedArea(position)) {
            String uuid = player.getUniqueId().toString();

            boolean cancelEvent = !TerrainProtector.CLAIM_MANAGER.FreeArea(position, uuid);

            if (cancelEvent) {
                Util.SendToPlayer(player, MsgType.Error, "A problem has occurred while disbanding this area. Is this area yours?");
            } else {
                Util.SendToPlayer(player, MsgType.Success, "You have successfully disbanded your claimed area.");
            }

            return cancelEvent;
        }

        return false;
    }

    private static void onClickUsingPaper(@NotNull ItemStack itemStack, Vector3 position, Player player) {
        if (!itemStack.hasItemMeta())
            return;

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!itemMeta.hasDisplayName())
            return;

        Component displayNameComponent = itemMeta.displayName();

        if (displayNameComponent == null)
            return;

        String displayName = PlainComponentSerializer.plain().serialize(displayNameComponent);

        Player memberPlayer = Util.Or(
                () -> Bukkit.getServer().getPlayer(displayName),
                () -> {
                    OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayerIfCached(displayName);

                    if (offlinePlayer == null)
                        return null;
                    else
                        return offlinePlayer.getPlayer();
                }
        );

        if (memberPlayer == null) {
            return;
        }

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
    }

    private static void onClickUsingCurrency(@NotNull ItemStack itemStack, Vector3 position, @NotNull Player player) {
        int amount = itemStack.getAmount();
        boolean createResult = TerrainProtector.CLAIM_MANAGER.CreateArea(position, player.getUniqueId().toString(), amount * TerrainProtector.CONFIG_SUPPLIER.SizeFactor.get());

        if (createResult) {
            Util.SendToPlayer(player, MsgType.Success, "You have successfully claimed this area.");
            itemStack.setAmount(0);
        } else {
            Util.SendToPlayer(player, MsgType.Error, "A problem has occurred while claiming this area. Is this area already claimed?");
        }
    }
}
