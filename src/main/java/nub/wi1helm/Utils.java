package nub.wi1helm;

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
}
