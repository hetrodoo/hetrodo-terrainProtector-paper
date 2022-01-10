package dev.hetrodo.terrainprotector.misc;

import dev.hetrodo.terrainprotector.TerrainProtector;
import dev.hetrodo.terrainprotector.dataTypes.enums.MsgType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class Util {
    public static final String Prefix = "[TerrainProtector]";
    public static final UUID NIL_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static void SendToPlayer(Player player, MsgType type, String text) {
        String result = text;

        switch (type) {
            case Info:
                result = "§b" + Prefix + "[Info]: " + result;
                break;
            case Error:
                result = "§c" + Prefix + "[Error]: " + result;
                break;
            case Warning:
                result = "§e" + Prefix + "[Warning]: " + result;
                break;
            case Success:
                result = "§a" + Prefix + "[Success]: " + result;
                break;
        }

        player.sendMessage(result);
    }

    public static void ExecuteRunnable(@NotNull Callable<Boolean> func, @NotNull Long delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!func.call()) {
                        throw new Exception("Runnable execution failed.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(TerrainProtector.Instance, delay);
    }

    public static <T> T Or(Supplier<@Nullable T> v0, Supplier<@Nullable T> v1) {
        @Nullable T value = v0.get();

        if (value == null) {
            value = v1.get();
        }

        return value;
    }

    public static <T> void Println(T x) {
        System.out.println(Prefix + " " + x);
    }
}
