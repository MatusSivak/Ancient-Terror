package sk.sivak.eldritchhorror.core.constants.mythos;

import java.util.Arrays;

public class YellowMythosCard extends AbstractMythosCard {

    public YellowMythosCard() {
        super(MythosColor.YELLOW,
                Arrays.asList(
                        BasicMythosAction.ADVANCE_OMEN,
                        BasicMythosAction.RECKONING,
                        BasicMythosAction.SPAWN_GATE));
    }
}
