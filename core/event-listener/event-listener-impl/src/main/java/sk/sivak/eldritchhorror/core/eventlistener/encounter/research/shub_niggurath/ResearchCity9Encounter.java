package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchCity9Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchCity9Encounter() {
        super(9, LocationType.CITY);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), this::onNo, this::onYes);

    }

    private void onYes() {
        if (ServicePlatform.get().getConditionsDeck().canGetConditionId(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT);
            gainThisClue();
            ServicePlatform.get().getService().release();
        } else {
            TypewriterUtils.confirmInfos("Can't get Dark Pact Condition").subscribe(this::onNo);
        }
    }

    private void onNo() {
        ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withFail().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getService().hideBackground();
            for (LocationId randomLocation : LocationId.getRandomLocations(2)) {
                SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
                spawnMonsterData.setLocationId(randomLocation);
                ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
            }
            ServicePlatform.get().getService().release();
        });
    }
}
