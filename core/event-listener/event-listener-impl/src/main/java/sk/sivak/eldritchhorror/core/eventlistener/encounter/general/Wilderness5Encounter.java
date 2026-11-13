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
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(),
                () -> finishAfterFlavor(getTextBuilder().withFail().withFlavor().build()), this::onYes);
    }

    private void onYes() {
        ServicePlatform.get().getTokenService().spend(1, 0, 0, 0).subscribe(
                spendData -> {
                    if (spendData.hasEnough()) {
                        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
                        TypewriterUtils.confirmInfos(
                                getTextBuilder().withPass().withInfo(1).build(),
                                getTextBuilder().withPass().withInfo(2).build()
                        ).subscribe(() -> {
                            ServicePlatform.get().getService().hold();
                            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                            spendData.pay();
                            ServicePlatform.get().getGameService().gainArtifact();
                            ServicePlatform.get().getService().release();
                        });
                    } else {
                        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor(2).build());
                        TypewriterUtils.confirmInfos("[#BAD]You don't have a Clue[]")
                                .subscribe(() -> ServicePlatform.get().getEncounterService().finishTypewriterPaper());
                    }
                }
        );
    }
}
