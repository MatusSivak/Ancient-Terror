package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea8Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchSea8Encounter() {
        super(8, LocationType.SEA);
    }

    @Override
    protected void execute() {
        if (ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty()) {
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo(1).build(),
                    getTextBuilder().withInfo(2).build(),
                    getTextBuilder().withOption(1).build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                gainThisClue();
                moveOneSpaceTowardTheNearestGate();
                ServicePlatform.get().getTokenService().loseSanity(2);
                ServicePlatform.get().getService().release();
            });
            return;
        }
        TypewriterUtils.confirmInfos(
                getTextBuilder().withInfo(1).build(),
                getTextBuilder().withInfo(2).build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            gainThisClue();
            moveOneSpaceTowardTheNearestGate();
            ServicePlatform.get().getService().showResearchBackground(LocationType.SEA);
            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
            chooseOne(
                    () -> {
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        ServicePlatform.get().getTokenService().loseSanity(2);
                    },
                    () -> {
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        SelectCardData selectCardData = new SelectCardData();
                        selectCardData.setAvailableCards(ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()));
                        selectCardData.setHideText("Display Spells?");
                        selectCardData.setTitleText("Select Spell");
                        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscardByChoice);
                    });
            ServicePlatform.get().getService().release();
        });
    }

    private void moveOneSpaceTowardTheNearestGate() {
        if (ServicePlatform.get().getGateStackRead().getSpawnedGates().isEmpty()) {
            return;
        }
        FindNearestData nearestGate = ServicePlatform.get().getLocationMap().findNearest(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId(),
                locationInfo -> ServicePlatform.get().getGateStackRead().isGateAtLocation(locationInfo.getLocationId()));


        ServicePlatform.get().getBasicActionService().travelToLocation(new LocationInfo.Connection() {
            @Override
            public LocationId getLocationId() {
                if (nearestGate.getPreviousLocations().size() == 1) {
                    return nearestGate.getLocationId();
                } else {
                    return nearestGate.getPreviousLocations().get(1);
                }
            }

            @Override
            public PathType getPathType() {
                return PathType.WALK;
            }
        });
    }

    private void chooseOne(Runnable option1, Runnable option2) {

        ServicePlatform.get().getEncounterService().typeInfo(" \n" + TypewriterUtils.CHOOSE_ONE);
        ServicePlatform.get().getEncounterService().displayButtons(
                getTextBuilder().withOption(1).build(),
                getTextBuilder().withOption(2).build()
        ).subscribe(x -> {
            ServicePlatform.get().getEncounterService().hold();
            if (x == 0) {
                option1.run();
            } else {
                option2.run();
            }
            ServicePlatform.get().getEncounterService().release();
        });
    }
}
