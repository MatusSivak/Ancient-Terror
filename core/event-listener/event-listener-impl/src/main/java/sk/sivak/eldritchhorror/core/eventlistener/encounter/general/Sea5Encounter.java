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
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), null, this::onYes);
    }

    private void onYes() {
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), false));
        if (!ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed()) {
            ServicePlatform.get().getGameService().gainArtifact();
        }

    }
}
