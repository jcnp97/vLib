package asia.virtualmc.vLib.utilities.java;

import java.util.List;

public class ObjectUtils {

    /**
     * Converts the given object to a list of strings if possible.
     * <p>
     * Checks if the provided object is a {@link List} containing only {@link String} elements.
     * If so, it safely casts and returns the list.
     * Returns {@code null} if the object is not a list of strings.
     *
     * @param object the object to be converted
     * @return a {@link List} of strings if conversion is successful, otherwise {@code null}
     */
    public static List<String> toStringList(Object object) {
        if (object instanceof List<?> rawList) {
            if (rawList.stream().allMatch(e -> e instanceof String)) {
                @SuppressWarnings("unchecked")
                List<String> stringList = (List<String>) rawList;
                return stringList;
            }
        }
        return null;
    }
}
