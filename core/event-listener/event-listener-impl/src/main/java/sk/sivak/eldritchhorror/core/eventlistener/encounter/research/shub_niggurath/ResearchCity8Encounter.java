package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchCity8Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchCity8Encounter() {
        super(8, LocationType.CITY);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), null, this::onYes);

    }

    private void onYes() {
        if (ServicePlatform.get().getConditionsDeck().canGetConditionId(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT);
            gainThisClue();
            ServicePlatform.get().getGameService().gainArtifact(ArtifactId.SWORD_OF_SAINT_JEROME);
            ServicePlatform.get().getService().release();
        } else {
            TypewriterUtils.confirmInfos("Can't get Dark Pact Condition").subscribe(() -> {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
        }

    }
}
