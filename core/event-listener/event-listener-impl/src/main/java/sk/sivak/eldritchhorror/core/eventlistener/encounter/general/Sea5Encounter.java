package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class Sea5Encounter extends AbstractGeneralEncounter {

    public Sea5Encounter() {
        super(5, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(),
                () -> finishAfterFlavor(getTextBuilder().withFail().withFlavor().build()), this::onYes);
    }

    private void onYes() {
        boolean alreadyDelayed = ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed();
        String[] infos = alreadyDelayed
                ? new String[]{"[#BAD]You are already Delayed[]"}
                : new String[]{getTextBuilder().withPass().withInfo(1).build(), getTextBuilder().withPass().withInfo(2).build()};
        ServicePlatform.get().getEncounterService().typeFlavor(alreadyDelayed
                ? getTextBuilder().withPass().withFlavor(2).build()
                : getTextBuilder().withPass().withFlavor().build());
        TypewriterUtils.confirmInfos(infos).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), false));
            if (!ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed()) {
                ServicePlatform.get().getGameService().gainArtifact();
            }
            ServicePlatform.get().getEncounterService().release();
        });
    }
}
