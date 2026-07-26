package sk.sivak.eldritchhorror.core.constants.mythos;

import java.util.List;

public interface MythosCard {

    int getId();

    MythosColor getMythosColor();

    List<BasicMythosAction> getBasicMythosActions();

    String getTitle();

    String getFlavor();

    String[] getInfos();

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
