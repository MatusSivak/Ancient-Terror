package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchWilderness3Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchWilderness3Encounter() {
        super(3, LocationType.WILDERNESS);
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
            ServicePlatform.get().getInvestigatorService().improveSkill(Stat.STRENGTH);
            ServicePlatform.get().getService().release();
        } else {
            onNo();
        }

    }

    private void onNo() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withFail().withFlavor().build());
        TypewriterUtils.confirmInfos(
                getTextBuilder().withFail().withInfo().build()).subscribe(( )-> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.PARANOIA);
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }
}
