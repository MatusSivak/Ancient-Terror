package sk.sivak.eldritchhorror.core.eventlistener.back.spell.wither;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.CONFIRM_TEST;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.UPDATE_MONSTER_DAMAGE;

public class WitherSpellBackListener4 extends AbstractSpellBackListener {

    public WitherSpellBackListener4(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]Gain an Illness Condition[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(ConditionTrait.ILLNESS);
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
        TypewriterUtils.confirmInfos("[#GOOD]Reduce the Monster's damage by one.[]").subscribe(() -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getService().startInsertingBeforeCommand(CONFIRM_TEST);
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().<TestData>addEventCommand(input -> {
                CombatData combatData = ServicePlatform.get().getTestFlavor().getFlavorData();
                combatData.setActualDamage(Math.max(1, combatData.getActualDamage()-1));
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getTestService().updateMonsterDamage(combatData);
                ServicePlatform.get().getTestService().convertTo(TestData.class, () -> input);
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
            ServicePlatform.get().getService().endInsertingAtCommand();
        });
    }
}
