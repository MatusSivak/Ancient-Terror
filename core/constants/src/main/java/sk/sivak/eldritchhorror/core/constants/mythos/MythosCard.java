package sk.sivak.eldritchhorror.core.constants.mythos;

import java.util.List;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public interface MythosCard {

    int getId();

    MythosColor getMythosColor();

    List<BasicMythosAction> getBasicMythosActions();

    String getTitle();

    String getFlavor();

    String[] getInfos();

    /** The placeholder requests one target for this card's text effect. */
    default boolean requiresRandomInvestigator() {
        for (String info : getInfos()) if (info.contains("{investigator}")) return true;
        return false;
    }

    default String[] getInfos(InvestigatorId target) {
        String[] infos = getInfos().clone();
        String occupation = target == null ? "No investigator" : "The " + target;
        for (int i = 0; i < infos.length; i++) infos[i] = infos[i].replace("{investigator}", occupation);
        return infos;
    }

    MythosDifficulty getMythosDifficulty();

    enum MythosColor {
        GREEN("Green"),
        YELLOW("Yellow"),
        BLUE("Blue");

        private String value;
        MythosColor(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    enum BasicMythosAction {
        /*
        --- GREEN ---
        ADVANCE_OMEN,
        MONSTER_SURGE,
        SPAWN_CLUE,
        --- YELLOW ---
        ADVANCE_OMEN,
        RECKONING,
        SPAWN_GATE,
        --- BLUE ---
        SPAWN_CLUE
        */
        ADVANCE_OMEN,
        MONSTER_SURGE,
        SPAWN_CLUE,
        RECKONING,
        SPAWN_GATE,
        DRAW_RUMOR
    }

    enum MythosDifficulty {
        EASY,
        NORMAL,
        HARD
    }
}
