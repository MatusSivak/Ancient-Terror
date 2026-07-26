package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class ResearchCity24Encounter extends AbstractAzathothResearchEncounter {

    public ResearchCity24Encounter() {
        super(24, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new InfoFlavorTemplate(getTextBuilder(),
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getDoomOmenService().advanceOmen();
                    ServicePlatform.get().getService().addEventCommand(whatever -> {
                        OmenInfo currentOmen = ServicePlatform.get().getOmenTrack().getCurrentOmen();
                        List<LocationId> spawnedGates = ServicePlatform.get().getGateStackRead().getSpawnedGates(currentOmen.getOmenColor().toGateColor());
                        if (spawnedGates.isEmpty()) {
                            // no gate was focused
                        } else {
                            ServicePlatform.get().getService().hold();
                            LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
                            ServicePlatform.get().getService().moveCameraToLocation(currentLocationId);
                            ServicePlatform.get().getService().showResearchBackground(LocationType.CITY);
                            ServicePlatform.get().getService().release();
                        }
                    });
                    ServicePlatform.get().getService().release();
                },
                () -> {
                    TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(),
                            () -> ServicePlatform.get().getEncounterService().finishTypewriterPaper(),
                            this::onYes);
                }).execute();
    }

    private void onYes() {
        ServicePlatform.get().getTokenService().canSpend(0,0,0,2).subscribe(canSpendData -> {
            if (!canSpendData.hasEnough()) {
                TypewriterUtils.confirmInfos("Not enough Sanity.").subscribe(() -> {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                });
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().spend(0,0,0,2).subscribe(spendData -> {
                if (!spendData.hasEnough()) {
                    return;
                }
                ServicePlatform.get().getService().hold();
                spendData.pay();
                gainThisClue();
                ServicePlatform.get().getService().release();

            });
            ServicePlatform.get().getService().release();
        });
    }
}
