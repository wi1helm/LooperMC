package nub.wi1helm;

import net.minestom.server.color.Color;

import java.util.*;

public class Utils {

    private static final Random RANDOM = new Random();

    public static <T> List<T> randomSubList(List<T> list, int newSize) {
        list = new ArrayList<>(list);
        Collections.shuffle(list);
        return list.subList(0, newSize);
    }

     /**
     * Returns a random element from the keys of a map.
     * @param map the map to pick from
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return a random key, or null if the map is empty
     **/
    public static <K, V> K randomKey(Map<K, V> map) {
        if (map == null || map.isEmpty()) return null;
        List<K> keys = new ArrayList<>(map.keySet());
        return keys.get(RANDOM.nextInt(keys.size()));
    }

     /**
     * Returns a random element from a collection.
     * @param collection the collection to pick from
     * @param <T> the type of elements
     * @return a random element, or null if the collection is null or empty
     */
    public static <T> T randomElement(Collection<T> collection) {

        if (collection == null || collection.isEmpty()) {
            return null;
        }

        // Convert the Collection to an ArrayList to get indexed access (needed for Random.nextInt)
        List<T> list = new ArrayList<>(collection);

        // Pick a random index between 0 (inclusive) and list.size() (exclusive)
        return list.get(RANDOM.nextInt(list.size()));
    }

    /**
     * Generates a random net.minestom.server.color.Color object.
     * * @return A Color instance with random RGB values.
     */
    public static Color randomColor() {
        // Generate three random integers, each from 0 (inclusive) to 256 (exclusive).
        int r = RANDOM.nextInt(256);
        int g = RANDOM.nextInt(256);
        int b = RANDOM.nextInt(256);

        // Minestom's Color constructor often takes the individual R, G, B integers.
        return new Color(r, g, b);
    }
}
