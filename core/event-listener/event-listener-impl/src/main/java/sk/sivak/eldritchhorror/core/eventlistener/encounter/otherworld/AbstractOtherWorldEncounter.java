package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
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

    private GateColor gateColor;

    protected AbstractOtherWorldEncounter(int page) {
        otherWorldEncounterTextBuilder = new OtherWorldEncounterTextBuilder(page);
    }

    protected OtherWorldEncounterTextBuilder getTextBuilder() {
        return otherWorldEncounterTextBuilder;
    }

    public void executeWhole(GateColor gateColor) {
        this.gateColor = gateColor;
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

    protected static final Runnable NOTHING = () -> {};

    protected void finishAfterFlavor(String flavor) {
        ServicePlatform.get().getEncounterService().typeFlavor(flavor);
        ServicePlatform.get().getEncounterService().displayButtons("OK").subscribe(button ->
                ServicePlatform.get().getEncounterService().finishTypewriterPaper());
    }

    protected void selectReward(Runnable option1, Runnable option2) {
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_REWARD);
        ServicePlatform.get().getEncounterService().displayButtons(
                getTextBuilder().withPass().withOption(1).build(),
                getTextBuilder().withPass().withOption(2).build()
        ).subscribe(x -> {
            int option = x + 1;
            narrateThen(getTextBuilder().withPass().withOption(option).withFlavor().build(),
                    new String[]{getTextBuilder().withPass().withOption(option).build()},
                    option == 1 ? option1 : option2);
        });
    }

    /** Option 1 applies {@code defaultAction}; option 2 makes the investigator Delayed. Both are narrated. */
    protected EncounterTemplate somethingUnlessDelayedTemplate(Runnable defaultAction) {
        return () -> {
            if (ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed()) {
                narrateUnavailableAlternative(defaultAction);
                return;
            }
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_ONE);
            ServicePlatform.get().getEncounterService().displayButtons(
                    getTextBuilder().withOption(1).build(),
                    getTextBuilder().withOption(2).build()
            ).subscribe(x -> {
                if (x == 0) {
                    narrateOption(1, defaultAction);
                } else {
                    narrateOption(2, () -> ServicePlatform.get().getInvestigatorService()
                            .becomeDelayed(new DelayedData(getActiveInvestigatorId(), false)));
                }
            });
            ServicePlatform.get().getEncounterService().release();
        };
    }

    protected EncounterTemplate unlessSpendTemplate(ClueFocusHealthSanity cost, Runnable defaultAction) {
        return unlessSpendTemplate(cost, defaultAction, null);
    }

    /**
     * Option 1 applies {@code defaultAction}; option 2 pays {@code cost} and then runs {@code onSpent}.
     * When {@code onSpent} is set, {@code option.2.info} describes its effect.
     */
    protected EncounterTemplate unlessSpendTemplate(ClueFocusHealthSanity cost, Runnable defaultAction, Runnable onSpent) {
        return () -> ServicePlatform.get().getTokenService().canSpend(cost).subscribe(canSpendData -> {
            if (!canSpendData.hasEnough()) {
                narrateUnavailableAlternative(defaultAction);
                return;
            }
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_ONE);
            ServicePlatform.get().getEncounterService().displayButtons(
                    getTextBuilder().withOption(1).build(),
                    getTextBuilder().withOption(2).build()
            ).subscribe(x -> {
                if (x == 0) {
                    narrateOption(1, defaultAction);
                } else {
                    spendThenNarrate(cost, defaultAction, onSpent);
                }
            });
            ServicePlatform.get().getEncounterService().release();
        });
    }

    private void spendThenNarrate(ClueFocusHealthSanity cost, Runnable defaultAction, Runnable onSpent) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
        ServicePlatform.get().getTokenService().spend(cost).subscribe(spendData -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
            if (spendData.hasEnough()) {
                spendData.pay();
                narrateThen(getTextBuilder().withOption(2).withFlavor().build(),
                        onSpent == null ? new String[0] : new String[]{getTextBuilder().withOption(2).withInfo().build()},
                        onSpent == null ? NOTHING : onSpent);
            } else {
                narrateUnavailableAlternative(defaultAction);
            }
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }

    private void narrateUnavailableAlternative(Runnable defaultAction) {
        narrateThen(getTextBuilder().withOption(1).withFlavor(2).build(),
                new String[]{getTextBuilder().withOption(1).build()}, defaultAction);
    }

    private void narrateOption(int option, Runnable action) {
        narrateThen(getTextBuilder().withOption(option).withFlavor().build(),
                new String[]{getTextBuilder().withOption(option).build()}, action);
    }

    /** Types the flavor, waits for the infos (or a plain OK) to be confirmed, closes the paper and runs the action. */
    private void narrateThen(String flavor, String[] infos, Runnable action) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeFlavor(" \n" + flavor);
        Completable confirmation = infos.length > 0
                ? TypewriterUtils.confirmInfos(infos)
                : Completable.create(onSub -> ServicePlatform.get().getEncounterService()
                        .displayButtons("OK").subscribe(button -> onSub.onCompleted()));
        confirmation.subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            action.run();
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }

    /**
     * Asks whether to become Delayed for {@code reward}. Both answers are narrated; an investigator who is
     * already Delayed is told so and gets no reward.
     */
    protected EncounterTemplate becomeDelayedForRewardTemplate(Runnable reward) {
        return () -> TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(),
                () -> finishAfterFlavor(" \n" + getTextBuilder().withFail().withFlavor().build()),
                () -> {
                    boolean alreadyDelayed = ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed();
                    String flavor = alreadyDelayed
                            ? getTextBuilder().withPass().withFlavor(2).build()
                            : getTextBuilder().withPass().withFlavor().build();
                    String[] infos = alreadyDelayed
                            ? new String[]{"[#BAD]You are already Delayed[]"}
                            : new String[]{getTextBuilder().withPass().withInfo(1).build(), getTextBuilder().withPass().withInfo(2).build()};
                    narrateThen(flavor, infos, () -> {
                        if (!alreadyDelayed) {
                            ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), false));
                            reward.run();
                        }
                    });
                });
    }


    protected void closeThisGate() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideBackground();
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        ServicePlatform.get().getService().closeGate(currentLocationId, true);
        ServicePlatform.get().getService().showOtherWorldBackground(gateColor);
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
                                ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withFlavor(2).build());
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
                ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withFlavor(2).build());
                failTemplate.execute();
                ServicePlatform.get().getService().release();
            }
        });
    }


}
