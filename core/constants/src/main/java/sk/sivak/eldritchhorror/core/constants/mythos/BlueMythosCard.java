package sk.sivak.eldritchhorror.core.constants.mythos;

import java.util.Arrays;

public class BlueMythosCard extends AbstractMythosCard {

    private String rumorCardId;

    public BlueMythosCard() {
        super(MythosColor.BLUE, Arrays.asList(
                        BasicMythosAction.SPAWN_CLUE,
                        BasicMythosAction.DRAW_RUMOR));
    }

    public String getRumorCardId() {
        return rumorCardId;
    }

    public void setRumorCardId(String rumorCardId) {
        this.rumorCardId = rumorCardId;
    }
}
