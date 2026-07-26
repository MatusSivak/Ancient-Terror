package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class ResearchSea20Encounter extends AbstractAzathothResearchEncounter {

    public ResearchSea20Encounter() {
        super(20, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(
                getTextBuilder().withInfo(1).build(),
                getTextBuilder().withInfo(2).build(),
                getTextBuilder().withInfo(3).build()
        ).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            gainThisClue();
            ServicePlatform.get().getDoomOmenService().selectNewOmen();
            ServicePlatform.get().getService().addEventCommand(whatever -> {
                OmenInfo currentOmen = ServicePlatform.get().getOmenTrack().getCurrentOmen();
                ServicePlatform.get().getDoomOmenService().advanceDoomByCurrentOmen(currentOmen);
            });
            ServicePlatform.get().getService().release();
        });
    }
}
