package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchCity13Encounter extends AbstractAzathothResearchEncounter {

    public ResearchCity13Encounter() {
        super(13, LocationType.CITY);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(),
                () -> {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                },
                this::onYes);
    }

    private void onYes() {
        ServicePlatform.get().getTokenService().canSpend(0, 0, 0, 2).subscribe(canSpendData -> {
            if (!canSpendData.hasEnough()) {
                TypewriterUtils.confirmInfos("Can't spend two Sanity").subscribe(() -> {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                });
                return;
            }

            boolean hasDarkPact = ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.DARK_PACT);
            if (hasDarkPact) {
                TypewriterUtils.confirmInfos("Can't get Dark Pact Condition").subscribe(() -> {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                });
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().spend(0,0,0,2).subscribe(spendData -> {
                if (!spendData.hasEnough()) {
                    return;
                }

                ServicePlatform.get().getService().hold();
                spendData.pay();
                ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT);
                gainThisClue();
                ServicePlatform.get().getGameService().gainArtifact(AssetTrait.TOME);
                ServicePlatform.get().getService().release();

            });
            ServicePlatform.get().getService().release();
        });
    }
}
