package dev.hetrodo.terrainprotector.behaviours;

import dev.hetrodo.terrainprotector.dataTypes.classes.Area;
import dev.hetrodo.terrainprotector.dataTypes.classes.OldBlock;
import dev.hetrodo.terrainprotector.dataTypes.classes.Vector3;
import dev.hetrodo.terrainprotector.misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ClaimManager {
    private final List<Area> areas;
    private final String worldName = "World";
    private final Material delimiterBlock = Material.POLISHED_ANDESITE;

    public ClaimManager() {
        areas = new ArrayList<>();
    }

    public boolean IsInsideUnauthorizedArea(Vector3 position) {
        return this.IsInsideUnauthorizedArea(position, Util.NIL_UUID.toString());
    }

    public boolean IsInsideUnauthorizedArea(Vector3 position, String uuid) {
        for (Area area : areas) {
            if (area.isInside(position)) {
                return !area.owner.equals(uuid) && area.members.stream().noneMatch(member -> member.equals(uuid));
            }
        }

        return false;
    }

    public boolean CreateArea(Vector3 center, String uuid, int size) {
        World world = Bukkit.getServer().getWorld(worldName);

        if (world == null)
            return false;

        Vector3 a = center.subtract(size, 0, size);
        Vector3 b = center.add(size, 0, size);

        Area newArea = new Area(a, b, uuid, size, center);

        if (this.ValidateNewArea(newArea, uuid)) {
            areas.add(newArea);

            for (int x = newArea.a.x; x < newArea.b.x + 1; x++) {
                for (int z = newArea.a.z; z < newArea.b.z + 1; z++) {
                    if (x == newArea.a.x || x == newArea.b.x || z == newArea.a.z || z == newArea.b.z) {
                        int y = world.getHighestBlockAt(x, z).getY();
                        Block block = world.getBlockAt(x, y, z);

                        String id = block.getBlockData().getMaterial().name();
                        newArea.oldBlockList.add(new OldBlock(id, new Vector3(x, y, z)));

                        block.setType(delimiterBlock);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean FreeArea(Vector3 position, String uuid) {
        World world = Bukkit.getServer().getWorld(worldName);

        if (world == null)
            return false;

        for (int i = 0; i < areas.size(); i++) {
            Area area = areas.get(i);
            boolean ownArea = area.isInside(position) && area.owner.equals(uuid);

            if (ownArea) {
                for (OldBlock blockData : area.oldBlockList) {
                    Block block = world.getBlockAt(blockData.position.x, blockData.position.y, blockData.position.z);
                    String blockMaterial = block.getBlockData().getMaterial().name();

                    if (blockMaterial.equals(delimiterBlock.name())) {
                        Material material = Material.getMaterial(blockData.material);

                        if (material != null) {
                            block.setType(material);
                        }
                    }
                }

                areas.remove(i);
                return true;
            }
        }

        return false;
    }

    public boolean AddMember(Vector3 position, String uuid, String member) {
        for (Area area : areas) {
            if (area.isInside(position)) {
                if (area.owner.equals(uuid)) {
                    if (area.members.stream().noneMatch(value -> value.equals(member))) {
                        area.members.add(member);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    public boolean HasMember(Vector3 position, String member) {
        for (Area area : areas) {
            if (area.isInside(position)) {
                return area.members.stream().anyMatch(value -> value.equals(member));
            }
        }

        return false;
    }

    public boolean RemoveMember(Vector3 position, String uuid, String member) {
        for (Area area : areas) {
            if (area.isInside(position)) {
                if (area.owner.equals(uuid)) {
                    if (area.members.stream().anyMatch(value -> value.equals(member))) {
                        area.members.remove(member);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    public double NearestAreaDistance(Vector3 position) {
        double distance = Double.MAX_VALUE;
        int size = 0;

        for (Area area : areas) {
            double dist = area.Distance(position);
            if (dist < distance) {
                distance = dist;
                size = area.size;
            }
        }

        return distance - size;
    }

    private boolean ValidateNewArea(Area newArea, String uuid) {
        AtomicInteger claimCount = new AtomicInteger(0);

        return areas.stream().noneMatch(area -> {
            if (area.owner.equals(uuid)) claimCount.incrementAndGet();
            return newArea.isInside(area.a) || newArea.isInside(area.b) || claimCount.get() >= 5;
        });
    }

    public void Save() {
        try {
            YamlConfiguration countData = new YamlConfiguration();
            countData.set("count", areas.size());
            countData.save("plugins/TerrainProtector/count.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.areas.forEach(area -> {
            YamlConfiguration areaData = new YamlConfiguration();
            areaData.set("owner", area.owner);

            areaData.set("a.x", area.a.x);
            areaData.set("a.y", area.a.y);
            areaData.set("a.z", area.a.z);

            areaData.set("b.x", area.b.x);
            areaData.set("b.y", area.b.y);
            areaData.set("b.z", area.b.z);

            areaData.set("members", String.join(",", area.members));

            areaData.set("size", area.size);

            areaData.set("center.x", area.center.x);
            areaData.set("center.y", area.center.y);
            areaData.set("center.z", area.center.z);

            areaData.set("oldBlockListSize", area.oldBlockList.size());

            area.oldBlockList.forEach(oldBlock -> {
                String propName = "oldBlockList." + area.oldBlockList.indexOf(oldBlock) + ".";
                areaData.set(propName + "material", oldBlock.material);

                areaData.set(propName + "position.x", oldBlock.position.x);
                areaData.set(propName + "position.y", oldBlock.position.y);
                areaData.set(propName + "position.z", oldBlock.position.z);
            });

            try {
                areaData.save("plugins/TerrainProtector/" + this.areas.indexOf(area) + ".yml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void Load() {
        try {
            YamlConfiguration countData = new YamlConfiguration();
            countData.load("plugins/TerrainProtector/count.yml");
            int count = countData.getInt("count");

            areas.clear();
            System.out.println("[TerrainProtector]: Found " + count + " area(s).");

            for (int i = 0; i < count; i++) {
                try {
                    YamlConfiguration areaData = new YamlConfiguration();
                    areaData.load("plugins/TerrainProtector/" + i + ".yml");

                    String owner = areaData.getString("owner");

                    int aX = areaData.getInt("a.x");
                    int aY = areaData.getInt("a.y");
                    int aZ = areaData.getInt("a.z");

                    int bX = areaData.getInt("b.x");
                    int bY = areaData.getInt("b.y");
                    int bZ = areaData.getInt("b.z");

                    String members = areaData.getString("members");

                    int size = areaData.getInt("size");

                    int centerX = areaData.getInt("center.x");
                    int centerY = areaData.getInt("center.y");
                    int centerZ = areaData.getInt("center.z");

                    Area newArea = new Area(new Vector3(aX, aY, aZ), new Vector3(bX, bY, bZ), owner, size, new Vector3(centerX, centerY, centerZ));

                    if (members != null) {
                        newArea.members.addAll(Arrays.asList(members.split(",")));
                    }

                    for (int j = 0; j < areaData.getInt("oldBlockListSize"); j++) {
                        String propName = "oldBlockList." + j + ".";
                        String material = areaData.getString(propName + "material");

                        int positionX = areaData.getInt(propName + "position.x");
                        int positionY = areaData.getInt(propName + "position.y");
                        int positionZ = areaData.getInt(propName + "position.z");

                        newArea.oldBlockList.add(new OldBlock(material, new Vector3(positionX, positionY, positionZ)));
                    }

                    System.out.println("[TerrainProtector]: An area from " + owner + " was loaded.");
                    areas.add(newArea);
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
