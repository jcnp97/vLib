package asia.virtualmc.vLib.utilities.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

public class AdventureUtils {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacyAmpersand = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer legacySection = LegacyComponentSerializer.legacySection();

    /**
     * Converts a formatted string into an Adventure {@link net.kyori.adventure.text.Component}.
     * Supports MiniMessage, legacy ampersand (&) and section (§) color codes.
     * Automatically disables italic formatting.
     *
     * @param string The input string to convert.
     * @return A Component representing the formatted message.
     */
    public static Component toComponent(String string) {
        Component component;
        if (string.contains("&")) {
            component = legacyAmpersand.deserialize(string);
        } else if (string.contains("§")) {
            component = legacySection.deserialize(string);
        } else {
            component = miniMessage.deserialize(string);
        }

        return component.decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Converts a list of formatted strings into a list of Adventure {@link net.kyori.adventure.text.Component}s.
     * Each string is converted using {@link #toComponent(String)} and has italic formatting disabled.
     *
     * @param strings The list of strings to convert.
     * @return A list of Components representing the formatted messages.
     */
    public static List<Component> toComponent(List<String> strings) {
        List<Component> components = new ArrayList<>();

        for (String string : strings) {
            components.add(toComponent(string));
        }

        return components;
    }

    /**
     * Converts a {@link net.kyori.adventure.text.Component} to a plain text {@link String}.
     * <p>
     * If the component is {@code null}, an empty string {@code ""} is returned.
     *
     * @param component the component to convert
     * @return the plain text representation of the component, or an empty string if null
     */
    public static String toString(Component component) {
        if (component == null) return "";
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
}
