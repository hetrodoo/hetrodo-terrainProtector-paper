package dev.hetrodo.terrainprotector.misc;

import dev.hetrodo.terrainprotector.dataTypes.enums.MsgType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Util {
    private static final String Prefix = "[TerrainProtector]";
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
}
