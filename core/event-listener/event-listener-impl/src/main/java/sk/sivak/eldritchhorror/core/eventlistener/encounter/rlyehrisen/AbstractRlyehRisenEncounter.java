package sk.sivak.eldritchhorror.core.eventlistener.encounter.rlyehrisen;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.RlyehRisenEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public abstract class AbstractRlyehRisenEncounter {

    private final RlyehRisenEncounterTextBuilder rlyehRisenEncounterTextBuilder;

    protected AbstractRlyehRisenEncounter(int page) {
        rlyehRisenEncounterTextBuilder = new RlyehRisenEncounterTextBuilder(page);
    }

    protected RlyehRisenEncounterTextBuilder getTextBuilder() {
        return rlyehRisenEncounterTextBuilder;
    }

    public void executeWhole() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
        ServicePlatform.get().getEncounterService().typeHeader("R'lyeh Risen");
        ServicePlatform.get().getEncounterService().typeFlavor(rlyehRisenEncounterTextBuilder.withFlavor().build());
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

}
