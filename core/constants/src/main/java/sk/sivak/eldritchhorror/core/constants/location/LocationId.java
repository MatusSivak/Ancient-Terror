package sk.sivak.eldritchhorror.core.constants.location;

import java.util.*;

/**
 * @author msivak
 */
public enum LocationId {
    SPACE_1,
    SPACE_2,
    SPACE_3,
    SPACE_4,
    SPACE_5,
    SPACE_6,
    SPACE_7,
    SPACE_8,
    SPACE_9,
    SPACE_10,
    SPACE_11,
    SPACE_12,
    SPACE_13,
    SPACE_14,
    SPACE_15,
    SPACE_16,
    SPACE_17,
    SPACE_18,
    SPACE_19,
    SPACE_20,
    SPACE_21,
    SAN_FRANCISCO,
    BUENOS_AIRES,
    ARKHAM,
    LONDON,
    ROME,
    ISTANBUL,
    SHANGHAI,
    TOKYO,
    SYDNEY,
    THE_AMAZON,
    THE_HEART_OF_AFRICA,
    THE_PYRAMIDS,
    ANTARCTICA,
    THE_HIMALAYAS,
    TUNGUSKA;

    public static List<LocationId> getRandomLocations(int count) {
        Stack<LocationId> locations = new Stack<>();
        locations.addAll(Arrays.asList(values()));
        Collections.shuffle(locations);
        List<LocationId> result = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            result.add(locations.pop());
        }
        return result;
    }

    @Override
    public String toString() {
        if (this == THE_HEART_OF_AFRICA) {
            return "The Heart of Africa";
        }
        String[] words = name().split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            word = word.toLowerCase();
            result.append(word.substring(0, 1).toUpperCase()).append(word.substring(1, word.length()).toLowerCase()).append(" ");
        }
        return result.substring(0, result.length() - 1);
    }

    public String lastWordToLowercase() {
        String[] words = name().split("_");
        return words[words.length-1].toLowerCase();
    }

    public String lastWordToCamelCase() {
        String lowercase = lastWordToLowercase();
        return lowercase.substring(0, 1).toUpperCase() + lowercase.substring(1, lowercase.length());
    }

    public static void main(String[] args) {
        for (LocationId locationId : LocationId.values()) {
            System.out.println(locationId.toString() + " " + locationId.lastWordToCamelCase());
        }
    }
}
