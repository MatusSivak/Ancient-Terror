package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.ExpeditionEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.GeneralEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public abstract class AbstractExpeditionEncounter {

    private final ExpeditionEncounterTextBuilder expeditionEncounterTextBuilder;
    private LocationId locationId;

    protected AbstractExpeditionEncounter(int page, LocationId locationId) {
        expeditionEncounterTextBuilder = new ExpeditionEncounterTextBuilder(page, locationId);
        this.locationId = locationId;
    }

    protected ExpeditionEncounterTextBuilder getTextBuilder() {
        return expeditionEncounterTextBuilder;
    }

    public void executeWhole() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
        ServicePlatform.get().getEncounterService().typeHeader(locationId.toString());
        ServicePlatform.get().getEncounterService().typeFlavor(expeditionEncounterTextBuilder.withFlavor().build());
        execute();
        ServicePlatform.get().getService().convertToNull();
        ServicePlatform.get().getService().release();
    }

    protected abstract void execute();

    protected final void gainCondition(ConditionId conditionId) {
        ServicePlatform.get().getGameService().gainCondition(conditionId);
    }

    protected final void retreatDoom() {
        ServicePlatform.get().getDoomOmenService().retreatDoom();
    }

    protected final void loseHealth(int amount) {
        ServicePlatform.get().getTokenService().loseHealth(amount);
    }

    protected final void loseSanity(int amount) {
        ServicePlatform.get().getTokenService().loseSanity(amount);
    }

    protected final void gainArtifact() {
        ServicePlatform.get().getGameService().gainArtifact();
    }

    protected final void gainTwoClues() {
        ServicePlatform.get().getTokenService().gainClueFromPool();
        ServicePlatform.get().getTokenService().gainClueFromPool();
    }

    protected final void gainClue() {
        ServicePlatform.get().getTokenService().gainClueFromPool();
    }

    protected final void becomeDelayed() {
        ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), true));
    }

    protected final void improveSkillOfChoice() {
        ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
        ServicePlatform.get().getInvestigatorService().improveSkill(null);
    }

    protected final void gainSpell() {
        ServicePlatform.get().getGameService().gainSpell();
    }


}
