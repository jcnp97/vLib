package asia.virtualmc.vLib.utilities.enums;

public class EnumsUtils {

    /**
     * Converts a string value to its corresponding {@link EnumsLib.UpdateType}.
     * <p>
     * Accepts "add" and "subtract" (case-insensitive) as valid inputs.
     * Any other value defaults to {@code SET}.
     * </p>
     *
     * @param type The update type string to parse.
     * @return The corresponding {@link EnumsLib.UpdateType}.
     */
    public static EnumsLib.UpdateType getUpdateType(String type) {
        return switch (type.toLowerCase()) {
            case "add" -> EnumsLib.UpdateType.ADD;
            case "subtract" -> EnumsLib.UpdateType.SUBTRACT;
            default -> EnumsLib.UpdateType.SET;
        };
    }
}
