package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import rx.Single;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.GeneralEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public abstract class AbstractGeneralEncounter {

    private final GeneralEncounterTextBuilder generalEncounterTextBuilder;
    private LocationType locationType;

    protected AbstractGeneralEncounter(int page, LocationType locationType) {
        generalEncounterTextBuilder = new GeneralEncounterTextBuilder(page, locationType);
        this.locationType = locationType;
    }

    protected GeneralEncounterTextBuilder getTextBuilder() {
        return generalEncounterTextBuilder;
    }

    public void executeWhole() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
        ServicePlatform.get().getEncounterService().typeHeader(locationType.getValue());
        ServicePlatform.get().getEncounterService().typeFlavor(generalEncounterTextBuilder.withFlavor().build());
        execute();
        ServicePlatform.get().getService().convertToNull();
        ServicePlatform.get().getService().release();
    }

    protected abstract void execute();

    protected void finishAfterFlavor(String flavor) {
        ServicePlatform.get().getEncounterService().typeFlavor(flavor);
        ServicePlatform.get().getEncounterService().displayButtons("OK").subscribe(button ->
                ServicePlatform.get().getEncounterService().finishTypewriterPaper());
    }

    protected void chooseReserveOrDeckReward(boolean reserveAvailable, Action0 gainFromReserve, Action0 gainFromDeck) {
        choosePassReward(reserveAvailable, 2, gainFromReserve, gainFromDeck);
    }

    protected void chooseClueOrImprovement(Stat skill) {
        boolean canImprove = ServicePlatform.get().getInvestigators().getActiveInvestigator().getStatBonus(skill) < 2;
        choosePassReward(canImprove, 1,
                () -> ServicePlatform.get().getTokenService().gainClueFromPool(),
                () -> ServicePlatform.get().getInvestigatorService().improveSkill(skill));
    }

    /** Narrates the pass, lets the investigator pick option 1 or 2, or confirms {@code fallbackOption} when only one is available. */
    protected void choosePassReward(boolean bothAvailable, int fallbackOption, Action0 firstReward, Action0 secondReward) {
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
        if (bothAvailable) {
            ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_REWARD);
            ServicePlatform.get().getEncounterService().displayButtons(
                    getTextBuilder().withPass().withOption(1).build(),
                    getTextBuilder().withPass().withOption(2).build()
            ).subscribe(button -> {
                if (button == 0) {
                    confirmReward(1, firstReward);
                } else {
                    confirmReward(2, secondReward);
                }
            });
        } else {
            confirmReward(fallbackOption, fallbackOption == 1 ? firstReward : secondReward);
        }
    }

    private void confirmReward(int option, Action0 gainReward) {
        ServicePlatform.get().getEncounterService().hold();
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withOption(option).withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withPass().withOption(option).build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            gainReward.call();
            ServicePlatform.get().getEncounterService().release();
        });
        ServicePlatform.get().getEncounterService().release();
    }


}
