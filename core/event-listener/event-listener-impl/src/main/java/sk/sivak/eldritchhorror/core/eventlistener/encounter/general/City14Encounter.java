package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class City14Encounter extends AbstractGeneralEncounter {

    public City14Encounter() {
        super(14, LocationType.CITY);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.INFLUENCE,-1, this::onSuccess, null);
    }

    private void onSuccess() {
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
        TypewriterUtils.confirmInfos(
                getTextBuilder().withPass().withInfo(1).build(),
                getTextBuilder().withPass().withInfo(2).build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getBasicActionService().gainTravelTicket(PathType.SHIP);
            ServicePlatform.get().getBasicActionService().gainTravelTicket(PathType.TRAIN);
            ServicePlatform.get().getEncounterService().release();
        });
    }
}
