package sk.sivak.eldritchhorror.core.eventlistener.back.spell.plumbthevoid;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class PlumbTheVoidSpellBackListener1 extends AbstractSpellBackListener {

    public PlumbTheVoidSpellBackListener1(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1, this::showSpellOwnerAction));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2, this::showSpellOwnerAction));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]The chosen investigator loses three Health[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            InvestigatorId spellOwnerId = getActiveInvestigatorId();
            InvestigatorId selectedInvestigator = getData("selectedInvestigator");
            if (spellOwnerId != selectedInvestigator) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigator);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            }
            ServicePlatform.get().getTokenService().loseHealth(3);
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
        TypewriterUtils.confirmInfos("[#GOOD]You may perform an additional action[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getBasicActionService().addFreeAction();
            ServicePlatform.get().getService().release();
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
