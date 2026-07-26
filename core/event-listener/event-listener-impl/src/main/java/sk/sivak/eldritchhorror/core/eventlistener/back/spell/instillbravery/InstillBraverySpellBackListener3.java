package sk.sivak.eldritchhorror.core.eventlistener.back.spell.instillbravery;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class InstillBraverySpellBackListener3 extends AbstractSpellBackListener {

    public InstillBraverySpellBackListener3(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0, this::showTargetInvestigator));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void showTargetInvestigator() {
        InvestigatorId targetInvestigatorId = getData("targetInvestigatorId");
        InvestigatorId spellOwnerId = getData("spellOwnerId");
        if (spellOwnerId != targetInvestigatorId) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(targetInvestigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        }
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]That investigator gains Madness Condition.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(ConditionTrait.MADNESS);
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
        TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getService().release();
        });
    }
}
