package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.OtherWorldEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public abstract class AbstractOtherWorldEncounter {

    private final OtherWorldEncounterTextBuilder otherWorldEncounterTextBuilder;

    protected AbstractOtherWorldEncounter(int page) {
        otherWorldEncounterTextBuilder = new OtherWorldEncounterTextBuilder(page);
    }

    protected OtherWorldEncounterTextBuilder getTextBuilder() {
        return otherWorldEncounterTextBuilder;
    }

    public void executeWhole() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
        ServicePlatform.get().getEncounterService().typeHeader(otherWorldEncounterTextBuilder.withTitle().build());
        ServicePlatform.get().getEncounterService().typeFlavor(otherWorldEncounterTextBuilder.withFlavor().build());
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

    protected void selectReward(Runnable option1, Runnable option2) {
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_REWARD);
        ServicePlatform.get().getEncounterService().displayButtons(
                getTextBuilder().withPass().withOption(1).build(),
                getTextBuilder().withPass().withOption(2).build()
        ).subscribe(x -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            if (x == 0) {
                option1.run();
            } else {
                option2.run();
            }
            ServicePlatform.get().getEncounterService().release();
        });
    }

    protected EncounterTemplate somethingUnlessDelayedTemplate(Runnable defaultAction) {
        return () -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_ONE);
            ServicePlatform.get().getEncounterService().displayButtons(
                    getTextBuilder().withOption(1).build(),
                    getTextBuilder().withOption(2).build()
            ).subscribe(x -> {
                if (x == 0) {
                    ServicePlatform.get().getEncounterService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    defaultAction.run();
                    ServicePlatform.get().getEncounterService().release();
                } else {
                    ServicePlatform.get().getEncounterService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), false));
                    ServicePlatform.get().getEncounterService().release();
                }
            });
            ServicePlatform.get().getEncounterService().release();
        };
    }


    protected void closeThisGate() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideBackground();
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        ServicePlatform.get().getService().closeGate(currentLocationId, true);
        ServicePlatform.get().getService().showOtherWorldBackground();
        ServicePlatform.get().getService().release();
    }

    protected void closeThisGateAndEnd() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideBackground();
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        ServicePlatform.get().getService().closeGate(currentLocationId, true);
        ServicePlatform.get().getService().release();
    }

    protected EncounterTemplate spendOneClueToResolvePassTemplate(EncounterTemplate passTemplate, EncounterTemplate failTemplate) {
        return () -> ServicePlatform.get().getTokenService().canSpend(1, 0, 0, 0).subscribe(canSpendData -> {
            if (canSpendData.hasEnough()) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().typeInfo(getTextBuilder().withQuestion().build());
                ServicePlatform.get().getEncounterService().displayButtons("[#BAD]No[]", "[#GOOD]Yes[]").subscribe(buttonId -> {
                    if (buttonId == 0) { // Don't spend clue
                        ServicePlatform.get().getService().hold();
                        getTextBuilder().addFailPrefix();
                        ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withFlavor().build());
                        failTemplate.execute();
                        ServicePlatform.get().getService().release();
                    } else { // Spend clue
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                        ServicePlatform.get().getTokenService().spend(1, 0, 0, 0).subscribe(spendData -> {
                            if (spendData.hasEnough()) {
                                ServicePlatform.get().getService().hold();
                                spendData.pay();
                                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                                getTextBuilder().addPassPrefix();
                                ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withFlavor().build());
                                passTemplate.execute();
                                ServicePlatform.get().getService().release();
                            } else {
                                ServicePlatform.get().getService().hold();
                                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                                ServicePlatform.get().getEncounterService().typeInfo("[#BAD]You don't have a Clue[]");
                                getTextBuilder().addFailPrefix();
                                ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withFlavor().build());
                                failTemplate.execute();
                                ServicePlatform.get().getService().release();
                            }
                        });
                        ServicePlatform.get().getService().release();
                    }
                });
                ServicePlatform.get().getService().release();
            } else { // Does not have a Clue
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().typeInfo("[#BAD]You don't have a Clue[]");
                getTextBuilder().addFailPrefix();
                ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withFlavor().build());
                failTemplate.execute();
                ServicePlatform.get().getService().release();
            }
        });
    }


}
