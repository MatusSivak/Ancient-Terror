package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchCity13Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchCity13Encounter() {
        super(13, LocationType.CITY);
    }

    @Override
    protected void execute() {
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        if (ServicePlatform.get().getGateStackRead().isGateAtLocation(currentLocationId)) {
            TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), () -> {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            }, () -> {
                if (ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty()) {
                    TypewriterUtils.confirmInfos("You don't have any Spells.").subscribe(() -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        ServicePlatform.get().getService().release();
                    });
                    return;
                }
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();

                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()));
                selectCardData.setHideText("Display Spells?");
                selectCardData.setTitleText("Select Spell");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(spell -> {
                    ServicePlatform.get().getService().hold();
                    EncounterUtils.onSelectCardToDiscardByChoice(spell);
                    ServicePlatform.get().getService().hideBackground();
                    ServicePlatform.get().getService().discardGate(currentLocationId);
                    ServicePlatform.get().getService().release();
                });

                ServicePlatform.get().getService().release();
            });
        } else {
            TypewriterUtils.confirmInfos("There is no Gate here.").subscribe(() -> {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
        }
    }
}
