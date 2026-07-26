package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class ResearchCity15Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchCity15Encounter() {
        super(15, LocationType.CITY);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(
                getTextBuilder().withInfo(1).build(),
                getTextBuilder().withInfo(2).build()).subscribe( () -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(2);
            moveToTheNearestSea();
            ServicePlatform.get().getService().release();
        });
    }
}
