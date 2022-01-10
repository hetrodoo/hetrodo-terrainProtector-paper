package dev.hetrodo.terrainprotector.behaviours;

import dev.hetrodo.terrainprotector.dataTypes.classes.OldBlock;
import dev.hetrodo.terrainprotector.dataTypes.classes.Vector3;
import dev.hetrodo.terrainprotector.misc.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AreaExplosionBehaviour {
    public static void Handle(Location location, List<Block> blockList) {
        World world = location.getWorld();
        List<OldBlock> oldBlocks = new ArrayList<>();

        blockList.forEach(block -> {
            oldBlocks.add(new OldBlock(block.getType().name(), new Vector3(block.getX(), block.getY(), block.getZ())));
            block.setType(Material.AIR);
        });
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);

        oldBlocks.sort(Comparator.comparingInt(o -> o.position.y));
        if (oldBlocks.size() > 0) AreaExplosionBehaviour.RevertBlockChange(world, oldBlocks, 0);
    }

    private static void RevertBlockChange(World world, List<OldBlock> oldBlockList, int index) {
        OldBlock oldBlock = oldBlockList.get(index);

        Material material = Material.getMaterial(oldBlock.material);

        if (material != null) {
            Block block = world.getBlockAt(oldBlock.position.x, oldBlock.position.y, oldBlock.position.z);
            if (block.getType().equals(Material.AIR)) block.setType(material);
        }

        if (index < oldBlockList.size() - 1) {
            Util.ExecuteRunnable(() -> {
                AreaExplosionBehaviour.RevertBlockChange(world, oldBlockList, index + 1);
                return true;
            }, 1L);
        }
    }
}
