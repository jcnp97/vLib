//package asia.virtualmc.vLib.utilities.messages;
//
//
//import java.util.Collections;
//
//public class MiscUtils {
//
//    /**
//     * Sends a toast message to a player by creating a temporary advancement.
//     *
//     * @param plugin    Your plugin instance (needed for scheduling).
//     * @param player    The player to show the toast to.
//     * @param message   The message text.
//     * @param icon      The icon item for the toast.
//     * @param frame     The toast frame type (TASK, GOAL, CHALLENGE).
//     */
//    public static void sendToast(Player player, Component message, ItemStack icon, AdvancementFrame frame) {
//        // Build a fake advancement identifier
//        Identifier key = new Identifier("yourplugin", "toast_" + UUID.randomUUID());
//
//        // Build the display info
//        PacketAdvancementDisplay display = new PacketAdvancementDisplay(
//                BukkitConverters.toNMSItem(icon),         // icon
//                AdventureSerializer.toJson(message),      // title as JSON string
//                "",                                       // description
//                null,                                     // background texture (only needed for root adv)
//                frame,                                    // TASK, GOAL, or CHALLENGE
//                true,                                     // showToast
//                false,                                    // announceToChat
//                true                                      // hidden
//        );
//
//        // Build the advancement
//        PacketAdvancement advancement = new PacketAdvancement(
//                key,
//                null,                                     // parent
//                display,
//                Collections.singletonMap("impossible", new PacketCriterion("impossible")), // criteria
//                Collections.singletonList(Collections.singletonList("impossible"))         // requirements
//        );
//
//        // Build the progress (mark criteria as achieved)
//        PacketAdvancementProgress progress = new PacketAdvancementProgress(
//                Collections.singletonMap("impossible", new PacketCriterionProgress(System.currentTimeMillis()))
//        );
//
//        // Send "grant"
//        PacketPlayUpdateAdvancements grant = new PacketPlayUpdateAdvancements(
//                false,
//                Collections.singletonMap(advancement, progress),
//                Collections.emptySet()
//        );
//        PacketEvents.getAPI().getPlayerManager().sendPacket(player, grant);
//
//        // Schedule "revoke" a tick later
//        Bukkit.getScheduler().runTaskLater(plugin, () -> {
//            PacketPlayUpdateAdvancements revoke = new PacketPlayUpdateAdvancements(
//                    false,
//                    Collections.emptyMap(),
//                    Collections.singleton(key)
//            );
//            PacketEvents.getAPI().getPlayerManager().sendPacket(player, revoke);
//        }, 20L);
//}
