package sk.sivak.eldritchhorror.core.eventlistener.back.spell.poisonmist;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.LinkedList;
import java.util.List;

public class PoisonMistSpellBackListener1 extends AbstractSpellBackListener {

    public PoisonMistSpellBackListener1(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2, () -> {
            List<DiceRoll> newDiceRolls = new LinkedList<>(getTestData().getDiceRolls());
            newDiceRolls.add(new DiceRoll(DiceRoll.Score.GOOD));
            newDiceRolls.add(new DiceRoll(DiceRoll.Score.GOOD));
            getTestData().setDiceRolls(newDiceRolls);
        }, true));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]Gain a Poisoned Condition[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.POISONED);
            ServicePlatform.get().getService().release();
        });
    }


    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Health[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        TypewriterUtils.confirmInfos("[#GOOD]Add 2 successes to your test result[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            runMainSpellAction();
            ServicePlatform.get().getService().release();
        });
    }

}
