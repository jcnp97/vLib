package asia.virtualmc.vLib.integration.packet_events;

import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerInitializeWorldBorder;
import org.bukkit.entity.Player;

public class PacketWorldUtils {
    private static final PlayerManager pm = PacketEvents.getAPI().getPlayerManager();

    /**
     * Sends a fake world border to a specific player.
     *
     * @param player        the player to send the border to
     * @param centerX       border center X
     * @param centerZ       border center Z
     * @param size          border diameter
     */
    public static void createWorldBorder(Player player, double centerX, double centerZ, double size) {
        WrapperPlayServerInitializeWorldBorder packet =
                new WrapperPlayServerInitializeWorldBorder(
                        centerX,
                        centerZ,
                        size,
                        size,
                        0L,
                        29999984,
                        0,
                        0
                );
        pm.sendPacket(player, packet);
    }

    /**
     * Sends fake weather to a specific player using EnumsLib's WeatherType.
     *
     * @param player   the player
     * @param type     the weather type
     */
    public static void createWeather(Player player, EnumsLib.WeatherType type) {
        if (type == EnumsLib.WeatherType.CLEAR) {
            // Stop raining
            pm.sendPacket(player,
                    new WrapperPlayServerChangeGameState(
                            WrapperPlayServerChangeGameState.Reason.END_RAINING, 0f));

            // Reset levels
            pm.sendPacket(player,
                    new WrapperPlayServerChangeGameState(
                            WrapperPlayServerChangeGameState.Reason.RAIN_LEVEL_CHANGE, 0f));
            pm.sendPacket(player,
                    new WrapperPlayServerChangeGameState(
                            WrapperPlayServerChangeGameState.Reason.THUNDER_LEVEL_CHANGE, 0f));

        } else if (type == EnumsLib.WeatherType.RAIN) {
            // Start raining
            pm.sendPacket(player,
                    new WrapperPlayServerChangeGameState(
                            WrapperPlayServerChangeGameState.Reason.BEGIN_RAINING, 0f));

            // Full rain
            pm.sendPacket(player,
                    new WrapperPlayServerChangeGameState(
                            WrapperPlayServerChangeGameState.Reason.RAIN_LEVEL_CHANGE, 1.0f));
        } else if (type == EnumsLib.WeatherType.THUNDER) {
            // Start raining
            pm.sendPacket(player,
                    new WrapperPlayServerChangeGameState(
                            WrapperPlayServerChangeGameState.Reason.BEGIN_RAINING, 0f));

            // Full rain
            pm.sendPacket(player,
                    new WrapperPlayServerChangeGameState(
                            WrapperPlayServerChangeGameState.Reason.RAIN_LEVEL_CHANGE, 1.0f));

            // Full thunder
            pm.sendPacket(player,
                    new WrapperPlayServerChangeGameState(
                            WrapperPlayServerChangeGameState.Reason.THUNDER_LEVEL_CHANGE, 1.0f));
        }
    }
}