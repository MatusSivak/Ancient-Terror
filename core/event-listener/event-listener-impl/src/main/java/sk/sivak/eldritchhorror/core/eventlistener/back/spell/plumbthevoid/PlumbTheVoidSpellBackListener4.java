package sk.sivak.eldritchhorror.core.eventlistener.back.spell.plumbthevoid;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class PlumbTheVoidSpellBackListener4 extends AbstractSpellBackListener {

    public PlumbTheVoidSpellBackListener4(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1, this::showSpellOwnerAction));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]The chosen investigator gains a\nLost in Time and Space Condition[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            InvestigatorId spellOwnerId = getActiveInvestigatorId();
            InvestigatorId selectedInvestigator = getData("selectedInvestigator");
            if (spellOwnerId != selectedInvestigator) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigator);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            }
            ServicePlatform.get().getGameService().gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE);
            if (spellOwnerId != selectedInvestigator) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(spellOwnerId);
                if (ServicePlatform.get().getPerformedActions().canPerformAction(spellOwnerId)) {
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                }
            }
            ServicePlatform.get().getService().release();
        });
    }

    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Sanity[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        });
    }

    private void showSpellOwnerAction() {
        InvestigatorId spellOwnerId = getActiveInvestigatorId();
        InvestigatorId selectedInvestigator = getData("selectedInvestigator");
        if (spellOwnerId != selectedInvestigator) {
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        }
    }

}
