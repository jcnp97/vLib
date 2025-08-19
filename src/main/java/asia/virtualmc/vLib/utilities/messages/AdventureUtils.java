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
        if (string == null) return toComponent("");

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
     * Converts a {@link Component} into its MiniMessage string representation.
     *
     * @param component the component to convert
     * @return the serialized string representation of the component
     */
    public static String toString(Component component) {
        if (component == null) return "";
        return miniMessage.serialize(component);
    }

    /**
     * Converts a list of {@link Component} objects into a list of their
     * MiniMessage string representations.
     *
     * @param components the list of components to convert
     * @return a list of serialized string representations of the components
     */
    public static List<String> toString(List<Component> components) {
        if (components == null) return new ArrayList<>();

        List<String> strings = new ArrayList<>();
        for (Component component : components) {
            strings.add(toString(component));
        }

        return strings;
    }
}
