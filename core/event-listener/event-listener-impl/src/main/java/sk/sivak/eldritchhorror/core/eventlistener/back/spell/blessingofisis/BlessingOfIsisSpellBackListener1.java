package sk.sivak.eldritchhorror.core.eventlistener.back.spell.blessingofisis;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class BlessingOfIsisSpellBackListener1 extends AbstractSpellBackListener {

    public BlessingOfIsisSpellBackListener1(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]The chosen investigator\ngains a Cursed Condition.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            boolean wasChanged = changeActiveInvestigator(getData("selectedInvestigator"));
            ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
            if (wasChanged) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getActiveInvestigatorId());
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            }
            ServicePlatform.get().getService().release();
        });
    }

    private boolean changeActiveInvestigator(InvestigatorId newActiveInvestigator) {
        if (getActiveInvestigatorId() != newActiveInvestigator) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(newActiveInvestigator);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            return true;
        }
        return false;
    }


    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Sanity.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(1);
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
