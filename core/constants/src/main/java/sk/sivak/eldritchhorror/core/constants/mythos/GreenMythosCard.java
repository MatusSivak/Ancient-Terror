package sk.sivak.eldritchhorror.core.constants.mythos;

import java.util.Arrays;

public class GreenMythosCard extends AbstractMythosCard {

    public GreenMythosCard() {
        super(MythosColor.GREEN,
                Arrays.asList(
                        BasicMythosAction.ADVANCE_OMEN,
                        BasicMythosAction.MONSTER_SURGE,
                        BasicMythosAction.SPAWN_CLUE));
    }
}
