package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea12Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchSea12Encounter() {
        super(12, LocationType.SEA);
    }

    @Override
    protected void execute() {
        if (ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
            ServicePlatform.get().getEncounterService().typeInfo("[#BAD]You have a Dark Pact Condition:[]\n ");
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo(1).build(),
                    getTextBuilder().withInfo(2).build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                ServicePlatform.get().getTokenService().loseHealth(3);
                ServicePlatform.get().getTokenService().loseSanity(3);
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withFlavor(2).build());
                displayTestButton();
                ServicePlatform.get().getService().release();
            });
        } else {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFlavor(2).build());
            displayTestButton();
            ServicePlatform.get().getService().release();
        }
    }

    private void displayTestButton() {
        TypewriterUtils.displayTestResearchButton(Stat.WILL, -2, () -> {
            TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo(1).build(),
                    getTextBuilder().withPass().withInfo(2).build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                gainThisClue();
                ServicePlatform.get().getTokenService().gainClueFromPool();
                ServicePlatform.get().getService().release();
            });
        }, () -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        });
    }
}
