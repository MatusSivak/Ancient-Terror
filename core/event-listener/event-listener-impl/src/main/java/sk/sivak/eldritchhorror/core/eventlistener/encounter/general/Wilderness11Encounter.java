package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class Wilderness11Encounter extends AbstractGeneralEncounter {

    public Wilderness11Encounter() {
        super(11, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(),
                () -> finishAfterFlavor(getTextBuilder().withFail().withFlavor().build()), this::onYes);
    }

    private void onYes() {
        if (ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed()) {
            onAlreadyDelayed();
            return;
        }
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
        boolean hasMadness = ServicePlatform.get().getConditionsDeck().hasTrait(
                ServicePlatform.get().getInvestigators().getActiveInvestigatorId(),
                ConditionTrait.MADNESS);
        if (hasMadness) {
            onHasMadness();
        } else {
            confirmRecovery(1, true);
        }
    }

    private void onAlreadyDelayed() {
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor(2).build());
        TypewriterUtils.confirmInfos("[#BAD]You are already Delayed[]").subscribe(() ->
                ServicePlatform.get().getEncounterService().finishTypewriterPaper());
    }

    private void onHasMadness() {
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_REWARD);
        ServicePlatform.get().getEncounterService().displayButtons(
                getTextBuilder().withOption(1).build(),
                getTextBuilder().withOption(2).build()
        ).subscribe(x -> confirmRecovery(x + 1, false));
    }

    private void confirmRecovery(int option, boolean onlyAvailableReward) {
        ServicePlatform.get().getEncounterService().hold();
        String flavor = onlyAvailableReward
                ? getTextBuilder().withOption(option).withFlavor(2).build()
                : getTextBuilder().withOption(option).withFlavor().build();
        ServicePlatform.get().getEncounterService().typeFlavor(flavor);
        TypewriterUtils.confirmInfos(
                getTextBuilder().withPass().withInfo().build(),
                getTextBuilder().withOption(option).build()
        ).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), false));
            if (option == 1) {
                ServicePlatform.get().getTokenService().gainSanity(3);
            } else {
                selectMadnessToDiscard();
            }
            ServicePlatform.get().getEncounterService().release();
        });
        ServicePlatform.get().getEncounterService().release();
    }

    private void selectMadnessToDiscard() {
        List<ConditionInfo> madnessConditions = ServicePlatform.get().getConditionsDeck()
                .getCondition(getActiveInvestigatorId(), ConditionTrait.MADNESS);
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(madnessConditions);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Madness Condition");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }
}
