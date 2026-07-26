package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchWilderness16Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchWilderness16Encounter() {
        super(16, LocationType.WILDERNESS);
    }

    @Override
    public void executeWhole() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
        ServicePlatform.get().getEncounterService().typeHeader(getLocationType().getValue());
        if (ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
            ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFlavor().build() + " " + getTextBuilder().withFlavor(2).build());
            TypewriterUtils.confirmInfos(getTextBuilder().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                ServicePlatform.get().getService().hideBackground();

                ConditionInfo darkPact = ServicePlatform.get().getConditionsDeck().getCondition(getActiveInvestigatorId(), ConditionId.DARK_PACT);
                findConditionBackListener(darkPact).justFlip();



                ServicePlatform.get().getService().addEventCommand(in -> {
                    if (ServicePlatform.get().getInvestigators().getActiveInvestigator() == null) { // investigator is dead
                        return;
                    }
                    ServicePlatform.get().getService().hold();
                    LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
                    ServicePlatform.get().getService().moveCameraToLocation(currentLocationId);
                    ServicePlatform.get().getService().showResearchBackground(LocationType.WILDERNESS);
                    ServicePlatform.get().getService().release();

                });

                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);

                ServicePlatform.get().getEncounterService().typeFlavor(" \n" +getTextBuilder().withFlavor(3).build());
                displayTestButton();


                ServicePlatform.get().getService().release();
            });

        } else {
            ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFlavor().build() + " " + getTextBuilder().withFlavor(3).build());
            displayTestButton();
        }

        ServicePlatform.get().getService().convertToNull();
        ServicePlatform.get().getService().release();
    }

    private void displayTestButton() {
        TypewriterUtils.displayTestResearchButton(Stat.WILL, 0, () -> {
            TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                gainThisClue();
                ServicePlatform.get().getService().release();
            });
        }, () -> {
            TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getTokenService().loseSanity(3);
                ServicePlatform.get().getService().release();
            });
        });
    }

    @Override
    protected void execute() {

    }

    private AbstractConditionBackListener findConditionBackListener(ConditionInfo conditionInfo) {
        String conditionBackClassName = conditionInfo.getId().getBackConditionClassName(conditionInfo.getConditionBack().getConditionBackId());
        String conditionBackListenerClassName = conditionBackClassName
                .replace(
                        "sk.sivak.eldritchhorror.core.constants.condition",
                        "sk.sivak.eldritchhorror.core.eventlistener.back.condition")
                .replace(
                        "ConditionBack",
                        "ConditionBackListener");
        try {
            return (AbstractConditionBackListener) Class.forName(conditionBackListenerClassName)
                    .getConstructor(ConditionInfo.class).newInstance(conditionInfo);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
