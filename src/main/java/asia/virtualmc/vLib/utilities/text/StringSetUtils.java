package asia.virtualmc.vLib.utilities.text;

import java.util.*;

public class StringSetUtils {

    /**
     * Creates a sorted {@link Set} from the given list of strings in natural order.
     * <p>
     * If the provided list is {@code null} or empty, returns an empty immutable set.
     * Maintains the sorted order using a {@link LinkedHashSet}.
     *
     * @param list the list of strings to sort
     * @return a {@link Set} containing the sorted strings in natural order
     */
    public static Set<String> sortedSet(List<String> list) {
        if (list == null || list.isEmpty()) return Set.of();
        List<String> sortedList = new ArrayList<>(list);
        sortedList.sort(Comparator.naturalOrder());
        return new LinkedHashSet<>(sortedList);
    }
}
