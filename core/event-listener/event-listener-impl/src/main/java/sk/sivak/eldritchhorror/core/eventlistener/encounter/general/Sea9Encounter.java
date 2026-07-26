package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class Sea9Encounter extends AbstractGeneralEncounter {

    public Sea9Encounter() {
        super(9, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.INFLUENCE, this::onSuccess, null);
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
}
