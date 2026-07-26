package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class City10Encounter extends AbstractGeneralEncounter {

    public City10Encounter() {
        super(10, LocationType.CITY);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.WILL, this::onSuccess, this::onFail);
    }

    private void onSuccess() {
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().spawnClues(1).subscribe();
            ServicePlatform.get().getEncounterService().release();
        });
    }

    private void onFail() {
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFail().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(2);
            ServicePlatform.get().getEncounterService().release();
        });
    }
}
