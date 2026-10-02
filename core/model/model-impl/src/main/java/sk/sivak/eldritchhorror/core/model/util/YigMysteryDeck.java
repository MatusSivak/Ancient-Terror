package sk.sivak.eldritchhorror.core.model.util;
import java.util.*;
import sk.sivak.eldritchhorror.core.constants.*;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.MysteryCardId.Yig;
public final class YigMysteryDeck {
    private YigMysteryDeck() {}
    public static List<MysteryCardInfo> create(int players) {
        List<MysteryCardInfo> cards = new ArrayList<>();
        for (Yig id : Yig.values()) {
            MysteryDeckHelper.MysteryCardInfoImpl card = new MysteryDeckHelper.MysteryCardInfoImpl();
            card.setAncientOneId(AncientOneId.YIG);
            card.setMysteryCardId(id);
            String key = "mystery.yig." + id.name();
            card.setName(key + ".name"); card.setFlavorText(key + ".flavor"); card.setMysteryText(key + ".text");
            int complexity = players;
            List<LocationId> pins = null;
            switch (id) {
                case CROWN_OF_THE_SERPENT: complexity = 1; break;
                case DESCENDANTS_OF_YIG: complexity = players + 2; pins = LocationId.getRandomLocations(1); break;
                case THE_WINGED_SERPENT: complexity = players + 2; pins = Collections.singletonList(LocationId.TOKYO); break;
                case KN_YAN_UNEARTHED: complexity = (players + 1) / 2; pins = Collections.singletonList(LocationId.SPACE_6); break;
                case RISE_OF_THE_SERPENT_PEOPLE: complexity = (players + 1) / 2; pins = new ArrayList<>(LocationId.getRandomLocations(complexity)); break;
                case SERPENTS_NEST: complexity = players + 3; pins = Collections.singletonList(LocationId.SPACE_7); break;
                default: break;
            }
            card.setMysteryComplexity(complexity);
            card.setProgressSupplier(() -> 0);
            if (pins != null) { final List<LocationId> locations = pins; card.setPinLocationsSupplier(() -> locations); }
            cards.add(card);
        }
        MysteryCardInfo finale = cards.remove(cards.size() - 1);
        Collections.shuffle(cards);
        cards.add(3, finale);
        return cards;
    }
}
