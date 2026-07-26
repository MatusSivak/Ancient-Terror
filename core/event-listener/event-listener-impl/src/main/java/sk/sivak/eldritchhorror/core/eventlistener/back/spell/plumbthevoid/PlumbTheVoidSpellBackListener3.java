package sk.sivak.eldritchhorror.core.eventlistener.back.spell.plumbthevoid;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class PlumbTheVoidSpellBackListener3 extends AbstractSpellBackListener {

    public PlumbTheVoidSpellBackListener3(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0, true));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]The chosen investigator loses one Sanity[] and\n[#BAD]Moves to a random space[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            InvestigatorId spellOwnerId = getActiveInvestigatorId();
            InvestigatorId selectedInvestigator = getData("selectedInvestigator");
            if (spellOwnerId != selectedInvestigator) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigator);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            }
            ServicePlatform.get().getTokenService().loseSanity(1);
            LocationId locationId = LocationId.getRandomLocations(1).get(0);
            ServicePlatform.get().getBasicActionService().travelToLocation(new LocationInfo.Connection() {
                @Override
                public LocationId getLocationId() {
                    return locationId;
                }

                @Override
                public PathType getPathType() {
                    return PathType.WALK;
                }
            });
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
        TypewriterUtils.confirmInfos("[#BAD]The chosen investigator loses one Sanity[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            InvestigatorId spellOwnerId = getActiveInvestigatorId();
            InvestigatorId selectedInvestigator = getData("selectedInvestigator");
            if (spellOwnerId != selectedInvestigator) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigator);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            }
            ServicePlatform.get().getTokenService().loseSanity(1);
            if (spellOwnerId != selectedInvestigator) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(spellOwnerId);
                if (ServicePlatform.get().getPerformedActions().canPerformAction(spellOwnerId)) {
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                }
            }
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
