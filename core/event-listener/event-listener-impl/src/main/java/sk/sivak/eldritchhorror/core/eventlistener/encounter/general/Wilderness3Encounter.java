package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class Wilderness3Encounter extends AbstractGeneralEncounter {

    public Wilderness3Encounter() {
        super(3, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.LORE, this::onSuccess, null);
    }

    private void onSuccess() {
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_REWARD);
        ServicePlatform.get().getEncounterService().displayButtons(
                getTextBuilder().withPass().withOption(1).build(),
                getTextBuilder().withPass().withOption(2).build()
        ).subscribe(x -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            if (x == 0) {
                ServicePlatform.get().getTokenService().gainClueFromPool();
            } else {
                ServicePlatform.get().getGameService().gainSpell();
            }
            ServicePlatform.get().getEncounterService().release();
        });
    }
}
