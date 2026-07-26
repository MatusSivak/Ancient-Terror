package sk.sivak.eldritchhorror.core.constants.card;

public final class CardIdUtils {

    private CardIdUtils() {
    }

    public static String asString(Enum<?> e) {
        String[] nameComponents = e.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nameComponents.length; i++) {
            sb.append(nameComponents[i].substring(0, 1));
            sb.append(nameComponents[i].substring(1).toLowerCase());
            if (i != nameComponents.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
