package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class Wilderness5Encounter extends AbstractGeneralEncounter {

    public Wilderness5Encounter() {
        super(5, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), null, this::onYes);
    }

    private void onYes() {
        ServicePlatform.get().getTokenService().spend(1, 0, 0, 0).subscribe(
                spendData -> {
                    if (spendData.hasEnough()) {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        spendData.pay();
                        ServicePlatform.get().getGameService().gainArtifact();
                        ServicePlatform.get().getService().release();
                    } else {
                        TypewriterUtils.confirmInfos("[#BAD]You don't have a Clue[]")
                                .subscribe(() -> ServicePlatform.get().getEncounterService().finishTypewriterPaper());
                    }
                }
        );
    }
}
