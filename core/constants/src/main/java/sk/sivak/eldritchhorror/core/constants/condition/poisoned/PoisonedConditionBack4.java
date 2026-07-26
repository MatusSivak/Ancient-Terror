package sk.sivak.eldritchhorror.core.constants.condition.poisoned;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class PoisonedConditionBack4 extends AbstractConditionBack {

    public PoisonedConditionBack4() {
        title = "Arcane Infection";
        flavorText = "Your blood burns and causes your veins to glow bright. It is no blood poisoning that ails you, but something far more difficult to repel.";
        effectText = "Test Lore. If you pass,\n" +
                "[#GOOD]discard this card.[]\n" +
                "If you fail, the infection continues to erode your knowledge of the arcane.\n" +
                "[#BAD]Discard 1 Spell\n" +
                "and flip this card.[]";
    }
}
