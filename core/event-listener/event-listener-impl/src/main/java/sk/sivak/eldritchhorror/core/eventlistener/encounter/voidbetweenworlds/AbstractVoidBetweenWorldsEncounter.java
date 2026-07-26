package sk.sivak.eldritchhorror.core.eventlistener.encounter.voidbetweenworlds;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.RlyehRisenEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.VoidBetweenWorldsEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public abstract class AbstractVoidBetweenWorldsEncounter {

    private final VoidBetweenWorldsEncounterTextBuilder voidBetweenWorldsEncounterTextBuilder;

    protected AbstractVoidBetweenWorldsEncounter(int page) {
        voidBetweenWorldsEncounterTextBuilder = new VoidBetweenWorldsEncounterTextBuilder(page);
    }

    protected VoidBetweenWorldsEncounterTextBuilder getTextBuilder() {
        return voidBetweenWorldsEncounterTextBuilder;
    }

    public void executeWhole() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
        ServicePlatform.get().getEncounterService().typeHeader("Void Between Worlds");
        ServicePlatform.get().getEncounterService().typeFlavor(voidBetweenWorldsEncounterTextBuilder.withFlavor().build());
        execute();
        ServicePlatform.get().getService().convertToNull();
        ServicePlatform.get().getService().release();
    }

    protected abstract void execute();

    protected final void gainCondition(ConditionId conditionId) {
        ServicePlatform.get().getGameService().gainCondition(conditionId);
    }

    protected final void gainCondition(ConditionTrait conditionTrait) {
        ServicePlatform.get().getGameService().gainCondition(conditionTrait);
    }

    protected final void loseHealth(int amount) {
        ServicePlatform.get().getTokenService().loseHealth(amount);
    }

    protected final void loseSanity(int amount) {
        ServicePlatform.get().getTokenService().loseSanity(amount);
    }

    protected final void becomeDelayed() {
        ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), true));
    }

    protected void closeThisGateAndEnd() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideBackground();
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        ServicePlatform.get().getService().closeGate(currentLocationId, false);
        ServicePlatform.get().getService().release();
    }
}
