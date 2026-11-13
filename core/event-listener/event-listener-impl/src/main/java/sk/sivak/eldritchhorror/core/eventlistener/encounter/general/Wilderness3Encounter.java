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
        TypewriterUtils.displayTestButton(Stat.LORE, this::onSuccess, this::onFail);
    }

    private void onFail() {
        finishAfterFlavor(getTextBuilder().withFail().withFlavor().build());
    }

    private void onSuccess() {
        choosePassReward(true, 1,
                () -> ServicePlatform.get().getTokenService().gainClueFromPool(),
                () -> ServicePlatform.get().getGameService().gainSpell());
    }
}
